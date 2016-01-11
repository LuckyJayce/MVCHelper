package com.shizhefei.test;

import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.test.view.MyLoadViewFactory;
import com.shizhefei.utils.MyVolley;

import android.app.Application;

public class BaseApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		MyVolley.init(getApplicationContext());
		
		// 设置LoadView的factory，用于创建使用者自定义的加载失败，加载中，加载更多等布局,写法参照DeFaultLoadViewFactory
		MVCHelper.setLoadViewFractory(new MyLoadViewFactory());
		
	}

}
