package com.shizhefei.test.controllers.task;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shizhefei.mvc.ProgressSender;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.Code;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.ITask;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.task.imp.SimpleCallback;
import com.shizhefei.test.models.datasource.okhttp.BooksOkHttp_AsyncDataSource;
import com.shizhefei.test.models.datasource.okhttp.BooksOkHttp_SyncDataSource;
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
    private View longTimeTask;
    private View longTimeAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_demo);
        asyncDataSourceButton = findViewById(R.id.taskdemo_iasyncdatasoruce_button);
        taskButton = findViewById(R.id.taskdemo_itask_button);
        asyncTaskButton = findViewById(R.id.taskdemo_iasynctask_button);
        dataSourceButton = findViewById(R.id.taskdemo_idatasoruce_button);
        longTimeTask = findViewById(R.id.taskdemo_longTimeTask_button);
        longTimeAsyncTask = findViewById(R.id.taskdemo_longTimeAsyncTask_button);
        resultTextView = (TextView) findViewById(R.id.taskdemo_result_textView);
        result2TextView = (TextView) findViewById(R.id.taskdemo_result2_textView);

        asyncDataSourceButton.setOnClickListener(onClickListener);
        taskButton.setOnClickListener(onClickListener);
        asyncTaskButton.setOnClickListener(onClickListener);
        dataSourceButton.setOnClickListener(onClickListener);
        longTimeTask.setOnClickListener(onClickListener);
        longTimeAsyncTask.setOnClickListener(onClickListener);

        taskHelper = new TaskHelper<>();

        taskHelper.registerCallBack(new SimpleCallback<Object>() {
            @Override
            public void onPreExecute(Object task) {
                super.onPreExecute(task);
                result2TextView.setText("开始执行:" + task.getClass().getSimpleName());
            }

            @Override
            public void onProgress(Object task, int percent, long current, long total, Object extraData) {
                super.onProgress(task, percent, current, total, extraData);
                result2TextView.append("\n progress percent:" + percent + " current:" + current + " total:" + total + " extraData:" + extraData);
                Log.d("zzzz", "registerCallBack progress:" + current);
            }

            @Override
            public void onPostExecute(Object task, Code code, Exception exception, Object data) {
                result2TextView.append("\n");
                result2TextView.append("code:" + code);
                result2TextView.append("\n");
                if (code == Code.SUCCESS) {
                    result2TextView.append(new Gson().toJson(data));
                } else if (code == Code.EXCEPTION) {
                    result2TextView.append(exception.getMessage());
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
        @Override
        public void onClick(View v) {
            //取消全部
            taskHelper.cancelAll();

            if (v == asyncDataSourceButton) {
                taskHelper.execute(new BooksOkHttp_AsyncDataSource(), new SimpleCallback<List<Book>>() {
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
                        } else if (code == Code.EXCEPTION) {
                            resultTextView.append(exception.getMessage());
                        }
                    }
                });
            } else if (v == dataSourceButton) {
                taskHelper.execute(new BooksOkHttp_SyncDataSource(), new SimpleCallback<List<Book>>() {
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
                        } else if (code == Code.EXCEPTION) {
                            resultTextView.append(exception.getMessage());
                        }
                    }
                });
            } else if (v == asyncTaskButton) {
                taskHelper.execute(new LoginAsyncTask("LuckyJayce", "111"), new SimpleCallback<User>() {
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
                        } else if (code == Code.EXCEPTION) {
                            resultTextView.append(exception.getMessage());
                        }
                    }
                });
            } else if (v == taskButton) {
                taskHelper.execute(new LoginTask("LuckyJayce", "111"), new SimpleCallback<User>() {
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
                        } else if (code == Code.EXCEPTION) {
                            resultTextView.append(exception.getMessage());
                        }
                    }
                });
            } else if (v == longTimeTask) {
                taskHelper.execute(new LongTimeTask(), null);
            } else if (v == longTimeAsyncTask) {
                taskHelper.execute(new LongTimeAsyncTask(), null);
            }
        }
    };

    private static class LongTimeTask implements ITask<String> {
        @Override
        public String execute(ProgressSender progressSender) throws Exception {
            for (int i = 0; i < 20; i++) {
                Thread.sleep(1000);
                Log.d("zzzz", "IAsyncTask progress:" + i);
                progressSender.sendProgress(i, 20, null);
            }
            return "完成";
        }

        @Override
        public void cancel() {
            //这里故意不写取消的方法，测试任务没有被取消一直执行
            // 会不会导致Activity和Callback有没有被强引用而gc不了
        }
    }

    private static class LongTimeAsyncTask implements IAsyncTask<String> {

        @Override
        public RequestHandle execute(final ResponseSender<String> sender) throws Exception {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        for (int i = 0; i < 20; i++) {
                            Thread.sleep(1000);
                            Log.d("zzzz", "IAsyncTask progress:" + i);
                            sender.sendProgress(i, 20, null);
                        }
                        sender.sendData("完成");
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender.sendError(e);
                    }
                }
            }.start();
            //这里故意不写取消的RequestHandle对象返回null，测试任务没有被取消一直执行
            // 会不会导致Activity和Callback有没有被强引用而gc不了
            return null;
        }
    }


}
