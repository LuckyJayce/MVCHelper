package com.shizhefei.test.models.datasource;

import com.shizhefei.mvc.IDataSource;
import com.shizhefei.test.models.enties.Movie;
import com.shizhefei.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LuckyJayce on 2016/7/20.
 */
public class MoviesDataSource implements IDataSource<List<Movie>> {

    private int moviePage;

    @Override
    public List<Movie> refresh() throws Exception {
        return load(1);
    }

    @Override
    public List<Movie> loadMore() throws Exception {
        return load(moviePage + 1);
    }

    @Override
    public boolean hasMore() {
        return moviePage < 5;
    }

    private List<Movie> load(int page) throws Exception {
        // 这里用百度首页模拟网络请求，如果网路出错的话，直接抛异常不会执行后面的获取books的语句
        HttpUtils.executeGet("https://www.baidu.com");
        Thread.sleep(300);
        List<Movie> movies = new ArrayList<Movie>();
        for (int i = 0; i < 20; i++) {
            movies.add(new Movie("海贼王第" + (20 * (page - 1) + i + 1) + " 集", i, "page" + page + " 海贼王剧情简介 " + i, "00:30"));
        }
        this.moviePage = page;
        return movies;
    }
}
