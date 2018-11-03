package com.shizhefei.test;

import android.app.Application;

import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.test.view.MyLoadViewFactory;
import com.squareup.leakcanary.LeakCanary;

public class BaseApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
		// 设置LoadView的factory，用于创建使用者自定义的加载失败，加载中，加载更多等布局,写法参照DeFaultLoadViewFactory
		MVCHelper.setLoadViewFractory(new MyLoadViewFactory());

		if (LeakCanary.isInAnalyzerProcess(this)) {
			// This process is dedicated to LeakCanary for heap analysis.
			// You should not init your app in this process.
			return;
		}
		LeakCanary.install(this);
	}

}
