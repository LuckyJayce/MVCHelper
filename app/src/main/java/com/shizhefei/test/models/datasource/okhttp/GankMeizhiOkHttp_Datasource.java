package com.shizhefei.test.models.datasource.okhttp;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.mvc.http.UrlBuilder;
import com.shizhefei.mvc.http.okhttp.GetMethod;
import com.shizhefei.test.models.enties.Meizhi;
import com.shizhefei.test.models.parser.MeizhiParser;

import java.util.List;

import okhttp3.Response;

/**
 * Created by LuckyJayce on 2016/7/11.
 */
public class GankMeizhiOkHttp_Datasource implements IAsyncDataSource<List<Meizhi>> {
    private int mPage = 1;

    @Override
    public RequestHandle refresh(ResponseSender<List<Meizhi>> sender) throws Exception {
        return load(sender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<Meizhi>> sender) throws Exception {
        return load(sender, mPage + 1);
    }

    private RequestHandle load(ResponseSender<List<Meizhi>> sender, final int page) {
        // http://gank.io/api/data/Android/10/1
        String url = new UrlBuilder("http://gank.io/api/data").sp("福利").sp("10").sp(page).build();
        GetMethod method = new GetMethod(url);
        method.executeAsync(sender, new MeizhiParser<List<Meizhi>>() {
            @Override
            protected void onParse(Response responses, List<Meizhi> meizhis) {
                mPage = page;
            }
        });
        return method;
    }

    @Override
    public boolean hasMore() {
        return true;
    }
}
