package com.shizhefei.test.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import com.shizhefei.view.mvc.demo.R;

/**
 * Created by LuckyJayce on 2017/7/22.
 */

public class SetExceptionView extends FrameLayout {
    private EditText editText;
    private ToggleButton toggleButton;

    public SetExceptionView(Context context) {
        super(context);
        init();
    }

    public SetExceptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SetExceptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        try {
            View.inflate(getContext(), R.layout.layout_setexception, this);
            toggleButton = (ToggleButton) findViewById(R.id.exception_toggleButton);
            editText = (EditText) findViewById(R.id.exception_editText);

            editText.setVisibility(View.INVISIBLE);
            toggleButton.setOnCheckedChangeListener(onCheckedChangeListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDefinedException() {
        if (toggleButton.isChecked()) {
            return "自定义异常："+editText.getText().toString();
        }
        return null;
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                editText.setText("");
                editText.setVisibility(View.VISIBLE);
            } else {
                editText.setVisibility(View.INVISIBLE);
            }
        }
    };
}
