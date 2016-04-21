package testcase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvchelper.testcase.ABSTestCaseFragment;
import com.shizhefei.mvchelper.testcase.TestCaseData;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.ProgressSender;
import com.shizhefei.task.ResponseSender;
import com.shizhefei.test.models.datasource.BookDetailDataSource;
import com.shizhefei.test.models.datasource.BooksAsyncDataSource;
import com.shizhefei.test.models.datasource.SearchBookDataSource;
import com.shizhefei.test.models.task.LoginAsyncTask;
import com.shizhefei.utils.ArrayListMap;

public class TestCaseFragment extends ABSTestCaseFragment {

	@Override
	protected List<TestCaseData> getTestCaseDatas() {
		List<TestCaseData> datas = new ArrayList<TestCaseData>();
		TestCaseData caseData = new TestCaseData("测试登录", new LoginAsyncTask("LuckyJayce", "111"));
		caseData.addParamGet(new String[] { "name", "password" }, new IAsyncTask<Map<String, String>, String>() {

			@Override
			public RequestHandle execute(ResponseSender<Map<String, String>, String> sender, ProgressSender progressSender) throws Exception {
				Map<String, String> map = new ArrayListMap<String, String>();
				map.put("name", "nnnnn");
				map.put("password", "ppppp");
				sender.sendData(map);
				return new RequestHandle() {

					@Override
					public boolean isRunning() {
						return false;
					}

					@Override
					public void cancle() {

					}
				};
			}
		});
		datas.add(caseData);
		datas.add(new TestCaseData("测试详情页", new BookDetailDataSource()));
		datas.add(new TestCaseData("测试搜索文章", new SearchBookDataSource("Java")));
		datas.add(new TestCaseData("测试文章列表", new BooksAsyncDataSource()));
		return datas;
	}

}
