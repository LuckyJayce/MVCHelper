package com.shizhefei.test.models.task;

import android.text.TextUtils;

import com.shizhefei.mvc.ProgressSender;
import com.shizhefei.task.ITask;
import com.shizhefei.test.models.enties.User;
import com.shizhefei.test.models.exception.BizException;
import com.shizhefei.utils.HttpUtils;

public class LoginTask implements ITask<User>{
    private String name;
    private String password;

    public LoginTask(String name, String password) {
        super();
        this.name = name;
        this.password = password;
    }

    @Override
    public User execute(ProgressSender progressSender) throws Exception {
        // 这里用百度首页模拟网络请求，如果网路出错的话，直接抛异常不会执行后面语句
        HttpUtils.executeGet("http://www.baidu.com");
        Thread.sleep(300);
        if (TextUtils.isEmpty(name)) {
            throw new BizException("请输入用户名");
        }
        if (TextUtils.isEmpty(password)) {
            throw new BizException("请输入密码");
        }
        if (name.equals("aaa") && password.equals("111")) {
            return new User("1", "aaa", 23, "中国人");
        } else {
            throw new BizException("用户名或者密码不正确");
        }
    }

    @Override
    public void cancel() {

    }
}
