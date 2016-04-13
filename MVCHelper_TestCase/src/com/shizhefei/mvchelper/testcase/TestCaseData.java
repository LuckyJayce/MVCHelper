package com.shizhefei.mvchelper.testcase;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.IDataSource;
import com.shizhefei.task.AsyncDataSourceProxyTask;
import com.shizhefei.task.DataSourceProxyTask;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.Task;

public class TestCaseData {
	String text;
	String result;
	Object task;
	int status;

	public TestCaseData(String text, Task task) {
		super();
		this.text = text;
		this.task = task;
	}

	public TestCaseData(String text, IAsyncTask asyncTask) {
		super();
		this.text = text;
		this.task = asyncTask;
	}

	public TestCaseData(String text, IDataSource datasource) {
		super();
		this.text = text;
		this.task = new DataSourceProxyTask(datasource);
	}

	public TestCaseData(String text, IAsyncDataSource datasource) {
		super();
		this.text = text;
		this.task = new AsyncDataSourceProxyTask(datasource);
	}
}
