package testcase;

import java.util.ArrayList;
import java.util.List;

import com.shizhefei.mvchelper.testcase.ABSTestCaseFragment;
import com.shizhefei.mvchelper.testcase.TestCaseData;
import com.shizhefei.test.models.datasource.BookDetailDataSource;
import com.shizhefei.test.models.datasource.BooksAsyncDataSource;
import com.shizhefei.test.models.datasource.SearchBookDataSource;
import com.shizhefei.test.models.task.LoginAsyncTask;

public class TestCaseFragment extends ABSTestCaseFragment {

	@Override
	protected List<TestCaseData> getTestCaseDatas() {
		List<TestCaseData> datas = new ArrayList<TestCaseData>();
		datas.add(new TestCaseData("测试登录", new LoginAsyncTask("LuckyJayce", "111")));
		datas.add(new TestCaseData("测试详情页", new BookDetailDataSource()));
		datas.add(new TestCaseData("测试搜索文章", new SearchBookDataSource("Java")));
		datas.add(new TestCaseData("测试文章列表", new BooksAsyncDataSource()));
		return datas;
	}

}
