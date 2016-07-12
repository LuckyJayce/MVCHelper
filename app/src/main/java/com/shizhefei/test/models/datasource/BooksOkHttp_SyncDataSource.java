package com.shizhefei.test.models.datasource;

import com.shizhefei.mvc.IDataSource;
import com.shizhefei.mvc.http.okhttp.GetMethod;
import com.shizhefei.mvc.http.okhttp.ResponseParser;
import com.shizhefei.test.models.enties.Book;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
 * 这是封装OKHttp请求的演示代码
 */
public class BooksOkHttp_SyncDataSource implements IDataSource<List<Book>> {
    private int mPage;
    private int mMaxPage = 5;

    @Override
    public List<Book> refresh() throws Exception {
        return loadBooks(1);
    }

    @Override
    public List<Book> loadMore() throws Exception {
        return loadBooks(mPage + 1);
    }

    @Override
    public boolean hasMore() {
        return mPage < mMaxPage;
    }

    private List<Book> loadBooks(final int page) throws Exception {
        GetMethod method = new GetMethod("https://www.baidu.com");
        method.addHeader("a", "aaaaa");
        method.addParam("api_key", "75ee6c644cad38dc8e53d3598c8e6b6c");
        List<Book> data = method.executeSync(new ResponseParser<List<Book>>() {
            @Override
            public List<Book> parse(Response response) throws Exception {
                Thread.sleep(1000);
                List<Book> books = new ArrayList<Book>();
                for (int i = 0; i < 15; i++) {
                    books.add(new Book("page" + page + "  Java编程思想 " + i, 108.00d));
                }
                mPage = page;
                return books;
            }
        });
        return data;
    }
}
