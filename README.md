# MVCHelper
MVCHelper. 实现下拉刷新，滚动底部自动加载更多，分页加载，自动切换显示网络失败布局，暂无数据布局，,真正的MVC架构.
## 1.Model (IDataSource<DATA>)
        //数据源
	public interface IDataSource<DATA> {
		// 获取刷新的数据
		public DATA refresh() throws Exception;
	
		// 获取加载更多的数据
		public DATA loadMore() throws Exception;
	
		// 是否还可以继续加载更多
		public boolean hasMore();
	}
例如：分页加载书籍列表数据
	
	public class BooksDataSource implements IDataSource<List<Book>> {
		private int page = 1;
		private int maxPage = 5;
	
		@Override
		public List<Book> refresh() throws Exception {
			return loadBooks(1);
		}
	
		@Override
		public List<Book> loadMore() throws Exception {
			return loadBooks(page + 1);
		}
	
		private List<Book> loadBooks(int page) {
			List<Book> books = new ArrayList<Book>();
			for (int i = 0; i < 20; i++) {
				books.add(new Book("page" + page + "  Java编程思想 " + i, 108.00d));
			}
			this.page = page;
			return books;
		}
	
		@Override
		public boolean hasMore() {
			return page < maxPage;
		}

	}
## 2.View（IDataAdapter<DATA>）
	public interface IDataAdapter<DATA> extends ListAdapter {

		public abstract void notifyDataChanged(DATA data, boolean isRefresh)
	
		public abstract DATA getData();
	
		public boolean isEmpty();
	}

例如：分页显示书籍列表数据
		
	public class BooksAdapter extends BaseAdapter implements IDataAdapter<List<Book>> {
		private List<Book> books = new ArrayList<Book>();
		private LayoutInflater inflater;
	
		public BooksAdapter(Context context) {
			super();
			inflater = LayoutInflater.from(context);
		}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_book, parent, false);
			}
			TextView textView = (TextView) convertView;
			textView.setText(books.get(position).getName());
			return convertView;
		}
	
		@Override
		public void notifyDataChanged(List<Book> data, boolean isRefresh) {
			if (isRefresh) {
				books.clear();
			}
			books.addAll(data);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return books.size();
		}
	
		@Override
		public List<Book> getData() {
			return books;
		}


		@Override
		public Object getItem(int position) {
			return null;
		}
	
		@Override
		public long getItemId(int position) {
			return 0;
		}
	
	
	}
## 3.Controller (Activity,Fragment,MVCHelper)
Activity负责调度，代码如下
	
	public class MainActivity extends Activity {

		private MVCHelper<List<Book>> mvcHelper;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// 设置LoadView的factory，用于创建用户自定义的加载失败，加载中，加载更多等布局
			// ListViewHelper.setLoadViewFractory(new LoadViewFractory());
	
			PullToRefreshListView refreshListView = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView);
			mvcHelper = new MVCPullrefshHelper<List<Book>>(refreshListView);
	
			// 设置数据源
			mvcHelper.setDataSource(new BooksDataSource());
			// 设置适配器
			mvcHelper.setAdapter(new BooksAdapter(this));
	
			// 加载数据
			mvcHelper.refresh();
		}

		@Override
		protected void onDestroy() {
			super.onDestroy();
			// 释放资源
			mvcHelper.destory();
		}
	}

只要写了上述几行代码，恭喜你，你已经实现了分页加载显示书籍列表，实现下拉刷新，滚动底部自动加载更多，在网络请求失败的时候自动显示网络失败,没有数据时显示无数据布局，加载成功时显示书籍列表

## 4.ILoadViewFractory 自定义 失败布局，无数据布局，加载中布局 
实现ILoadViewFractory  
然后ListViewHelper.setLoadViewFractory(new LoadViewFractory());  
就这样，就会显示你自定义的布局

## 5.你可以自由的切换主流刷新类库 
1.用android-support-v4.jar 的SwipeRefreshLayout作为刷新框架（**MVCSwipeRefreshHelper）** 

		SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
		MVCHelper<List<Book>> mvcHelper = new MVCSwipeRefreshHelper<List<Book>>(swipeRefreshLayout);

		// 设置数据源
		mvcHelper.setDataSource(new BooksDataSource());
		// 设置适配器
		mvcHelper.setAdapter(new BooksAdapter(this));

		// 加载数据
		mvcHelper.refresh();

2.用Android-PullToRefresh-Library作为刷新框架**（MVCPullrefshHelper）**  
  地址：https://github.com/chrisbanes/Android-PullToRefresh

		PullToRefreshListView refreshListView = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView);
		MVCHelper<List<Book>> mvcHelper = new MVCPullrefshHelper<List<Book>>(refreshListView);

		// 设置数据源
		mvcHelper.setDataSource(new BooksDataSource());
		// 设置适配器
		mvcHelper.setAdapter(new BooksAdapter(this));

		// 加载数据
		mvcHelper.refresh();
3.用android-Ultra-Pull-To-Refresh-library作为刷新框架**（MVCUltraHelper）**  
  地址：https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh

		PtrClassicFrameLayout mPtrFrameLayout = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);

		MVCHelper<List<Book>> mvcHelper = new MVCUltraHelper<List<Book>>(mPtrFrameLayout);
		// 设置数据源
		mvcHelper.setDataSource(new BooksDataSource());
		// 设置适配器
		mvcHelper.setAdapter(new BooksAdapter(this));

		// 加载数据
		mvcHelper.refresh();

4.不使用刷新框架（**MVCNormalHelper**）

		View contentLayout = findViewById(R.id.content_layout);
		MVCHelper<Book> mvcHelper= new MVCNormalHelper<Book>(contentLayout);

		// 设置数据源
		mvcHelper.setDataSource(new BookDetailDataSource());
		// 设置适配器
		mvcHelper.setAdapter(dataAdapter);

		// 加载数据
		mvcHelper.refresh();
5.如果使用其他刷新框架的话可以继承MVCHelper自定义一个

## 6.不再局限于ListView的MVC
可以任意的View作为刷新的内容，并且提供相同的MVC架构操作  
果断支持RecyclerView的加载更多  
	
	/***
	 * 测试下拉组件的非列表界面
	 * 
	 * @author LuckyJayce
	 *
	 */
	public class BooDetailActivity extends Activity {
	
		private MVCHelper<Book> listViewHelper;
		private TextView authorTextView;
		private TextView contentTextView;
		private TextView descriptionTextView;
		private TextView nameTextView;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_bookdetail);
	
			nameTextView = (TextView) findViewById(R.id.name_textView);
			authorTextView = (TextView) findViewById(R.id.author_textView);
			descriptionTextView = (TextView) findViewById(R.id.description_textView);
			contentTextView = (TextView) findViewById(R.id.content_textView);
	
			PtrClassicFrameLayout contentLayout = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
			listViewHelper = new MVCUltraHelper<Book>(contentLayout);
	
			// 设置数据源
			listViewHelper.setDataSource(new BookDetailDataSource());
			// 设置适配器
			listViewHelper.setAdapter(dataAdapter);
	
			// 加载数据
			listViewHelper.refresh();
		}
	
		@Override
		protected void onDestroy() {
			super.onDestroy();
			// 释放资源
			listViewHelper.destory();
		}
	
		private IDataAdapter<Book> dataAdapter = new IDataAdapter<Book>() {
			private Book data;
	
			@Override
			public void notifyDataChanged(Book data, boolean isRefresh) {
				this.data = data;
				authorTextView.setText(data.getAuthor());
				contentTextView.setText(data.getContent());
				descriptionTextView.setText(data.getDescription());
				nameTextView.setText(data.getName());
			}
	
			@Override
			public boolean isEmpty() {
				return data == null;
			}
	
			@Override
			public Book getData() {
				return data;
			}
		};
	
		public void onClickBack(View view) {
			finish();
		}
	
	}

## 7.不再局限于返回值的个数

原先

	public class MovieDetailDataSource implements IDataSource<Movie>{
	
	}

使用Data1的可以传一个泛型返回值  
使用Data2的可以传两个泛型返回值  
使用Data3的可以传三个泛型参数  

	public class MovieDetailDataSource implements IDataSource<Data3<Movie, List<Discuss>, List<Movie>>>{
	
	}

## 9.说明

以下三个是目前支持的下拉刷新的第三方开源类库  
Android-PullToRefresh-Library  
android-Ultra-Pull-To-Refresh-library  
android-support-v4.jar  
  
以下三个是对应刷新类库的MVCHelper  
MVCPullrefshHelper  
MVCUltraHelper  
MVCSwipeRefreshHelper  

核心代码  
MVCHelper_Library

示例代码   
MVCHelper_Demo

License
=======

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
