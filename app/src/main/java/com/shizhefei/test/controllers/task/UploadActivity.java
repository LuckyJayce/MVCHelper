package com.shizhefei.test.controllers.task;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.shizhefei.task.TaskHelper;
import com.shizhefei.test.models.task.UploadTask;
import com.shizhefei.test.view.callback.UploadCallback;
import com.shizhefei.view.mvc.demo.R;

public class UploadActivity extends Activity {

	private TaskHelper<String, String> taskHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		taskHelper = new TaskHelper<String, String>();
		findViewById(R.id.button1).setOnClickListener(onClickListener);

	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			taskHelper.setTask(new UploadTask());
			taskHelper.setCallback(new UploadCallback(taskHelper, UploadActivity.this, false));
			taskHelper.execute();
		}
	};

}
