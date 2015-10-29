package com.shizhefei.test.models.task;

import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.ProgressSender;
import com.shizhefei.task.ResponseSender;
import com.shizhefei.test.models.datasource.VolleyRequestHandle;
import com.shizhefei.test.models.enties.User;
import com.shizhefei.utils.MyVolley;

public class LoginAsyncTask implements IAsyncTask<User, String> {
	private String name;
	private String password;

	public LoginAsyncTask(String name, String password) {
		super();
		this.name = name;
		this.password = password;
	}

	@Override
	public RequestHandle execute(final ResponseSender<User, String> sender, ProgressSender progressSender) throws Exception {
		String url = "http://www.baidu.com";
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("userName", name);
		builder.appendQueryParameter("password", password);
		StringRequest jsonObjRequest = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				if (TextUtils.isEmpty(name)) {
					sender.sendFail("请输入用户名");
				}
				if (TextUtils.isEmpty(password)) {
					sender.sendFail("请输入密码");
				}
				if (name.equals("aaa") && password.equals("111")) {
					sender.sendData(new User("1", "aaa", 23, "中国人"));
				} else {
					sender.sendFail("用户名或者密码不正确");
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				sender.sendError(error);
			}
		});
		MyVolley.getRequestQueue().add(jsonObjRequest);
		return new VolleyRequestHandle(jsonObjRequest);
	}

}
