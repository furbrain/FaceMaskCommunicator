package uk.underwood_underground.face_mask_communicator;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import androidx.core.util.Supplier;

import java.util.ArrayList;

public class SRListener implements RecognitionListener {
    private final SpeechRecognizer speechRecognizer;
    private final SerialInputConnection inputConnection;
    private final Intent speechRecognizerIntent;
    private final Supplier<Boolean> continueListening;

    public SRListener(SerialInputConnection inputConnection, Intent speechRecognizerIntent, SpeechRecognizer speechRecognizer, Supplier<Boolean> continueListening) {
        this.continueListening = continueListening;
        this.speechRecognizer = speechRecognizer;
        this.inputConnection = inputConnection;
        this.speechRecognizerIntent = speechRecognizerIntent;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {
        if (continueListening.get()) speechRecognizer.startListening(speechRecognizerIntent);
    }

    @Override
    public void onResults(Bundle bundle) {
        ArrayList<String> texts = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        inputConnection.commitText(texts.get(0) + " ", 1);
        if (continueListening.get()) speechRecognizer.startListening(speechRecognizerIntent);
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        ArrayList<String> texts = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        inputConnection.setComposingText(texts.get(0), 1);
    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }
}
