package com.shizhefei.test.controllers.task;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shizhefei.task.Code;
import com.shizhefei.task.TaskHandle;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.task.imp.SimpleCallback;
import com.shizhefei.test.models.datasource.BooksOkHttp_AsyncDataSource;
import com.shizhefei.test.models.datasource.BooksOkHttp_SyncDataSource;
import com.shizhefei.test.models.enties.Book;
import com.shizhefei.test.models.enties.User;
import com.shizhefei.test.models.task.LoginAsyncTask;
import com.shizhefei.test.models.task.LoginTask;
import com.shizhefei.view.mvc.demo.R;

import java.util.List;


public class TaskDemoActivity extends Activity {

    private View asyncDataSourceButton;
    private View taskButton;
    private View asyncTaskButton;
    private View dataSourceButton;
    private TaskHelper<Object> taskHelper;
    private TextView resultTextView;
    private TextView result2TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_demo);
        asyncDataSourceButton = findViewById(R.id.taskdemo_iasyncdatasoruce_button);
        taskButton = findViewById(R.id.taskdemo_itask_button);
        asyncTaskButton = findViewById(R.id.taskdemo_iasynctask_button);
        dataSourceButton = findViewById(R.id.taskdemo_idatasoruce_button);
        resultTextView = (TextView) findViewById(R.id.taskdemo_result_textView);
        result2TextView = (TextView) findViewById(R.id.taskdemo_result2_textView);

        asyncDataSourceButton.setOnClickListener(onClickListener);
        taskButton.setOnClickListener(onClickListener);
        asyncTaskButton.setOnClickListener(onClickListener);
        dataSourceButton.setOnClickListener(onClickListener);

        taskHelper = new TaskHelper<>();

        taskHelper.registerCallBack(new SimpleCallback<Object>() {
            @Override
            public void onPreExecute(Object task) {
                super.onPreExecute(task);
                result2TextView.setText("开始执行:" + task.getClass().getSimpleName());
            }

            @Override
            public void onPostExecute(Object task, Code code, Exception exception, Object data) {
                result2TextView.append("\n");
                result2TextView.append("code:" + code);
                result2TextView.append("\n");
                if (code == Code.SUCCESS) {
                    result2TextView.append(new Gson().toJson(data));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消全部
        taskHelper.destroy();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public TaskHandle dataSourceTaskHandle;
        public TaskHandle asyncDataSourceTaskHandle;
        public TaskHandle taskHandle;
        public TaskHandle asyncTaskHandle;

        @Override
        public void onClick(View v) {
            //取消全部
//            taskHelper.cancelAll();

            //单独取消某个task
            if (dataSourceTaskHandle != null) {
                dataSourceTaskHandle.cancle();
            }
            if (asyncDataSourceTaskHandle != null) {
                asyncDataSourceTaskHandle.cancle();
            }
            if (asyncTaskHandle != null) {
                asyncTaskHandle.cancle();
            }
            if (taskHandle != null) {
                taskHandle.cancle();
            }

            if (v == asyncDataSourceButton) {
                asyncDataSourceTaskHandle = taskHelper.execute(new BooksOkHttp_AsyncDataSource(), new SimpleCallback<List<Book>>() {
                    @Override
                    public void onPreExecute(Object task) {
                        super.onPreExecute(task);
                        resultTextView.setText("正在执行asyncDataSourceButton");
                    }

                    @Override
                    public void onPostExecute(Object task, Code code, Exception exception, List<Book> books) {
                        resultTextView.append("\n");
                        resultTextView.append("code:" + code);
                        resultTextView.append("\n");
                        if (code == Code.SUCCESS) {
                            resultTextView.append(new Gson().toJson(books));
                        }
                    }
                });
            } else if (v == dataSourceButton) {
                dataSourceTaskHandle = taskHelper.execute(new BooksOkHttp_SyncDataSource(), new SimpleCallback<List<Book>>() {
                    @Override
                    public void onPreExecute(Object task) {
                        super.onPreExecute(task);
                        resultTextView.setText("正在执行SyncDataSource");
                    }

                    @Override
                    public void onPostExecute(Object task, Code code, Exception exception, List<Book> books) {
                        resultTextView.append("\n");
                        resultTextView.append("code:" + code);
                        resultTextView.append("\n");
                        if (code == Code.SUCCESS) {
                            resultTextView.append(new Gson().toJson(books));
                        }
                    }
                });
            } else if (v == asyncTaskButton) {
                asyncTaskHandle = taskHelper.execute(new LoginAsyncTask("LuckyJayce", "111"), new SimpleCallback<User>() {
                    @Override
                    public void onPreExecute(Object task) {
                        super.onPreExecute(task);
                        resultTextView.setText("正在执行asyncTaskButton");
                    }

                    @Override
                    public void onPostExecute(Object task, Code code, Exception exception, User user) {
                        resultTextView.append("\n");
                        resultTextView.append("code:" + code);
                        resultTextView.append("\n");
                        if (code == Code.SUCCESS) {
                            resultTextView.append(new Gson().toJson(user));
                        }
                    }
                });
            } else if (v == taskButton) {
                taskHandle = taskHelper.execute(new LoginTask("LuckyJayce", "111"), new SimpleCallback<User>() {
                    @Override
                    public void onPreExecute(Object task) {
                        super.onPreExecute(task);
                        resultTextView.setText("正在执行taskButton");
                    }

                    @Override
                    public void onPostExecute(Object task, Code code, Exception exception, User user) {
                        resultTextView.append("\n");
                        resultTextView.append("code:" + code);
                        resultTextView.append("\n");
                        if (code == Code.SUCCESS) {
                            resultTextView.append(new Gson().toJson(user));
                        }
                    }
                });
            }
        }
    };

}
