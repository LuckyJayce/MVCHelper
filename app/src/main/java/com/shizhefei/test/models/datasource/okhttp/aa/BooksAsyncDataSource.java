package com.shizhefei.test.models.datasource.okhttp.aa;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.test.models.enties.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是封装OKHttp请求的演示代码
 */
public class BooksAsyncDataSource implements IAsyncDataSource<List<Book>> {
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
        LoadBooksThread thread = new LoadBooksThread(sender, page);
        thread.start();
        return thread;
    }

    private class LoadBooksThread extends Thread implements RequestHandle {
        private final int page;
        private ResponseSender<List<Book>> sender;

        public LoadBooksThread(ResponseSender<List<Book>> sender, int page) {
            this.sender = sender;
            this.page = page;
        }

        @Override
        public void run() {
            super.run();
            try {
                List<Book> books = new ArrayList<>();
                for (int i = 0; i < 100; i++) {
                    //请求被取消，直接退出方法
                    if (cancel) {
                        return;
                    }
                    books.add(new Book("Book" + page + "-" + i, 1));
                }
                //返回数据出去，执行结束
                sender.sendData(books);
            } catch (Exception e) {
                //抛出异常出去，执行结束
                sender.sendError(e);
            }
        }

        private boolean cancel;

        //实现 RequestHandle 取消请求的方法
        @Override
        public void cancle() {
            cancel = true;
        }

        @Override
        public boolean isRunning() {
            //这个可以不用写
            return false;
        }
    }
}
