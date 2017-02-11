package com.shizhefei.test.models.datasource;

import com.shizhefei.mvc.IDataSource;
import com.shizhefei.test.models.enties.Book;
import com.shizhefei.test.view.adapters.multitype.Message;

import java.util.ArrayList;
import java.util.List;

public class MyDataSource implements IDataSource<List<Object>> {
    private boolean hasMore = true;

    @Override
    public List<Object> refresh() throws Exception {
        Thread.sleep(1000);
        List<Object> list = new ArrayList<>();
        list.add(new Message("1", "你知道你这次比赛用了多少时间么?"));
        list.add(new Message("2", "不知道"));
        list.add(new Message("1", "58秒95"));
        list.add(new Message("2", "58秒95？"));
        list.add(new Message("2", "自己都没想到自己"));
        list.add(new Message("2", "我以为是59秒"));
        list.add(new Message("2", "啊～～我有这么快？？"));
        list.add(new Message("2", "我很满意"));
        list.add(new Message("1", "今天这个状态有所保留么？"));
        list.add(new Message("2", "没有保留！我已经，我已经用了洪荒之力啦！"));
        list.add(new Message("1", "是不是对明天的决赛充满希望"));
        list.add(new Message("2", "我已经很满意啦。"));
        list.add(new Message("1", "明天加油"));
        list.add(new Message("2", "啦。。。啦。。。"));
        hasMore = true;
        return list;
    }

    @Override
    public List<Object> loadMore() throws Exception {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(new Book("Java编程思想 " + i, 100));
        }
        hasMore = false;
        return list;
    }

    @Override
    public boolean hasMore() {
        return hasMore;
    }
}