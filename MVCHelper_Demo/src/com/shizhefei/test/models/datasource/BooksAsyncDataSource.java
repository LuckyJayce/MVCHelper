package com.shizhefei.test.models.datasource;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.test.models.enties.Book;

public class BooksAsyncDataSource implements IAsyncDataSource<List<Book>> {
	private int mPage;
	private int mMaxPage = 5;

	private static AsyncHttpClient client = new AsyncHttpClient();

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
		Log.d("xxxx", "hasMore mMaxPage:"+mMaxPage+" mPage;"+mPage);
		return mPage < mMaxPage;
	}

	private RequestHandle loadBooks(final ResponseSender<List<Book>> sender, final int page) throws Exception {
		String url = "http://www.baidu.com";
		RequestParams params = new RequestParams();
		params.put("api_key", "75ee6c644cad38dc8e53d3598c8e6b6c");
		return new AsyncRequestHandle(client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				List<Book> books = new ArrayList<Book>();
				for (int i = 0; i < 4; i++) {
					books.add(new Book("page" + page + "  Java编程思想 " + i, 108.00d));
				}
				mPage = page;
				sender.sendData(books);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				List<Book> books = new ArrayList<Book>();
				for (int i = 0; i < 4; i++) {
					books.add(new Book("page" + page + "  Java编程思想 " + i, 108.00d));
				}
				mPage = page;
				sender.sendData(books);
			}
		}));
	}
}
