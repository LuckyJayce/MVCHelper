package com.shizhefei.task;

import com.shizhefei.mvc.RequestHandle;

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

	private SuperTask<SUCCESS, FAIL> task;

	private Callback<SUCCESS, FAIL> callback;

	private MyTask<SUCCESS, FAIL> myTask;

	private MyAsyncTask<SUCCESS, FAIL> myAsyncTask;

	public TaskHelper() {
		super();
	}

	public TaskHelper(Task<SUCCESS, FAIL> task, Callback<SUCCESS, FAIL> callback) {
		super();
		this.task = task;
		this.callback = callback;
	}

	public TaskHelper(IAsyncTask<SUCCESS, FAIL> task, Callback<SUCCESS, FAIL> callback) {
		super();
		this.task = task;
		this.callback = callback;
	}

	public void setTask(Task<SUCCESS, FAIL> task) {
		this.task = task;
	}

	public void setTask(IAsyncTask<SUCCESS, FAIL> task) {
		this.task = task;
	}

	public Callback<SUCCESS, FAIL> getCallback() {
		return callback;
	}

	public void setCallback(Callback<SUCCESS, FAIL> callback) {
		this.callback = callback;
	}

	public void cancle() {
		if (myTask != null && myTask.getStatus() != AsyncTask.Status.FINISHED) {
			myTask.cancleMyTask();
		}
		if (myAsyncTask != null && myAsyncTask.isRunning()) {
			myAsyncTask.cancle();
		}
		myTask = null;
		myAsyncTask = null;
	}

	public void execute() {
		execute(true);
	}

	public boolean isRunning() {
		if (myTask == null) {
			return false;
		}
		if (myTask.getStatus() == AsyncTask.Status.FINISHED) {
			return false;
		}
		return true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void execute(boolean canclePre) {
		if (!canclePre) {
			if (myTask != null && myTask.getStatus() != AsyncTask.Status.FINISHED) {
				return;
			}
			if (myAsyncTask != null && myAsyncTask.isRunning()) {
				return;
			}
		} else {
			cancle();
		}
		if (task instanceof Task) {
			myTask = new MyTask<SUCCESS, FAIL>(callback, (Task<SUCCESS, FAIL>) task);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				myTask.execute();
			}
		} else if (task instanceof IAsyncTask) {
			IAsyncTask<SUCCESS, FAIL> asyncTask = (IAsyncTask<SUCCESS, FAIL>) task;
			myAsyncTask = new MyAsyncTask<SUCCESS, FAIL>(callback, asyncTask);
			myAsyncTask.execute();
		}
	}

	public void destory() {
		cancle();
	}

	private static class MyAsyncTask<SUCCESS, FAIL> implements ResponseSender<SUCCESS, FAIL>, ProgressSender {
		private RequestHandle handle;
		private boolean isCancle = false;
		private Callback<SUCCESS, FAIL> callback;
		private IAsyncTask<SUCCESS, FAIL> task;
		private boolean isRunning;

		public MyAsyncTask(Callback<SUCCESS, FAIL> callback, IAsyncTask<SUCCESS, FAIL> task) {
			super();
			this.callback = callback;
			this.task = task;
			isCancle = false;
			isRunning = true;
		}

		@Override
		public final void sendError(Exception exception) {
			onPostExecute(Code.EXCEPTION, exception, null, null);
		}

		@Override
		public final void sendData(SUCCESS success) {
			if (success != null) {
				callback.onPostExecute(Code.SUCESS, null, success, null);
			} else {
				callback.onPostExecute(Code.FAIL, null, null, null);
			}
		}

		public boolean isRunning() {
			return isRunning;
		}

		public void cancle() {
			if (handle != null) {
				handle.cancle();
				handle = null;
			}
			isCancle = true;
			if (isRunning) {
				callback.onPostExecute(Code.CANCLE, null, null, null);
			}
			isRunning = false;
		}

		public RequestHandle execute() {
			callback.onPreExecute();
			try {
				return handle = task.execute(this, this);
			} catch (Exception e) {
				e.printStackTrace();
				onPostExecute(Code.EXCEPTION, e, null, null);
			}
			return null;
		}

		@Override
		public void sendFail(FAIL data) {
			onPostExecute(Code.FAIL, null, null, data);
		}

		@Override
		public void send(long current, long total, Object exraData) {
			int percent = 0;
			if (current == 0) {
				percent = 0;
			} else if (total == 0) {
				percent = 0;
			} else {
				percent = (int) (100 * current / total);
			}
			if (!isCancle) {
				callback.onProgressUpdate(percent, current, total, exraData);
			}
		}

		public void onPostExecute(Code code, Exception exception, SUCCESS success, FAIL fail) {
			if (!isCancle) {
				callback.onPostExecute(code, exception, success, fail);
			}
			isRunning = false;
		}

	}

	private static class MyTask<SUCCESS, FAIL> extends AsyncTask<Void, Object, Data<SUCCESS, FAIL>> {

		private volatile Exception e;
		private Callback<SUCCESS, FAIL> callback;
		private Task<SUCCESS, FAIL> task;

		public MyTask(Callback<SUCCESS, FAIL> callback, Task<SUCCESS, FAIL> task) {
			super();
			this.callback = callback;
			this.task = task;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			callback.onPreExecute();
		}

		public void cancleMyTask() {
			task.cancle();
			cancel(true);
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
}
