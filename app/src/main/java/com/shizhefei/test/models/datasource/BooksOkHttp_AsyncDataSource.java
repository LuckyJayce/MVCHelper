package com.shizhefei.test.models.datasource;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.mvc.http.okhttp.GetMethod;
import com.shizhefei.mvc.http.okhttp.ResponseParser;
import com.shizhefei.test.models.enties.Book;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
 * 这是封装OKHttp请求的演示代码
 */
public class BooksOkHttp_AsyncDataSource implements IAsyncDataSource<List<Book>> {
    private int mPage;
    private int mMaxPage = 5;

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
        GetMethod method = new GetMethod("https://www.baidu.com");
        method.addHeader("a", "aaaaa");
        method.addParam("api_key", "75ee6c644cad38dc8e53d3598c8e6b6c");
        method.executeAsync(sender, new ResponseParser<List<Book>>() {
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
//      下面演示正常解析
//        method.executeAsync(sender, new ResponseParser<List<Book>>() {
//            @Override
//            public List<Book> parse(Response response) throws Exception {
//                if (response.isSuccessful()) {
//                    String json = response.body().string();
//                    Type type = new TypeToken<List<Book>>() {
//                    }.getType();
//                    List<Book> books = new Gson().fromJson(json, type);
//                    mPage = page;
//                    return books;
//                }
//                如果code不是200-300之间，向外抛出异常，之后会调用ResponseSender.sendError(e)  -> LoadView.showFail(e);
//                throw new Exception("fail httpcode:" + response.code());
//            }
//        });
        return method;
    }
}
