package com.shizhefei.test.controllers.task;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.Code;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.task.function.Func1;
import com.shizhefei.task.function.Func2;
import com.shizhefei.task.imp.SimpleCallback;
import com.shizhefei.task.tasks.LinkTask;
import com.shizhefei.task.tasks.ProxyTask;
import com.shizhefei.task.tasks.Tasks;
import com.shizhefei.task.utils.TaskLogUtil;
import com.shizhefei.test.models.enties.User;
import com.shizhefei.test.view.SetExceptionView;
import com.shizhefei.view.mvc.demo.R;


/**
 * Created by LuckyJayce on 2017/7/22.
 */

public class TaskOpExceptionActivity extends Activity {
    private Button runButton;
    private Button cancelButton;
    private SetExceptionView combineFunctionSetExceptionView;
    private SetExceptionView concatFunctionSetExceptionView;
    private SetExceptionView getHomeConfigSetExceptionView;
    private SetExceptionView getAppConfigSetExceptionView;
    private SetExceptionView loadHomeDataSetExceptionView;
    private EditText justEditText;
    private EditText retryTimesEditText;
    private TaskHelper<Object> taskHelper;
    private View layout;
    private SetExceptionView logInSetExceptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskopexception);
        runButton = (Button) findViewById(R.id.taskopexception_testButton);
        cancelButton = (Button) findViewById(R.id.taskopexception_cancelButton);
        layout = findViewById(R.id.taskopexception_layout);
        logInSetExceptionView = (SetExceptionView) findViewById(R.id.taskopexception_login_setExceptionView);
        combineFunctionSetExceptionView = (SetExceptionView) findViewById(R.id.taskopexception_combineFunction_setExceptionView);
        concatFunctionSetExceptionView = (SetExceptionView) findViewById(R.id.taskopexception_concatFunction_setExceptionView);
        getHomeConfigSetExceptionView = (SetExceptionView) findViewById(R.id.taskopexception_getHomeConfig_setExceptionView);
        getAppConfigSetExceptionView = (SetExceptionView) findViewById(R.id.taskopexception_getAppConfig_setExceptionView);
        loadHomeDataSetExceptionView = (SetExceptionView) findViewById(R.id.taskopexception_loadHomeData_SetExceptionView);
        justEditText = (EditText) findViewById(R.id.taskopexception_justTask_editTextView);
        retryTimesEditText = (EditText) findViewById(R.id.taskopexception_retryTimes_editText);

        runButton.setOnClickListener(onClickListener);
        cancelButton.setOnClickListener(onClickListener);
        layout.setOnClickListener(onClickListener);

        taskHelper = new TaskHelper<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskHelper.destroy();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == runButton) {
                String combineFunctionException = combineFunctionSetExceptionView.getDefinedException();
                String concatFunctionException = concatFunctionSetExceptionView.getDefinedException();
                String getHomeConfigSetException = getHomeConfigSetExceptionView.getDefinedException();
                String getAppConfigSetException = getAppConfigSetExceptionView.getDefinedException();
                String loadHomeDataSetException = loadHomeDataSetExceptionView.getDefinedException();
                String loginSetException = logInSetExceptionView.getDefinedException();
                String userId = justEditText.getText().toString();
                int retryTimes = 0;
                try {
                    retryTimes = Integer.parseInt(retryTimesEditText.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                LoadTask loadTask = new LoadTask(loginSetException, combineFunctionException, concatFunctionException, getAppConfigSetException, getHomeConfigSetException, loadHomeDataSetException, retryTimes, userId);
                taskHelper.execute(loadTask, new SimpleCallback<HomeData>() {
                    @Override
                    public void onPreExecute(Object task) {
                        super.onPreExecute(task);
                        runButton.setEnabled(false);
                        cancelButton.setEnabled(true);
                    }

                    @Override
                    public void onPostExecute(Object task, Code code, Exception exception, HomeData homeData) {
                        runButton.setEnabled(true);
                        cancelButton.setEnabled(false);
                        TaskLogUtil.d("TaskOpExceptionActivity 执行结果: {}", task, code, exception, homeData);
                    }
                });
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(justEditText.getWindowToken(), 0);
            } else if (v == cancelButton) {
                taskHelper.cancelAll();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(justEditText.getWindowToken(), 0);
            } else if (v == layout) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(justEditText.getWindowToken(), 0);
            }
        }
    };

    /**
     * 整个链式的LoadTask
     */
    private static class LoadTask extends ProxyTask<HomeData> {
        private final String loginException;
        private String userId;
        private String combineFunctionException;
        private String concatFunctionException;
        private String homeConfigException;
        private String appConfigException;
        private String homeDataException;
        private int retryTimes;

        public LoadTask(String loginException, String combineFunctionException, String concatFunctionException, String appConfigException, String homeConfigException, String homeDataException, int retryTimes, String userId) {
            this.combineFunctionException = combineFunctionException;
            this.concatFunctionException = concatFunctionException;
            this.appConfigException = appConfigException;
            this.homeConfigException = homeConfigException;
            this.homeDataException = homeDataException;
            this.loginException = loginException;
            this.retryTimes = retryTimes;
            this.userId = userId;
        }

        @Override
        protected IAsyncTask<HomeData> getTask() {
            //userId -> LoginTask -> [LoadAppConfigTask, LoadHomeConfigTask] -> LoadHomeDataTask 失败重试 -> 2000毫秒后执行SaveHomeDataTask
            LinkTask<HomeData> task = Tasks.just(userId).concatMap(new Func1<String, IAsyncTask<User>>() {
                @Override
                public IAsyncTask<User> call(String data) throws Exception {
                    if (concatFunctionException != null) {
                        throw new Exception(concatFunctionException);
                    }
                    return new LoginTask(data, loginException);
                }
            }).concatWith(Tasks.combine(new LoadAppConfigTask(appConfigException), new LoadHomeConfigTask(homeConfigException), new Func2<ConfigData, ConfigData, ConfigData>() {
                @Override
                public ConfigData call(ConfigData configData, ConfigData configData2) throws Exception {
                    if (combineFunctionException != null) {
                        throw new Exception(combineFunctionException);
                    }
                    return new ConfigData(configData.text + " - " + configData2.text);
                }
            })).concatWith(Tasks.retry(new LoadHomeDataTask(homeDataException, retryTimes), retryTimes)).concatMap(new Func1<HomeData, IAsyncTask<HomeData>>() {
                @Override
                public IAsyncTask<HomeData> call(HomeData data) throws Exception {
                    return Tasks.delay(new SaveHomeDataTask(data), 2000);
                }
            });
//            task = Tasks.timeout(task, 3000);
            TaskLogUtil.d("LoadTask getTask={}", task);
            return task;
        }
    }

    /**
     * 登录task
     */
    private static class LoginTask implements IAsyncTask<User> {
        private String userId;
        private String loginSetException;

        public LoginTask(String userId, String loginSetException) {
            this.userId = userId;
            this.loginSetException = loginSetException;
        }

        @Override
        public RequestHandle execute(ResponseSender<User> sender) throws Exception {
            //如果几面有配置异常就直接抛出异常
            if (loginSetException != null) {
                throw new Exception(loginSetException);
            }
            //发送成功数据
            sender.sendData(new User(userId, "name", 11, "info"));
            return null;
        }
    }

    /**
     * 加载App配置信息
     */
    private static class LoadAppConfigTask implements IAsyncTask<ConfigData> {
        private final String exception;

        public LoadAppConfigTask(String exception) {
            this.exception = exception;
        }

        @Override
        public RequestHandle execute(ResponseSender<ConfigData> sender) throws Exception {
            //如果几面有配置异常就直接抛出异常
            if (exception != null) {
                throw new Exception(exception);
            }
            //发送成功数据
            sender.sendData(new ConfigData("appConfig"));
            return null;
        }
    }

    /**
     * 加载主页配置信息
     */
    private static class LoadHomeConfigTask implements IAsyncTask<ConfigData> {
        private final String exception;

        public LoadHomeConfigTask(String exception) {
            this.exception = exception;
        }

        @Override
        public RequestHandle execute(ResponseSender<ConfigData> sender) throws Exception {
            //如果几面有配置异常就直接抛出异常
            if (exception != null) {
                throw new Exception(exception);
            }
            //发送成功数据
            sender.sendData(new ConfigData("homeConfig"));
            return null;
        }
    }

    /**
     * 加载主页数据
     */
    private static class LoadHomeDataTask implements IAsyncTask<HomeData> {
        private final String exception;
        private final Handler handler;
        private int retryTimes;
        private int times;

        public LoadHomeDataTask(String exception, int retryTimes) {
            this.exception = exception;
            this.retryTimes = retryTimes;
            handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public RequestHandle execute(final ResponseSender<HomeData> sender) throws Exception {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //如果界面有配置异常
                    if (exception != null) {
                        //如果有设置重试次数：前几次都报错，最后一次为成功
                        if (retryTimes > 0) {
                            if (times < retryTimes) {
                                times++;
                                sender.sendError(new Exception(exception));
                                return;
                            }
                        } else {//没有设置重试次数就直接抛出异常
                            sender.sendError(new Exception(exception));
                            return;
                        }
                    }
                    //发送成功数据
                    sender.sendData(new HomeData("homeData"));
                }
            }, 3000);
            return new RequestHandle() {
                @Override
                public void cancle() {
                    handler.removeCallbacksAndMessages(null);
                }

                @Override
                public boolean isRunning() {
                    return false;
                }
            };
        }
    }

    /**
     * 保存主页数据
     */
    private static class SaveHomeDataTask implements IAsyncTask<HomeData> {
        private HomeData homeData;

        public SaveHomeDataTask(HomeData homeData) {
            this.homeData = homeData;
        }

        @Override
        public RequestHandle execute(ResponseSender<HomeData> sender) throws Exception {
            sender.sendData(homeData);
            return null;
        }
    }

    private static class HomeData {
        private String text;

        public HomeData(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "HomeData data:+" + text;
        }
    }

    private static class ConfigData {
        private String text;

        public ConfigData(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "ConfigData data:+" + text;
        }
    }
}
