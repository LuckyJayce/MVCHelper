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

import java.util.ArrayList;
import java.util.List;

import com.shizhefei.mvc.IDataSource;
import com.shizhefei.mvc.data.Data3;
import com.shizhefei.test.models.enties.Discuss;
import com.shizhefei.test.models.enties.Movie;
import com.shizhefei.utils.HttpUtils;

public class MovieDetailDataSource implements IDataSource<Data3<Movie, List<Discuss>, List<Movie>>> {

	private int bookPage = 0;
	private int maxBookPage = 3;

	private int moviePage = 0;
	private int maxmoviePage = 3;

	@Override
	public Data3<Movie, List<Discuss>, List<Movie>> refresh() throws Exception {
		Movie value1 = new Movie("海贼王第23集", 67.0,
				"哲普的出现，揭露了他和克利克都到过伟大的航路的事实，从阿金口中得知他们在伟大的航路碰上一个神秘的男人，他竟独力打败了五十艘海贼船，而就在克利克为抢夺哲普的航海日记及海上餐厅这艘船时，传说中鹰眼的男人出现了", "00:30");
		Data3<Movie, List<Discuss>, List<Movie>> data = new Data3<Movie, List<Discuss>, List<Movie>>(value1, loadDiscuss(1), null);
		bookPage = 1;
		moviePage = 0;
		return data;
	}

	@Override
	public Data3<Movie, List<Discuss>, List<Movie>> loadMore() throws Exception {
		if (bookPage < maxBookPage) {
			return new Data3<Movie, List<Discuss>, List<Movie>>(null, loadDiscuss(bookPage + 1), null);
		} else {
			return new Data3<Movie, List<Discuss>, List<Movie>>(null, null, loadMovies(moviePage + 1));
		}
	}

	private List<Discuss> loadDiscuss(int page) throws Exception {
		// 这里用百度首页模拟网络请求，如果网路出错的话，直接抛异常不会执行后面的获取books的语句
		HttpUtils.executeGet("http://www.baidu.com");
		Thread.sleep(300);

		List<Discuss> discusss = new ArrayList<Discuss>();
		for (int i = 0; i < 20; i++) {
			discusss.add(new Discuss("", "page" + page + " 精彩 " + i, System.currentTimeMillis()));
		}
		this.bookPage = page;
		return discusss;
	}

	private List<Movie> loadMovies(int page) throws Exception {
		// 这里用百度首页模拟网络请求，如果网路出错的话，直接抛异常不会执行后面的获取books的语句
		HttpUtils.executeGet("http://www.baidu.com");
		Thread.sleep(300);

		List<Movie> movies = new ArrayList<Movie>();
		for (int i = 0; i < 20; i++) {
			movies.add(new Movie("海贼王第" + (20 * (page - 1) + i + 1) + " 集", i, "page" + page + " 海贼王剧情简介 " + i, "00:30"));
		}
		this.moviePage = page;
		return movies;
	}

	@Override
	public boolean hasMore() {
		if (bookPage < maxBookPage) {
			return true;
		}
		if (moviePage < maxmoviePage) {
			return true;
		}
		return false;
	}

}
