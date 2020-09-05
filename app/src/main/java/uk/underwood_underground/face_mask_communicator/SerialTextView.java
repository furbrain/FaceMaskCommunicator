package uk.underwood_underground.face_mask_communicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class SerialTextView extends androidx.appcompat.widget.AppCompatEditText {
    private TerminalFragment fragment;

    public SerialTextView(Context context) {
        super(context);
    }

    public SerialTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SerialTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection ic = super.onCreateInputConnection(outAttrs);
        return new SerialInputConnection(ic, this);
    }

    public void setFragment(TerminalFragment fragment) {
        this.fragment = fragment;
    }

    public void send(String text) {
        fragment.send(text);
    }
}
