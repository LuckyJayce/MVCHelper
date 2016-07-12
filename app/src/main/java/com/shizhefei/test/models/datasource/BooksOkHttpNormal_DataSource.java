package com.shizhefei.test.models.datasource;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.test.models.enties.Book;
import com.shizhefei.utils.OkHttpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 这是没有封装OKHttp请求的演示代码
 */
public class BooksOkHttpNormal_DataSource implements IAsyncDataSource<List<Book>> {
    private int mPage;
    private int mMaxPage = 5;

    public BooksOkHttpNormal_DataSource() {
        super();
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<Book>> sender) throws Exception {
        return loadBooks(sender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<Book>> sender) throws Exception {
        return loadBooks(sender, mPage + 1);
    }

    @Override
    public boolean hasMore() {
        return mPage < mMaxPage;
    }

    private RequestHandle loadBooks(final ResponseSender<List<Book>> sender, final int page) throws Exception {
        //这里只是简单的演示OKhttp，你可以再封装哦
        FormBody.Builder formEncodingBuilder = new FormBody.Builder();
        formEncodingBuilder.add("api_key", "75ee6c644cad38dc8e53d3598c8e6b6c");
        Request request = new Request.Builder()
                .url("https://www.baidu.com").post(formEncodingBuilder.build())
                .build();
        Call call = OkHttpUtils.client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                //send 要放在最后一句(请求结束)
                sender.sendError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final List<Book> books = new ArrayList<Book>();
                for (int i = 0; i < 15; i++) {
                    books.add(new Book("page" + page + "  Java编程思想 " + i, 108.00d));
                }
                mPage = page;

                //send 要放在最后一句(请求结束)
                sender.sendData(books);
            }
        });
        return new OKHttpRequestHandle(call);
    }
}
