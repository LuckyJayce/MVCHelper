/*
Copyright 2015 shizhefei（LuckyJayce）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.shizhefei.test.models.datasource;

import com.shizhefei.mvc.IDataSource;
import com.shizhefei.test.models.enties.Book;
import com.shizhefei.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchBookDataSource implements IDataSource<List<Book>> {
	private int page = 1;
	private int maxPage = 10;

	private String keyword;

	public SearchBookDataSource(String keyword) {
		super();
		this.keyword = keyword;
	}

	@Override
	public List<Book> refresh() throws Exception {
		return loadBooks(1);
	}

	@Override
	public List<Book> loadMore() throws Exception {
		return loadBooks(page + 1);
	}

	private List<Book> loadBooks(int page) throws Exception {
		// 这里用百度首页模拟网络请求，如果网路出错的话，直接抛异常不会执行后面的获取books的语句
		HttpUtils.executeGet("http://www.baidu.com");
		Thread.sleep(1000);

		List<Book> books = new ArrayList<Book>();
		for (int i = 0; i < 20; i++) {
			books.add(new Book("page" + page + " " + keyword + i, 108.00d));
		}
		this.page = page;
		return books;
	}

	@Override
	public boolean hasMore() {
		return page < maxPage;
	}
}
