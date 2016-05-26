package com.shizhefei.test.models.datasource;

import android.util.Log;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.test.models.enties.Book;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BooksAsyncDataSource implements IAsyncDataSource<List<Book>> {
    private int mPage;
    private int mMaxPage = 5;

    public BooksAsyncDataSource() {
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
        Log.d("xxxx", "hasMore mMaxPage:" + mMaxPage + " mPage;" + mPage);
        return mPage < mMaxPage;
    }

    private RequestHandle loadBooks(final ResponseSender<List<Book>> sender, final int page) throws Exception {
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        formEncodingBuilder.add("api_key", "75ee6c644cad38dc8e53d3598c8e6b6c");
        Request request = new Request.Builder()
                .url("http://www.baidu.com").post(formEncodingBuilder.build())
                .build();

        Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                sender.sendError(e);
                Log.d("xxxx","error",e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final  List<Book> books = new ArrayList<Book>();
                for (int i = 0; i < 15; i++) {
                    books.add(new Book("page" + page + "  Java编程思想 " + i, 108.00d));
                }
                mPage = page;
                sender.sendData(books);
            }
        });
        return new OKHttpRequestHandle(call);
    }
}
