package com.shizhefei.test.controllers.task;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shizhefei.task.Code;
import com.shizhefei.task.ICallback;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.test.models.enties.User;
import com.shizhefei.test.models.exception.BizException;
import com.shizhefei.test.models.task.LoginAsyncTask;
import com.shizhefei.test.models.task.UploadTask;
import com.shizhefei.test.view.callback.UploadCallback;
import com.shizhefei.view.mvc.demo.R;

public class LoginActivity extends Activity {
    private EditText nameEditText;
    private EditText pwEditText;
    private Button loginButton;
    private TaskHelper<Object> taskHelper;
    private TextView textView;
    private View uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        nameEditText = (EditText) findViewById(R.id.editText1);
        pwEditText = (EditText) findViewById(R.id.editText2);
        loginButton = (Button) findViewById(R.id.button1);
        textView = (TextView) findViewById(R.id.textView2);
        uploadButton = findViewById(R.id.upload_button);

        loginButton.setOnClickListener(onClickListener);
        uploadButton.setOnClickListener(onClickListener);

        taskHelper = new TaskHelper<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskHelper.destroy();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == loginButton) {
                String name = nameEditText.getText().toString();
                String password = pwEditText.getText().toString();
                taskHelper.execute(new LoginAsyncTask(name, password), loginCallback);
            } else if (v == uploadButton) {
                taskHelper.execute(new UploadTask(), new UploadCallback(taskHelper, LoginActivity.this, false));
            }
        }
    };

    private ICallback<User> loginCallback = new ICallback<User>() {
        @Override
        public void onPreExecute(Object task) {
            loginButton.setEnabled(false);
            loginButton.setText("登陆中...");
        }

        @Override
        public void onProgress(Object task, int percent, long current, long total, Object extraData) {

        }

        @Override
        public void onPostExecute(Object task, Code code, Exception exception, User user) {
            loginButton.setEnabled(true);
            loginButton.setText("登陆");
            switch (code) {
                case EXCEPTION:
                    if (exception instanceof BizException) {
                        BizException bizException = (BizException) exception;
                        String fail = bizException.getMessage();
                        Toast.makeText(getApplicationContext(), fail, Toast.LENGTH_SHORT).show();
                        textView.setText(fail);
                    } else {
                        Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                        textView.setText("网络连接失败");
                    }
                    break;
                case SUCCESS:
                    Toast.makeText(getApplicationContext(), "登陆成功：" + new Gson().toJson(user), Toast.LENGTH_LONG).show();
                    textView.setText("登陆成功：" + new Gson().toJson(user));
                    break;
                default:
                    break;
            }
        }

    };
}
