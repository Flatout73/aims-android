package net.styleru.aims;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

/**
 * Created by LeonidL on 24.12.16.
 */

public class DelayAutoCompleteTextView extends AutoCompleteTextView {

    private static final int MESSAGE_TEXT_CHANGED = 100;
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 750;

    private int mAutoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;
    private ProgressBar mLoadingIndicator;

    public DelayAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DelayAutoCompleteTextView.super.performFiltering((CharSequence)msg.obj, msg.arg1);
        }
    };

    public void setmLoadingIndicator(ProgressBar mLoadingIndicator) {
        this.mLoadingIndicator = mLoadingIndicator;
    }

    public void setmAutoCompleteDelay(int mAutoCompleteDelay) {
        this.mAutoCompleteDelay = mAutoCompleteDelay;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if(mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(VISIBLE);
        }
        handler.removeMessages(MESSAGE_TEXT_CHANGED);
        handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_TEXT_CHANGED, text), mAutoCompleteDelay);
    }

    @Override
    public void onFilterComplete(int count) {
        if(mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(GONE);
        }
        super.onFilterComplete(count);
    }
}
