package com.shizhefei.test.controllers.task;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shizhefei.task.Callback;
import com.shizhefei.task.Code;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.test.models.enties.User;
import com.shizhefei.test.models.task.LoginAsyncTask;
import com.shizhefei.view.mvc.demo.R;

public class LoginActivity extends Activity {
	private EditText nameEditText;
	private EditText pwEditText;
	private Button loginButton;
	private TaskHelper<User, String> loginHelper;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		nameEditText = (EditText) findViewById(R.id.editText1);
		pwEditText = (EditText) findViewById(R.id.editText2);
		loginButton = (Button) findViewById(R.id.button1);
		textView = (TextView) findViewById(R.id.textView2);
		loginButton.setOnClickListener(onClickListener);
		loginHelper = new TaskHelper<User, String>();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		loginHelper.destory();
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == loginButton) {
				String name = nameEditText.getText().toString();
				String password = pwEditText.getText().toString();
				//loginHelper.setTask(new LoginTask(name, password));
				loginHelper.setTask(new LoginAsyncTask(name, password));
				loginHelper.setCallback(loginCallback);
				loginHelper.execute();
			}
		}
	};

	private Callback<User, String> loginCallback = new Callback<User, String>() {
		@Override
		public void onPreExecute() {
			loginButton.setEnabled(false);
			loginButton.setText("登陆中...");
		}

		@Override
		public void onProgressUpdate(int percent, long current, long total, Object exraData) {

		}

		@Override
		public void onPostExecute(Code code, Exception exception, User success, String fail) {
			loginButton.setEnabled(true);
			loginButton.setText("登陆");
			switch (code) {
			case FAIL:
			case EXCEPTION:
				if (TextUtils.isEmpty(fail)) {
					Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
					textView.setText("网络连接失败");
				} else {
					Toast.makeText(getApplicationContext(), fail, Toast.LENGTH_SHORT).show();
					textView.setText(fail);
				}
				break;
			case SUCESS:
				Toast.makeText(getApplicationContext(), "登陆成功：" + new Gson().toJson(success), Toast.LENGTH_LONG).show();
				textView.setText("登陆成功：" + new Gson().toJson(success));
				break;
			default:
				break;
			}
		}
	};
}
