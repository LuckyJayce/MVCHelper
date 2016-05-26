package com.shizhefei.test.models.datasource;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.test.models.enties.Book;
import com.shizhefei.utils.OkHttpUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BooksOkHttpDataSource implements IAsyncDataSource<List<Book>> {
    private int mPage;
    private int mMaxPage = 5;

    public BooksOkHttpDataSource() {
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
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        formEncodingBuilder.add("api_key", "75ee6c644cad38dc8e53d3598c8e6b6c");
        Request request = new Request.Builder()
                .url("https://www.baidu.com").post(formEncodingBuilder.build())
                .build();

        Call call = OkHttpUtils.client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                //send 要放在最后一句(请求结束)
                sender.sendError(e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final  List<Book> books = new ArrayList<Book>();
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
