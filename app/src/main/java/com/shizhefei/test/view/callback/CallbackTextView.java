package com.shizhefei.test.view.callback;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shizhefei.task.Code;
import com.shizhefei.task.ICallback;

/**
 * Created by luckyjayce on 2017/4/16.
 */

public class CallbackTextView extends TextView implements ICallback<Object> {

    public CallbackTextView(Context context) {
        super(context);
    }

    public CallbackTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CallbackTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onPreExecute(Object task) {
        setText("onPreExecute task:"+task);
    }

    @Override
    public void onProgress(Object task, int percent, long current, long total, Object extraData) {
        append("\nonProgress:" + task + " current:" + current + " total:" + total+" extraData:"+extraData);
    }

    @Override
    public void onPostExecute(Object task, Code code, Exception exception, Object data) {
        append("\n");
        append("code:" + code);
        append("\n");
        if (code == Code.SUCCESS) {
            append(new Gson().toJson(data));
        } else if (code == Code.EXCEPTION) {
            append(exception.getMessage());
        }
    }
}
