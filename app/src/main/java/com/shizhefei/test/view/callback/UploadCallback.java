package com.shizhefei.test.view.callback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.shizhefei.task.Code;
import com.shizhefei.task.ICallback;
import com.shizhefei.task.TaskHelper;

public class UploadCallback implements ICallback<String> {

    private final boolean canceledOnTouchOutside;
    private final TaskHelper taskHelper;
    private final Activity activity;
    private AlertDialog alertDialog;
    private Context context;

    public UploadCallback(final TaskHelper taskHelper, Activity activity, boolean canceledOnTouchOutside) {
        this.taskHelper = taskHelper;
        this.activity = activity;
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }

    @Override
    public void onPreExecute(final Object task) {
        context = activity.getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("上传文件中");
        builder.setMessage("请等待 ");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                taskHelper.cancel(task);
            }
        });
        alertDialog = builder.create();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                taskHelper.cancel(task);
            }
        });
        alertDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        alertDialog.show();
    }

    @Override
    public void onProgress(Object task, int percent, long current, long total, Object extraData) {
        alertDialog.setMessage(current + "/" + total + "\n" + "%" + percent + "\n" + extraData);
    }

    @Override
    public void onPostExecute(Object task, Code code, Exception exception, String success) {
        alertDialog.dismiss();
        switch (code) {
            case SUCCESS:
                Toast.makeText(context, success, Toast.LENGTH_SHORT).show();
                break;
            case CANCEL:
                Toast.makeText(context, "您取消了操作", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}