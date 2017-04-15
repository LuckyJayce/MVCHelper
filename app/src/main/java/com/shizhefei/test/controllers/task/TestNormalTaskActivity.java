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
    private StaticHandler handler;
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

        handler = new StaticHandler(resultTextView);
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
            if (v == threadButton) {//测试Thread+Handler方式
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            handler.sendMessage(handler.obtainMessage(WHAT_START));
                            for (int i = 0; i < 20; i++) {
                                Thread.sleep(1000);
                                Log.d("zzzz", "threadButton progress:" + i);
                                handler.sendMessage(handler.obtainMessage(WHAT_PROGRESS, i));
                            }
                            handler.sendMessage(handler.obtainMessage(WHAT_SUCCESS, "完成"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            handler.sendMessage(handler.obtainMessage(WHAT_EXCEPTION, e));
                        }
                    }
                }.start();
            } else if (v == threadWeakButton) {//测试Thread+Handler+WeakReference方式
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
            } else if (v == staticThreadWeakButton) {
                new LongTimeThread(handler).start();
            } else if (v == asyTaskButton) {//
                selfAsyncTask = new SelfAsyncTask();
                selfAsyncTask.execute();
            } else if (v == interruptAsyTaskButton) {
                selfInterruptAsyncTask = new SelfAsyncTask();
                selfInterruptAsyncTask.execute();
            }
        }
    };

    private static final int WHAT_START = 0;
    private static final int WHAT_PROGRESS = 1;
    private static final int WHAT_SUCCESS = 2;
    private static final int WHAT_EXCEPTION = 3;

    private static class StaticHandler extends Handler {
        private WeakReference<TextView> weakReference;

        public StaticHandler(TextView textView) {
            this.weakReference = new WeakReference<TextView>(textView);
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
                    Log.d("zzzz", "AsyncTask progress:" + i);
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
            resultTextView.append("\nAsyncTask progress:");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (exception != null) {
                resultTextView.append("\nAsyncTask exception:" + exception);
            } else {
                resultTextView.append("\nAsyncTask 结果:" + s);
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
