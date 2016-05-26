package com.shizhefei.test.models.task;

import com.shizhefei.task.Data;
import com.shizhefei.task.ProgressSender;
import com.shizhefei.task.Task;

public class UploadTask implements Task<String, String> {

	private volatile boolean isCancle;

	@Override
	public Data<String, String> execute(ProgressSender progressSender) throws Exception {
		int total = 864;
		for (int i = 1; i <= total && !isCancle; i++) {
			Thread.sleep(20);
			progressSender.send(i, total, "正在上传文件中...");
		}
		return Data.madeSuccess("文件上传成功");
	}

	@Override
	public void cancle() {
		isCancle = true;
	}

}