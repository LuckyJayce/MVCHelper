package com.shizhefei.task;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.ProgressSender;
import com.shizhefei.task.ResponseSender;

public class AsyncDataSourceProxyTask<DATA> implements IAsyncTask<DATA, Void> {
	private IAsyncDataSource<DATA> datasource;

	public AsyncDataSourceProxyTask(IAsyncDataSource<DATA> datasource) {
		super();
		this.datasource = datasource;
	}

	public IAsyncDataSource<DATA> getDatasource() {
		return datasource;
	}

	@Override
	public RequestHandle execute(final ResponseSender<DATA, Void> sender, ProgressSender progressSender) throws Exception {
		com.shizhefei.mvc.ResponseSender<DATA> responseSender = new com.shizhefei.mvc.ResponseSender<DATA>() {
			@Override
			public void sendError(Exception exception) {
				sender.sendError(exception);
			}

			@Override
			public void sendData(DATA data) {
				sender.sendData(data);
			}
		};
		return datasource.refresh(responseSender);
	}
}
