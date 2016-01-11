###MVCHelper更倾向于获取数据并显示数据，比较适用于http的get请求。

###TaskHelper更倾向于任务的执行的失败，成功，取消，比较适用于http的post请求

#一、 MVCHelper
MVCHelper. 实现下拉刷新，滚动底部自动加载更多，分页加载，自动切换显示网络失败布局，暂无数据布局，,真正的MVC架构.  
Download Library [JAR](https://github.com/LuckyJayce/MVCHelper/files/76209/LuckyJayce_MVCHelper_1.0.0.zip)  
Download sample [Apk](https://github.com/LuckyJayce/MVCHelper/blob/master/raw/MVCHelper_Demo.apk?raw=true)  

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
	public interface IDataAdapter<DATA> {
	
		public abstract void notifyDataChanged(DATA data, boolean isRefresh);
	
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

#二、 TaskHelper
## 1.Model (Task<SUCCESS, FAIL>)
	/**
	 * @param <SUCCESS>
	 *            成功的数据类型
	 * @param <FAIL>
	 *            失败的数据类型
	 */
	public interface Task<SUCCESS, FAIL> {
	
		/**
		 * 执行后台任务
		 * 
		 * @param progressSender
		 *            进度更新发送者
		 * @return
		 * @throws Exception
		 */
		public Data<SUCCESS, FAIL> execute(ProgressSender progressSender) throws Exception;
	
		/**
		 * 注意cancle 和 execute 有可能不在同一个线程，cancle可能在UI线程被调用
		 */
		public void cancle();
	
	}

例如登陆

	public class LoginTask implements Task<User, String> {
		private String name;
		private String password;
	
		public LoginTask(String name, String password) {
			super();
			this.name = name;
			this.password = password;
		}
	
		@Override
		public Data<User, String> execute(ProgressSender progressSender) throws Exception {
			if (name.equals("aaa") && password.equals("111")) {
				return Data.madeSuccess(new User("1", "aaa", 23, "中国人"));
			} else {
				return Data.madeFail("用户名或者密码不正确");
			}
		}
	
		@Override
		public void cancle() {
	
		}
	
	}
## 2.View（Callback<SUCCESS, FAIL>）
	/**
	 *
	 * @param <SUCCESS>
	 *            执行成功返回的数据类型
	 * @param <FAIL>
	 *            执行失败返回的数据类型
	 */
	public interface Callback<SUCCESS, FAIL> {
	
		/**
		 * 执行task之前的回调
		 */
		public void onPreExecute();
	
		/**
		 * 进度更新回调
		 * 
		 * @param percent
		 * @param current
		 * @param total
		 * @param exraData
		 */
		public void onProgressUpdate(int percent, long current, long total, Object exraData);
	
		/**
		 * 执行task结束的回调，通过code判断是什么情况结束task，（成功，失败，异常，取消）
		 * 
		 * @param code
		 *            返回码
		 * @param exception
		 *            异常信息（throw exception 时才有值）
		 * @param success
		 *            成功返回的数据
		 * @param fail
		 *            失败返回的数据
		 */
		public void onPostExecute(Code code, Exception exception, SUCCESS success, FAIL fail);
	
	}

例如登陆请求更新UI

	private Callback<User, String> loginCallback = new Callback<User, String>() {
			@Override
			public void onPreExecute() {
				loginButton.setEnabled(false);
				loginButton.setText("登陆中...");
			}
	
			@Override
			public void onProgressUpdate(int percent, long current, long total, Object exraData) {
	
			}
	
			@Override
			public void onPostExecute(Code code, Exception exception, User success, String fail) {
				loginButton.setEnabled(true);
				loginButton.setText("登陆");
				switch (code) {
				case FAIL:
				case EXCEPTION:
					if (TextUtils.isEmpty(fail)) {
						Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(), fail, Toast.LENGTH_SHORT).show();
					}
					break;
				case SUCESS:
					Toast.makeText(getApplicationContext(), "登陆成功：" + new Gson().toJson(success), Toast.LENGTH_LONG).show();
					break;
				default:
					break;
				}
			}
		};

## 3.Controller (Activity,Fragment,TaskHelper)

Activity负责调度，代码如下
	
	loginHelper.setTask(new LoginTask(name, password));
	loginHelper.setCallback(loginCallback);
	loginHelper.execute();

	//loginHelper.cancle();//执行取消操作
	//loginHelper.destory();//执行释放操作

## 三、说明

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
