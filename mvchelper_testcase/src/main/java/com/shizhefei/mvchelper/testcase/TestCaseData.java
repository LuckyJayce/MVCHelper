package com.shizhefei.mvchelper.testcase;

import android.view.View;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.IDataSource;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.ITask;

import java.util.HashMap;
import java.util.Map;

public class TestCaseData {
    String text;
    String result;
    Object task;
    int status;

    public TestCaseData(String text, ITask task) {
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
        this.task = datasource;
    }

    public TestCaseData(String text, IAsyncDataSource datasource) {
        super();
        this.text = text;
        this.task = datasource;
    }

    public void addParamGet(String paramName, IAsyncTask<String> paramGetTask) {
        paramGets.put(paramName, paramGetTask);
    }

    public void addParamGet(String[] paramNames, IAsyncTask<Map<String, String>> paramGetTask) {
        paramGetsMap.put(paramNames, paramGetTask);
    }

    interface ParamGet {
        public String get();
    }

    Map<String, IAsyncTask<String>> paramGets = new HashMap<>();

    Map<String[], IAsyncTask<Map<String, String>>> paramGetsMap = new HashMap<>();

    public interface IParamValuesNotify {
        public void notifyCurrentParamValues(Map<String, Object> currentParamValue, View button);
    }
}
