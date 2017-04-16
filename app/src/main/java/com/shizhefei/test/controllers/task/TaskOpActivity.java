package com.shizhefei.test.controllers.task;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shizhefei.mvc.ProgressSender;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.Code;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.ITask;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.task.function.Func1;
import com.shizhefei.task.function.Func2;
import com.shizhefei.task.imp.SimpleCallback;
import com.shizhefei.task.tasks.LinkTask;
import com.shizhefei.task.tasks.LinkTasks;
import com.shizhefei.task.tasks.ProxyTask;
import com.shizhefei.test.models.enties.Book;
import com.shizhefei.test.models.enties.User;
import com.shizhefei.test.view.callback.CallbackTextView;
import com.shizhefei.view.mvc.demo.R;


/**
 * Created by luckyjayce on 2017/4/16.
 */

public class TaskOpActivity extends Activity {
    private View concatButton;
    private TaskHelper<Object> taskHelper;
    private CallbackTextView callBackTextView;
    private View cancelButton;
    private View combineButton;
    private View proxyTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskop);
        concatButton = findViewById(R.id.taskop_concat_button);
        cancelButton = findViewById(R.id.taskop_cancel_button);
        combineButton = findViewById(R.id.taskop_combine_button);
        proxyTaskButton = findViewById(R.id.taskop_proxyTask_button);
        callBackTextView = (CallbackTextView) findViewById(R.id.taskdemo_result2_callbackTextView);

        concatButton.setOnClickListener(onClickListener);
        cancelButton.setOnClickListener(onClickListener);
        combineButton.setOnClickListener(onClickListener);
        proxyTaskButton.setOnClickListener(onClickListener);

        taskHelper = new TaskHelper<>();
        taskHelper.registerCallBack(callBackTextView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskHelper.destroy();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            taskHelper.cancelAll();
            if (v == concatButton) {
                LinkTask<String> task = LinkTasks
                        .async(new InitTokenTask())
                        .concatWith(LinkTasks.async(new GetUserTask()))
                        .concatMap(new Func1<User, IAsyncTask<String>>() {
                            @Override
                            public IAsyncTask<String> call(User data) throws Exception {
                                return new GetUserName(data);
                            }
                        });
                taskHelper.execute(task, new StringCallback("concatTask"));
            } else if (v == cancelButton) {

            } else if (v == combineButton) {
                LinkTask<String> task = LinkTasks.create(new InitTokenTask())
                        .concatWith(LinkTasks.combine(LinkTasks.async(new GetUserTask()), new GetBookTask(), new Func2<User, Book, String>() {
                            @Override
                            public String call(User user, Book book) throws Exception {
                                return "userName:" + user.getName() + " read book:" + book.getName();
                            }
                        }));
                taskHelper.execute(task, new StringCallback("combineTask"));
            } else if (v == proxyTaskButton) {
                taskHelper.execute(new InitAndGetUserNameTask(), new StringCallback("proxyTask"));
            }
        }
    };

    private class StringCallback extends SimpleCallback<String> {
        private String taskName;

        public StringCallback(String taskName) {
            this.taskName = taskName;
        }

        @Override
        public void onPreExecute(Object task) {
            super.onPreExecute(task);
            Log.d("pppp", taskName + " SimpleCallback onPreExecute" + task);
        }

        @Override
        public void onProgress(Object task, int percent, long current, long total, Object extraData) {
            super.onProgress(task, percent, current, total, extraData);
            Log.d("pppp", taskName + " SimpleCallback onProgress current:" + current + " total:" + total);
        }

        @Override
        public void onPostExecute(Object task, Code code, Exception exception, String user) {
            Log.d("pppp", taskName + " SimpleCallback code:" + code + " exception:" + exception + " user:" + user);
        }
    }

    private static class InitAndGetUserNameTask extends ProxyTask<String> {

        @Override
        protected IAsyncTask<String> getTask() {
            LinkTask<String> task = LinkTasks
                    .async(new InitTokenTask())
                    .concatWith(LinkTasks.async(new GetUserTask()))
                    .concatMap(new Func1<User, IAsyncTask<String>>() {
                        @Override
                        public IAsyncTask<String> call(User data) throws Exception {
                            return new GetUserName(data);
                        }
                    });
            return task;
        }
    }

    private static class InitTokenTask implements ITask<Void> {

        @Override
        public Void execute(ProgressSender progressSender) throws Exception {
            for (int i = 0; i < 5; i++) {
                Thread.sleep(1000);
                Log.d("pppp", " InitTokenTask onProgress current:" + i + " total:" + 5);
                progressSender.sendProgress(i, 10, "InitTokenTask");
            }
            return null;
        }

        @Override
        public void cancel() {

        }
    }

    private static class GetUserTask implements ITask<User> {

        @Override
        public User execute(ProgressSender progressSender) throws Exception {
            for (int i = 0; i < 5; i++) {
                Thread.sleep(1000);
                Log.d("pppp", " GetUserTask onProgress current:" + i + " total:" + 5);
                progressSender.sendProgress(i, 10, "GetUserTask");
            }
            return new User("id:1", "LuckyJayce", 1, "test");
        }

        @Override
        public void cancel() {

        }
    }

    private static class GetBookTask implements IAsyncTask<Book> {

        @Override
        public RequestHandle execute(ResponseSender<Book> sender) throws Exception {
            sender.sendData(new Book("Java编程思想", 100));
            return null;
        }
    }

    private static class GetUserName implements IAsyncTask<String> {
        private User user;

        public GetUserName(User user) {
            this.user = user;
        }

        @Override
        public RequestHandle execute(final ResponseSender<String> sender) throws Exception {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(1000);
                        sender.sendData(user.getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        sender.sendError(e);
                    }
                }
            }.start();
            return null;
        }
    }


}
