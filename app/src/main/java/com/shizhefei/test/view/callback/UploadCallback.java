package com.shizhefei.test.view.callback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.shizhefei.task.Callback;
import com.shizhefei.task.Code;
import com.shizhefei.task.TaskHelper;

public class UploadCallback implements Callback<String, String> {

	private AlertDialog alertDialog;
	private Context context;

	public UploadCallback(final TaskHelper<String, String> taskHelper, Activity activity, boolean canceledOnTouchOutside) {
		context = activity.getApplicationContext();
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("上传文件中");
		builder.setMessage("请等待 ");
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				taskHelper.cancle();
			}
		});
		alertDialog = builder.create();
		alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				taskHelper.cancle();
			}
		});
		alertDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
	}

	@Override
	public void onPreExecute() {
		alertDialog.show();
	}

	@Override
	public void onProgressUpdate(int percent, long current, long total, Object exraData) {
		alertDialog.setMessage(current + "/" + total + "\n" + "%" + percent + "\n" + exraData);
	}

	@Override
	public void onPostExecute(Code code, Exception exception, String success, String fail) {
		alertDialog.dismiss();
		switch (code) {
		case SUCESS:
			Toast.makeText(context, success, 1).show();
			break;
		case CANCLE:
			Toast.makeText(context, "您取消了操作", 1).show();
			break;
		default:
			break;
		}
	}

}