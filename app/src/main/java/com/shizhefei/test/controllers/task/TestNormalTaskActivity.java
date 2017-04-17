package com.shizhefei.test.controllers.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shizhefei.view.mvc.demo.R;

import java.lang.ref.WeakReference;

/**
 * Created by luckyjayce on 2017/4/15.
 */

public class TestNormalTaskActivity extends Activity {

    private View threadButton;
    private View asyTaskButton;
    private TextView resultTextView;
    private StaticHandler staticHandler;
    private SelfHandler selfHandler;
    private View threadWeakButton;
    private SelfAsyncTask selfInterruptAsyncTask;
    private SelfAsyncTask selfAsyncTask;
    private View staticThreadWeakButton;
    private View interruptAsyTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_normaltask);
        threadButton = findViewById(R.id.normalTask_thread_button);
        threadWeakButton = findViewById(R.id.normalTask_threadWeak_button);
        staticThreadWeakButton = findViewById(R.id.normalTask_staticThreadWeak_button);
        interruptAsyTaskButton = findViewById(R.id.normalTask_interruptAsyTask_button);
        asyTaskButton = findViewById(R.id.normalTask_asyTask_button);
        resultTextView = (TextView) findViewById(R.id.normalTask_result2_textView);

        staticHandler = new StaticHandler(resultTextView);
        selfHandler = new SelfHandler();

        staticThreadWeakButton.setOnClickListener(onClickListener);
        threadButton.setOnClickListener(onClickListener);
        threadWeakButton.setOnClickListener(onClickListener);
        asyTaskButton.setOnClickListener(onClickListener);
        interruptAsyTaskButton.setOnClickListener(onClickListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (selfInterruptAsyncTask != null) {
            selfInterruptAsyncTask.cancel(true);
        }
        if (selfAsyncTask != null) {
            selfAsyncTask.cancel(false);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == threadButton) {//测试Thread+ 静态Handler方式
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            staticHandler.sendMessage(staticHandler.obtainMessage(WHAT_START));
                            for (int i = 0; i < 20; i++) {
                                Thread.sleep(1000);
                                Log.d("zzzz", "threadButton progress:" + i);
                                staticHandler.sendMessage(staticHandler.obtainMessage(WHAT_PROGRESS, i));
                            }
                            staticHandler.sendMessage(staticHandler.obtainMessage(WHAT_SUCCESS, "完成"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            staticHandler.sendMessage(staticHandler.obtainMessage(WHAT_EXCEPTION, e));
                        }
                    }
                }.start();
            } else if (v == threadWeakButton) {//测试Thread+ 内部类Handler+WeakReference方式
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            selfHandler.sendMessage(selfHandler.obtainMessage(WHAT_START));
                            for (int i = 0; i < 20; i++) {
                                Thread.sleep(1000);
                                Log.d("zzzz", "threadButton progress:" + i);
                                selfHandler.sendMessage(selfHandler.obtainMessage(WHAT_PROGRESS, i));
                            }
                            selfHandler.sendMessage(selfHandler.obtainMessage(WHAT_SUCCESS, "完成"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            selfHandler.sendMessage(selfHandler.obtainMessage(WHAT_EXCEPTION, e));
                        }
                    }
                }.start();
            } else if (v == staticThreadWeakButton) {//静态Thread+静态Handler+WeakReference方式
                new LongTimeThread(staticHandler).start();
            } else if (v == asyTaskButton) {// 内部类asyncTask 没有Interrupt
                selfAsyncTask = new SelfAsyncTask();
                selfAsyncTask.execute();
            } else if (v == interruptAsyTaskButton) {//内部类asyncTask Interrupt
                selfInterruptAsyncTask = new SelfAsyncTask();
                selfInterruptAsyncTask.execute();
            }
            //还有静态asyncTask + WeakReference方式
            //静态asyncTask + cancel的时候置空引用对象
        }
    };

    private static final int WHAT_START = 0;
    private static final int WHAT_PROGRESS = 1;
    private static final int WHAT_SUCCESS = 2;
    private static final int WHAT_EXCEPTION = 3;

    private static class StaticHandler extends Handler {
        private WeakReference<TextView> weakReference;

        public StaticHandler(TextView textView) {
            this.weakReference = new WeakReference<>(textView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView textView = weakReference.get();
            if (textView == null) {
                return;
            }
            switch (msg.what) {
                case WHAT_START:
                    textView.setText("开始:");
                    break;
                case WHAT_EXCEPTION:
                    textView.append("\n异常:" + msg.obj);
                    break;
                case WHAT_SUCCESS:
                    textView.append("\nsuccess:" + msg.obj);
                    break;
                case WHAT_PROGRESS:
                    textView.append("\nprogress:" + msg.obj);
                    break;
            }
        }
    }

    private class SelfHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_START:
                    resultTextView.setText("开始:");
                    break;
                case WHAT_EXCEPTION:
                    resultTextView.append("\n异常:" + msg.obj);
                    break;
                case WHAT_SUCCESS:
                    resultTextView.append("\nsuccess:" + msg.obj);
                    break;
                case WHAT_PROGRESS:
                    resultTextView.append("\nprogress:" + msg.obj);
                    break;
            }
        }
    }

    private class SelfAsyncTask extends AsyncTask<Void, Integer, String> {
        private volatile Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resultTextView.append("开始AsyncTask:");
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                for (int i = 0; i < 20; i++) {
                    Thread.sleep(1000);
                    Log.d("zzzz", "AsyncLinkTask progress:" + i);
                    publishProgress(i);
                }
                return "完成";
            } catch (Exception e) {
                e.printStackTrace();
                this.exception = e;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            resultTextView.append("\nAsyncLinkTask progress:");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (exception != null) {
                resultTextView.append("\nAsyncLinkTask exception:" + exception);
            } else {
                resultTextView.append("\nAsyncLinkTask 结果:" + s);
            }
        }
    }

    private static class LongTimeThread extends Thread {

        public LongTimeThread(Handler leakMyHandler) {
            this.leakMyHandler = leakMyHandler;
        }

        private Handler leakMyHandler;

        @Override
        public void run() {
            super.run();
            try {
                leakMyHandler.sendMessage(leakMyHandler.obtainMessage(WHAT_START));
                for (int i = 0; i < 20; i++) {
                    Thread.sleep(1000);
                    Log.d("zzzz", "threadButton progress:" + i);
                    leakMyHandler.sendMessage(leakMyHandler.obtainMessage(WHAT_PROGRESS, i));
                }
                leakMyHandler.sendMessage(leakMyHandler.obtainMessage(WHAT_SUCCESS, "完成"));
            } catch (Exception e) {
                e.printStackTrace();
                leakMyHandler.sendMessage(leakMyHandler.obtainMessage(WHAT_EXCEPTION, e));
            }
        }
    }

}
