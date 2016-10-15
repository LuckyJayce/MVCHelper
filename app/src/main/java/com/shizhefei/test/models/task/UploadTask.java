package com.shizhefei.test.models.task;


import com.shizhefei.mvc.ProgressSender;
import com.shizhefei.task.ITask;

public class UploadTask implements ITask<String> {

    private boolean isCancle;

    @Override
    public String execute(ProgressSender progressSender) throws Exception {
        int total = 864;
        for (int i = 1; i <= total && !isCancle; i++) {
            Thread.sleep(20);
            progressSender.sendProgress(i, total, "正在上传文件中...");
        }
        return "文件上传成功";
    }

    @Override
    public void cancel() {
        isCancle = true;
    }

}