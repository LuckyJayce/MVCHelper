package com.shizhefei.task;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

/**
 * task执行类
 * 
 * @author LuckyJayce
 *
 * @param <SUCCESS>
 *            成功的数据类型
 * @param <FAIL>
 *            失败的数据类型
 */
public class TaskHelper<SUCCESS, FAIL> {

	private Task<SUCCESS, FAIL> task;

	private Callback<SUCCESS, FAIL> callback;

	private AsyncTask<Void, Object, Data<SUCCESS, FAIL>> asyncTask;

	public TaskHelper() {
		super();
	}

	public TaskHelper(Task<SUCCESS, FAIL> task, Callback<SUCCESS, FAIL> callback) {
		super();
		this.task = task;
		this.callback = callback;
	}

	public Task<SUCCESS, FAIL> getTask() {
		return task;
	}

	public void setTask(Task<SUCCESS, FAIL> task) {
		this.task = task;
	}

	public Callback<SUCCESS, FAIL> getCallback() {
		return (Callback<SUCCESS, FAIL>) callback;
	}

	public void setCallback(Callback<SUCCESS, FAIL> callback) {
		this.callback = callback;
	}

	public void cancle() {
		if (asyncTask != null && asyncTask.getStatus() != AsyncTask.Status.FINISHED) {
			task.cancle();
			asyncTask.cancel(true);
		}
	}

	public void execute() {
		execute(true);
	}

	public boolean isRunning() {
		if (asyncTask == null) {
			return false;
		}
		if (asyncTask.getStatus() == AsyncTask.Status.FINISHED) {
			return false;
		}
		return true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void execute(boolean canclePre) {
		if (!canclePre) {
			if (asyncTask != null && asyncTask.getStatus() != AsyncTask.Status.FINISHED) {
				return;
			}
		} else {
			if (asyncTask != null) {
				asyncTask.cancel(true);
			}
		}
		asyncTask = new AsyncTask<Void, Object, Data<SUCCESS, FAIL>>() {

			private volatile Exception e;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				callback.onPreExecute();
			}

			@Override
			protected Data<SUCCESS, FAIL> doInBackground(Void... params) {
				try {
					ProgressSender progressSender = new ProgressSender() {

						@Override
						public void send(long current, long total, Object exraData) {
							publishProgress(current, total, exraData);
						}
					};
					return task.execute(progressSender);
				} catch (Exception e) {
					e.printStackTrace();
					this.e = e;
				}
				return null;
			}

			@Override
			protected void onProgressUpdate(Object... values) {
				super.onProgressUpdate(values);
				Long current = (Long) values[0];
				Long total = (Long) values[1];
				Object exraData = values[2];
				int percent = 0;
				if (current == 0) {
					percent = 0;
				} else if (total == 0) {
					percent = 0;
				} else {
					percent = (int) (100 * current / total);
				}
				callback.onProgressUpdate(percent, current, total, exraData);
			}

			@Override
			protected void onPostExecute(Data<SUCCESS, FAIL> result) {
				super.onPostExecute(result);
				if (this.e != null) {
					callback.onPostExecute(Code.EXCEPTION, this.e, null, null);
				} else if (result == null) {
					callback.onPostExecute(Code.FAIL, null, null, null);
				} else if (result.isSuccess()) {
					callback.onPostExecute(Code.SUCESS, null, result.getSuccess(), null);
				} else {
					callback.onPostExecute(Code.FAIL, null, null, result.getFail());
				}
			}

			@Override
			protected void onCancelled() {
				callback.onPostExecute(Code.CANCLE, null, null, null);
				super.onCancelled();
			}
		};

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			asyncTask.execute();
		}
	}

	public void destory() {
		cancle();
	}
}
