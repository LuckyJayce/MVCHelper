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
package com.shizhefei.test.controllers.other;

import in.srain.cube.views.GridViewHandler;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCSwipeRefreshHelper;
import com.shizhefei.test.models.datasource.BooksVolleyDataSource;
import com.shizhefei.test.models.enties.Book;
import com.shizhefei.test.view.adapters.BooksAdapter;
import com.shizhefei.view.mvc.demo.R;

/**
 * 测试下拉刷新组件，MVCSwipeRefreshHelper
 * 
 * @author LuckyJayce
 *
 */
public class VolleyGridViewActivity extends Activity {

	private MVCHelper<List<Book>> mvcHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_girdview);

		// PullToRefreshListView refreshListView = (PullToRefreshListView)
		// findViewById(R.id.pullToRefreshListView);
		SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
		// GridViewWithHeaderAndFooter gridViewWithHeaderAndFooter =
		// (GridViewWithHeaderAndFooter) findViewById(R.id.girdView);
		mvcHelper = new MVCSwipeRefreshHelper<List<Book>>(swipeRefreshLayout);

		// 设置数据源
		mvcHelper.setDataSource(new BooksVolleyDataSource());

		// 设置AsyncHttpClient 作为网络请求的数据源
		// listViewHelper.setDataSource(new BooksAsyncDataSource());

		// 设置适配器
		mvcHelper.setAdapter(new BooksAdapter(this), new GridViewHandler());

		// 加载数据
		mvcHelper.refresh();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 释放资源
		mvcHelper.destory();
	}

	public void onClickBack(View view) {
		finish();
	}

}
