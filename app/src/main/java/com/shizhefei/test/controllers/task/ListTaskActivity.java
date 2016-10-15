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
package com.shizhefei.test.controllers.task;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCUltraHelper;
import com.shizhefei.task.Code;
import com.shizhefei.task.ICallback;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.task.imp.MemoryCacheStore;
import com.shizhefei.test.models.datasource.MoviesDataSource;
import com.shizhefei.test.models.enties.Movie;
import com.shizhefei.test.models.enties.MovieAmount;
import com.shizhefei.test.view.adapters.MoviesAdapter;
import com.shizhefei.view.mvc.demo.R;

import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/***
 * <pre>
 * 模拟列表多个请求的task
 * 这个作用，比如服务端数据静态化。
 * 视频列表的数据除了评论和播放数据其他是基本不变的，那么这个视频列表就可以静态的页面接口
 * 而评论和播放量也可以作为静态页面，每个视频对应一个静态页的评论数和播放量，那个视频的评论数变动的话，只更新这静态页就可以
 *</>
 * @author LuckyJayce
 */
public class ListTaskActivity extends Activity {
    private MVCHelper<List<Movie>> mvcHelper;
    private TaskHelper<MovieAmount> taskHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultrarecyclerview);

        PtrClassicFrameLayout mPtrFrameLayout = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        taskHelper = new TaskHelper<>(new MemoryCacheStore(200));
        //注册全局的task执行回调
        taskHelper.registerCallBack(new ICallback<MovieAmount>() {
            @Override
            public void onPreExecute(Object task) {
                Log.d("pppp", "task:" + task);
            }

            @Override
            public void onProgress(Object task, int percent, long current, long total, Object extraData) {

            }

            @Override
            public void onPostExecute(Object task, Code code, Exception exception, MovieAmount movieAmount) {
                Log.d("pppp", "task:" + task + " code:" + code);
            }
        });

        mvcHelper = new MVCUltraHelper<>(mPtrFrameLayout);
        // 设置数据源
        mvcHelper.setDataSource(new MoviesDataSource());
        // 设置适配器
        mvcHelper.setAdapter(new MoviesAdapter(taskHelper));

        // 加载数据
        mvcHelper.refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放资源
        mvcHelper.destory();
        //取消所有请求
        taskHelper.destroy();
    }

    public void onClickBack(View view) {
        finish();
    }

}
