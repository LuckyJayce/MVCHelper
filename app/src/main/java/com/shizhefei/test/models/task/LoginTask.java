package com.shizhefei.test.models.task;

import android.text.TextUtils;

import com.shizhefei.task.Data;
import com.shizhefei.task.ProgressSender;
import com.shizhefei.task.Task;
import com.shizhefei.test.models.enties.User;
import com.shizhefei.utils.HttpUtils;

public class LoginTask implements Task<User, String> {
	private String name;
	private String password;

	public LoginTask(String name, String password) {
		super();
		this.name = name;
		this.password = password;
	}

	@Override
	public Data<User, String> execute(ProgressSender progressSender) throws Exception {
		// 这里用百度首页模拟网络请求，如果网路出错的话，直接抛异常不会执行后面语句
		HttpUtils.executeGet("http://www.baidu.com");
		Thread.sleep(300);

		if (TextUtils.isEmpty(name)) {
			return Data.madeFail("请输入用户名");
		}
		if (TextUtils.isEmpty(password)) {
			return Data.madeFail("请输入密码");
		}
		if (name.equals("aaa") && password.equals("111")) {
			return Data.madeSuccess(new User("1", "aaa", 23, "中国人"));
		} else {
			return Data.madeFail("用户名或者密码不正确");
		}
	}

	@Override
	public void cancle() {

	}

}
