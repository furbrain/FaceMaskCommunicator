package uk.underwood_underground.face_mask_communicator;

import android.os.Bundle;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputContentInfo;

import org.apache.commons.lang3.StringUtils;

public class SerialInputConnection extends InputConnectionWrapper {
    private final SerialTextView textView;
    private String buffer = "";
    public SerialInputConnection(InputConnection target, SerialTextView tv) {
        super(target, true);
        textView = tv;
    }

    private void updateBuffer(String text) {
        // find first difference
        String diff = StringUtils.difference(buffer, text);
        int kept_length = text.length() - diff.length();
        int delete_length = buffer.length() - kept_length;
        //send sufficient delete characters - but we'll use "#" for now...
        if (delete_length>0) {
            textView.send(StringUtils.repeat('\b', delete_length));
        }
        textView.send(diff);
        buffer = text;
    }

    private void clearBuffer() {
        buffer = "";
        //textView.setText("");
        //CharSequence currentText = getExtractedText(new ExtractedTextRequest(), 0).text;
        //CharSequence beforeCursorText = getTextBeforeCursor(currentText.length(), 0);
        //CharSequence afterCursorText = getTextAfterCursor(currentText.length(), 0);
        //deleteSurroundingText(beforeCursorText.length(), afterCursorText.length());
    }

    @Override
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        boolean result = super.setComposingText(text, newCursorPosition);
        updateBuffer(text.toString());
        return result;
    }

    @Override
    public boolean setComposingRegion(int start, int end) {
        return super.setComposingRegion(start, end);
    }

    @Override
    public boolean finishComposingText() {
        boolean result = super.finishComposingText();
        clearBuffer();
        return result;
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
        boolean result = super.commitText(text, newCursorPosition);
        updateBuffer(text.toString());
        clearBuffer();
        return result;
    }

    @Override
    public boolean commitCompletion(CompletionInfo text) {
        return super.commitCompletion(text);
    }

    @Override
    public boolean commitCorrection(CorrectionInfo correctionInfo) {
        return super.commitCorrection(correctionInfo);
    }

    @Override
    public boolean commitContent(InputContentInfo inputContentInfo, int flags, Bundle opts) {
        return super.commitContent(inputContentInfo, flags, opts);
    }
}
