/*
Copyright 2015 shizhefei（LuckyJayce）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.shizhefei.test.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shizhefei.task.Code;
import com.shizhefei.task.ICallback;
import com.shizhefei.task.TaskHandle;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.task.imp.MemoryCacheStore;
import com.shizhefei.test.controllers.mvchelpers.NormalActivity;
import com.shizhefei.test.controllers.mvchelpers.PullrefshActivity;
import com.shizhefei.test.controllers.mvchelpers.SwipeRefreshActivity;
import com.shizhefei.test.controllers.mvchelpers.UltraActivity;
import com.shizhefei.test.controllers.other.BookDetailActivity;
import com.shizhefei.test.controllers.other.MovieDetailActivity;
import com.shizhefei.test.controllers.other.UltraRecyclerViewActivity;
import com.shizhefei.test.controllers.other.Volley_OKHttp_GridViewActivity;
import com.shizhefei.test.controllers.task.ListTaskActivity;
import com.shizhefei.test.controllers.task.LoginActivity;
import com.shizhefei.test.controllers.task.TaskDemoActivity;
import com.shizhefei.test.models.task.LoginTask;
import com.shizhefei.view.mvc.demo.R;

public class MainActivity extends Activity {

    private TaskHelper<Object> taskHelper;
    private LoginTask loginTask;
    private TaskHandle taskHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskHelper = new TaskHelper<>(new MemoryCacheStore(100));

        taskHelper.registerCallBack(new ICallback<Object>() {
            @Override
            public void onPreExecute(Object task) {
                Log.d("vvvv", "registCallBak onPreExecute task:" + task);
            }

            @Override
            public void onProgress(Object task, int percent, long current, long total, Object extraData) {
//                Log.d("vvvv", "registCallBak onProgress current:"+current+"  task:" + task);
            }

            @Override
            public void onPostExecute(Object task, Code code, Exception exception, Object Object) {
                Log.d("vvvv", "registCallBak onPostExecute task:" + task + " code:" + code);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskHelper.destroy();
    }

    /**
     * 测试用例
     *
     * @param view
     */
    public void onClickTestCase(View view) {
        ProxyActivity.startActivity(this, TestCaseFragment.class, "测试用例");
    }

    /**
     * MVCPullrefshHelper的Demo
     *
     * @param view
     */
    public void onClickDemo(View view) {
        startActivity(new Intent(getApplicationContext(), PullrefshActivity.class));
    }

    /**
     * MVCUltraHelper的Demo
     *
     * @param view
     */
    public void onClickDemo2(View view) {
        startActivity(new Intent(getApplicationContext(), UltraActivity.class));
    }

    /**
     * MVCSwipeRefreshHelper的Demo
     *
     * @param view
     */
    public void onClickDemo3(View view) {
        startActivity(new Intent(getApplicationContext(), SwipeRefreshActivity.class));
    }

    /**
     * 不具有下拉刷新的非ListView界面
     *
     * @param view
     */
    public void onClickDemo4(View view) {
        startActivity(new Intent(getApplicationContext(), BookDetailActivity.class));
    }

    /**
     * 不具有下拉刷新的非ListView界面
     *
     * @param view
     */
    public void onClickDemo5(View view) {
        startActivity(new Intent(getApplicationContext(), NormalActivity.class));
    }

    /**
     * Ultra的RecyclerView界面
     *
     * @param view
     */
    public void onClickDemo6(View view) {
        startActivity(new Intent(getApplicationContext(), UltraRecyclerViewActivity.class));
    }

    /**
     * 超复杂的界面
     *
     * @param view
     */
    public void onClickDemo7(View view) {
        startActivity(new Intent(getApplicationContext(), MovieDetailActivity.class));
    }

    /**
     * Volley和OKhttp网络请求\nandroid-async-http网络请求\n
     * GridView界面
     *
     * @param view
     */
    public void onClickDemo8(View view) {
        startActivity(new Intent(getApplicationContext(), Volley_OKHttp_GridViewActivity.class));
    }

    /**
     * 带有缓存的Task列表
     *
     * @param view
     */
    public void onClickTask3(View view) {
        startActivity(new Intent(getApplicationContext(), ListTaskActivity.class));
    }

    /**
     * 登陆Task
     *
     * @param view
     */
    public void onClickTask1(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    /**
     * TaskDemo
     *
     * @param view
     */
    public void onClickTaskDemo(View view) {
        startActivity(new Intent(getApplicationContext(), TaskDemoActivity.class));
    }


}
