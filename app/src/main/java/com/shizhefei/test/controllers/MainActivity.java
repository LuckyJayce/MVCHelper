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
import android.view.View;

import com.shizhefei.test.controllers.other.BookDetailActivity;
import com.shizhefei.test.controllers.other.MovieDetailActivity;
import com.shizhefei.test.controllers.other.UltraRecyclerViewActivity;
import com.shizhefei.test.controllers.other.Volley_OKHttp_GridViewActivity;
import com.shizhefei.test.controllers.task.LoginActivity;
import com.shizhefei.test.controllers.task.UploadActivity;
import com.shizhefei.test.controllers.testhelpers.NormalActivity;
import com.shizhefei.test.controllers.testhelpers.PullrefshActivity;
import com.shizhefei.test.controllers.testhelpers.SwipeRefreshActivity;
import com.shizhefei.test.controllers.testhelpers.UltraActivity;
import com.shizhefei.view.mvc.demo.R;

import testcase.TestCaseFragment;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
     * 登陆Task
     *
     * @param view
     */
    public void onClickTask1(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    /**
     * 上传文件Task
     *
     * @param view
     */
    public void onClickTask2(View view) {
        startActivity(new Intent(getApplicationContext(), UploadActivity.class));
    }

}
