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
package com.shizhefei.test.controllers.testhelpers;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCUltraHelper;
import com.shizhefei.test.models.datasource.BooksDataSource;
import com.shizhefei.test.models.enties.Book;
import com.shizhefei.test.view.adapters.BooksAdapter;
import com.shizhefei.view.mvc.demo.R;

/**
 * 测试下拉刷新组件，MVCUltraHelper
 * 
 * @author LuckyJayce
 *
 */
public class UltraActivity extends Activity {
	private MVCHelper<List<Book>> listViewHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ultra);

		// 设置LoadView的factory，用于创建使用者自定义的加载失败，加载中，加载更多等布局,写法参照DeFaultLoadViewFactory
		// ListViewHelper.setLoadViewFactory(new LoadViewFactory());

		/*
		 * 配置PtrClassicFrameLayout的刷新样式
		 */
		PtrClassicFrameLayout mPtrFrameLayout = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
		final MaterialHeader header = new MaterialHeader(getApplicationContext());
		header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		header.setPadding(0, dipToPix(getApplicationContext(), 15), 0, dipToPix(getApplicationContext(), 10));
		header.setPtrFrameLayout(mPtrFrameLayout);
		mPtrFrameLayout.setLoadingMinTime(800);
		mPtrFrameLayout.setDurationToCloseHeader(800);
		mPtrFrameLayout.setHeaderView(header);
		mPtrFrameLayout.addPtrUIHandler(header);

		listViewHelper = new MVCUltraHelper<List<Book>>(mPtrFrameLayout);
		// 设置数据源
		listViewHelper.setDataSource(new BooksDataSource());
		// 设置适配器
		listViewHelper.setAdapter(new BooksAdapter(this));

		// 加载数据
		listViewHelper.refresh();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 释放资源
		listViewHelper.destory();
	}

	public void onClickBack(View view) {
		finish();
	}

	/**
	 * 根据dip值转化成px值
	 * 
	 * @param context
	 * @param dip
	 * @return
	 */
	public static int dipToPix(Context context, int dip) {
		int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
		return size;
	}

}
