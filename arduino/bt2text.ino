#include <Adafruit_GFX.h>
#include <FastLED_NeoMatrix.h>
#include <FastLED.h>
#include "BluetoothSerial.h"


#ifndef PSTR
#define PSTR // Make Arduino Due happy
#endif

#define PIN 16

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BluetoothSerial SerialBT;

#define MATRIX_WIDTH 16
#define MATRIX_HEIGHT 8
#define LED_COUNT MATRIX_WIDTH * MATRIX_HEIGHT

CRGB matrixleds[LED_COUNT]; 

FastLED_NeoMatrix *matrix = new FastLED_NeoMatrix(matrixleds, MATRIX_WIDTH, MATRIX_HEIGHT,
 NEO_MATRIX_TOP     + NEO_MATRIX_LEFT +
 NEO_MATRIX_COLUMNS + NEO_MATRIX_ZIGZAG);
const uint16_t colors[] = {
 matrix->Color(255, 0, 0), matrix->Color(0, 255, 0), matrix->Color(0, 0, 255) };

String printText; //create global printText variable
int speed = 36; //in characters per 10 seconds
int led_delay;
int brightness = 20;

void setup() {
 pinMode(LED_BUILTIN, OUTPUT);
 Serial.begin(115200);
 SerialBT.begin("FaceMask"); //Bluetooth device name
 Serial.println("Ready");
 FastLED.addLeds<NEOPIXEL, PIN>(matrixleds, LED_COUNT);
 matrix->begin();
 matrix->setTextWrap(false);
 matrix->setBrightness(brightness);
 matrix->setTextColor(colors[0]);
 printText = String("Ready");
 set_delay(); 

}

void set_delay() {
  led_delay = 1250/speed;
}

int x = matrix->width();
int pass = 0;
int current_letter_width = 0;
int led_state = 1;

int get_char_width(char c) {
  int16_t temp1;
  uint16_t temp2;
  uint16_t result;
  String ch = String(c);
  matrix->getTextBounds(ch, 0, 0, &temp1, &temp1, &result, &temp2);
  return result;
}

void loop()
{
 char c;
 digitalWrite(LED_BUILTIN, led_state);
 led_state ^= 1;
 if (SerialBT.available())
 {
   // append to end of string. readString waits for a timeout, so just use read()
   if (printText.length()==0) {
    x = matrix->width();
   }
   c = SerialBT.read();
   // we can put our control code here eg <>[]
   String response = "";
   switch (c) {
    case '<': 
      speed -= 2.0;
      if (speed < 4) speed = 4;
      set_delay();
      response = String("Speed ") + speed;
      break;
    case '>': 
      speed += 2.0;
      if (speed > 80) speed = 80;
      set_delay();
      response = String("Speed ") + speed;
      break;
    case '[': 
      brightness -= 10;
      if (brightness < 10) brightness = 10;
      matrix->setBrightness(brightness);
      response = String("Brightness ") + brightness;
      break;
    case ']': 
      brightness += 10;
      if (brightness > 255) brightness = 255;
      matrix->setBrightness(brightness);
      response = String("Brightness ") + brightness;
      break;
    case '\b':
      if (printText.length() > 0) {
        printText.remove(printText.length()-1, 1);
        Serial.write(c);
      }
      break;
    default:
      printText.concat(c);
      Serial.write(c);
      break;
   }
   if (response.length()>0) {
     printText = response;
     for (int i=0; i< response.length(); i++) {
       SerialBT.write(response[i]);
       Serial.write(response[i]);
       }
     SerialBT.write('\n');
   }
   current_letter_width = get_char_width(printText[0]);
 }
 //if old print text != print text, set x to whatever
 matrix->fillScreen(0);
 if (printText.length()>0) {
   matrix->setCursor(x, 0);
   matrix->print(printText);
   if(--x < -current_letter_width)
   {
     printText = printText.substring(1);
     current_letter_width = get_char_width(printText[0]);
     x = 0;
     //if(++pass >= 3) pass = 0;
     //matrix.setTextColor(colors[pass]);
     matrix->setTextColor(colors[0]);
   }
 }
 matrix->show();  
 delay(led_delay);
}
