package com.shizhefei.mvchelper.testcase;

import java.util.HashMap;
import java.util.Map;

import android.view.View;

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

	public void addParamGet(String paramName, IAsyncTask<String, String> paramGetTask) {
		paramGets.put(paramName, paramGetTask);
	}

	public void addParamGet(String[] paramNames, IAsyncTask<Map<String, String>, String> paramGetTask) {
		paramGetsMap.put(paramNames, paramGetTask);
	}

	interface ParamGet {
		public String get();
	}

	Map<String, IAsyncTask<String, String>> paramGets = new HashMap<String, IAsyncTask<String, String>>();

	Map<String[], IAsyncTask<Map<String, String>, String>> paramGetsMap = new HashMap<String[], IAsyncTask<Map<String, String>, String>>();

	public interface IParamValuesNotify {
		public void notifyCurrentParamValues(Map<String, Object> currentParamValue, View button);
	}
}
