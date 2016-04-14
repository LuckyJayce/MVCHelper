package com.shizhefei.mvchelper.testcase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map.Entry;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shizhefei.recyclerview.HFAdapter;
import com.shizhefei.recyclerview.HFAdapter.OnItemClickListener;
import com.shizhefei.task.AsyncDataSourceProxyTask;
import com.shizhefei.task.Callback;
import com.shizhefei.task.Code;
import com.shizhefei.task.DataSourceProxyTask;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.task.Task;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.utils.ArrayListMap;

public abstract class ABSTestCaseFragment extends Fragment {
	private RecyclerView paramsRecyclerView;
	private TextView resultTextView;
	private Button runButton;
	private Button resetButton;
	private List<TestCaseData> datas;
	private Gson gson;
	private TasksAdapter tasksAdapter;
	private RecyclerView recyclerView;
	private View itemRunButton;
	private ParamsAdapter paramsAdapter;
	private LayoutInflater inflater;
	private TextView resultStateTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.testcase, container, false);

		GsonBuilder builder = new GsonBuilder();
		builder.addSerializationExclusionStrategy(new ExclusionStrategy() {
			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				// // 这里作判断，决定要不要排除该字段,return true为排除
				// if ("finalField".equals(f.getName())) return true; //按字段名排除
				// Expose expose = f.getAnnotation(Expose.class);
				// if (expose != null && expose.deserialize() == false) return
				// true; //按注解排除
				return false;
			}

			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				// 直接排除某个类 ，return true为排除
				return clazz == Gson.class || clazz == Bitmap.class;
			}
		}).create();
		gson = builder.create();

		recyclerView = (RecyclerView) view.findViewById(R.id.testcase2_recyclerView);
		paramsRecyclerView = (RecyclerView) view.findViewById(R.id.testcase2_params_recyclerView);
		resultTextView = (TextView) view.findViewById(R.id.testcase2_result_textView);
		runButton = (Button) view.findViewById(R.id.testcase2_run_button);
		resetButton = (Button) view.findViewById(R.id.testcase2_reset_button);
		itemRunButton = view.findViewById(R.id.testcase2_run2_button);
		resultStateTextView = (TextView) view.findViewById(R.id.testcase2_resultState_textView);

		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setAdapter(tasksAdapter = new TasksAdapter());
		// recyclerView.addItemDecoration(new
		// DividerItemDecoration(getContext()));

		paramsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		// paramsRecyclerView.addItemDecoration(new
		// DividerItemDecoration(getContext()));
		paramsRecyclerView.setAdapter(paramsAdapter = new ParamsAdapter());

		datas = getTestCaseDatas();

		resetButton.setOnClickListener(onClickListener);
		runButton.setOnClickListener(onClickListener);
		itemRunButton.setOnClickListener(onClickListener);
		tasksAdapter.setOnItemClickListener(onItemClickListener);
		resultTextView.setOnClickListener(onClickListener);

		onItemClickListener.onItemClick(tasksAdapter, null, selectPosition);

		return view;
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == runButton) {
				TestCaseData data = datas.get(0);
				exe(0, data, true);
			} else if (v == resetButton) {
				if (taskHelper2 != null) {
					taskHelper2.cancle();
				}
				datas = getTestCaseDatas();
				tasksAdapter.notifyDataSetChanged();
				onItemClickListener.onItemClick(tasksAdapter, null, selectPosition);
			} else if (v == itemRunButton) {
				hideSoftKeyboard(itemRunButton);
				TestCaseData data = datas.get(selectPosition);
				data.task = paramsAdapter.getTask();
				exe(selectPosition, data, false);
			} else if (v == resultTextView) {
				if (resultTextView.getText().length() == 0) {
					return;
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setItems(new String[] { "复制", "分享" }, new AlertDialog.OnClickListener() {

					@TargetApi(Build.VERSION_CODES.HONEYCOMB)
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String text = resultTextView.getText().toString();
						if (which == 0) {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
								cmb.setText(text);
							} else {
								android.text.ClipboardManager manager = (android.text.ClipboardManager) getContext().getSystemService(
										Context.CLIPBOARD_SERVICE);
								manager.setText(text);
							}
							Toast.makeText(getContext(), "复制成功", Toast.LENGTH_SHORT).show();
						} else {
							Intent intent = new Intent(Intent.ACTION_SEND);
							intent.setType("text/plain"); // 纯文本
							intent.putExtra(Intent.EXTRA_TEXT, text);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(Intent.createChooser(intent, "分享"));
						}
					}
				});
				builder.show();
			}
		}
	};

	protected abstract List<TestCaseData> getTestCaseDatas();

	private class ParamsAdapter extends HFAdapter {

		private ArrayListMap<String, Object> map = new ArrayListMap<String, Object>();
		private ArrayListMap<String, Object> map2 = new ArrayListMap<String, Object>();
		private Object task;

		@Override
		public ViewHolder onCreateViewHolderHF(ViewGroup viewGroup, int type) {
			return new ItemViewHolder(inflater.inflate(R.layout.testcase_param_item, viewGroup, false));
		}

		@Override
		public void onBindViewHolderHF(ViewHolder vh, int position) {
			ItemViewHolder holder = (ItemViewHolder) vh;
			Object value = map.valueAt(position);
			String key = map.keyAt(position);
			holder.setData(key, value);
		}

		@Override
		public int getItemCountHF() {
			return map.size();
		}

		private void setData(Object task) {
			this.task = task;
			String json;
			if (task instanceof DataSourceProxyTask) {
				DataSourceProxyTask sourceTask = (DataSourceProxyTask) task;
				json = gson.toJson(sourceTask.getDatasource());
			} else if (task instanceof AsyncDataSourceProxyTask) {
				AsyncDataSourceProxyTask sourceTask = (AsyncDataSourceProxyTask) task;
				json = gson.toJson(sourceTask.getDatasource());
			} else {
				json = gson.toJson(task);
			}
			map = gson.fromJson(json, map.getClass());
			map2 = new ArrayListMap<String, Object>(map);
		}

		private Object getTask() {
			Object object;
			if (task instanceof DataSourceProxyTask) {
				DataSourceProxyTask sourceTask = (DataSourceProxyTask) task;
				object = sourceTask.getDatasource();
			} else if (task instanceof AsyncDataSourceProxyTask) {
				AsyncDataSourceProxyTask sourceTask = (AsyncDataSourceProxyTask) task;
				object = sourceTask.getDatasource();
			} else {
				object = task;
			}
			for (Entry<String, Object> data : map2.entrySet()) {
				try {
					Field field = object.getClass().getDeclaredField(data.getKey());
					field.setAccessible(true);
					field.set(object, data.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return task;
		}

		private class ItemViewHolder extends ViewHolder {

			private TextView keyTextView;
			private EditText valueEditText;
			private String key;

			public ItemViewHolder(View itemView) {
				super(itemView);
				keyTextView = (TextView) itemView.findViewById(R.id.textView1);
				valueEditText = (EditText) itemView.findViewById(R.id.editText1);
			}

			public void setData(String key, Object value) {
				this.key = key;
				keyTextView.setText(key);
				valueEditText.removeTextChangedListener(textWatcher);
				valueEditText.setText(String.valueOf(value));
				valueEditText.addTextChangedListener(textWatcher);
			}

			private TextWatcher textWatcher = new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {
					String text = s.toString();
					Object value = map.get(key);
					Object newValue = gson.fromJson(text, value.getClass());
					map2.put(key, newValue);
				}
			};
		}

	}

	private int selectPosition = 0;

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(HFAdapter adapter, ViewHolder vh, int position) {
			TestCaseData data = datas.get(position);
			// paramsRecyclerView.setText(param(data));
			resultTextView.setText(data.result);
			selectPosition = position;
			paramsAdapter.setData(data.task);
			switch (data.status) {
			case -1:
				resultStateTextView.setText("ERROR");
				break;
			case 0:
				resultStateTextView.setText("");
				break;
			case 1:
				resultStateTextView.setText("RUNING");
				break;
			case 2:
				resultStateTextView.setText("SUCCESS");
				break;
			}
			paramsAdapter.notifyDataSetChanged();
			adapter.notifyDataSetChangedHF();
		}
	};

	private TaskHelper taskHelper2;

	private class TasksAdapter extends HFAdapter {

		@Override
		public ViewHolder onCreateViewHolderHF(ViewGroup viewGroup, int type) {
			return new ItemViewHolder(inflater.inflate(R.layout.testcase_item, viewGroup, false));
		}

		@Override
		public void onBindViewHolderHF(ViewHolder vh, int position) {
			ItemViewHolder holder = (ItemViewHolder) vh;
			holder.setData(position, datas.get(position));
		}

		@Override
		public int getItemCountHF() {
			return datas.size();
		}

		class ItemViewHolder extends ViewHolder {

			private Button b;
			private TestCaseData data;
			private int index;

			public ItemViewHolder(View itemView) {
				super(itemView);
			}

			private void setData(int index, TestCaseData data) {
				this.index = index;
				this.data = data;
				TextView textView = (TextView) itemView.findViewById(R.id.item_testcase2_textView);
				View stateView = itemView.findViewById(R.id.item_testcase2_state_view);
				if (index == selectPosition) {
					itemView.setBackgroundColor(Color.WHITE);
				} else {
					itemView.setBackgroundColor(Color.parseColor("#cccccc"));
				}
				switch (data.status) {
				case -1:
					stateView.setBackgroundColor(Color.RED);
					break;
				case 0:
					stateView.setBackgroundColor(Color.TRANSPARENT);
					break;
				case 1:
					stateView.setBackgroundResource(R.drawable.testcase_arrow);
					break;
				case 2:
					stateView.setBackgroundColor(Color.GREEN);
					break;
				}
				textView.setText(data.text);
			}
		}
	}

	private void exe(int index, TestCaseData data, boolean exeNext) {
		if (taskHelper2 != null && taskHelper2.isRunning()) {
			taskHelper2.cancle();
		}
		TaskHelper taskHelper = new TaskHelper();
		if (data.task instanceof Task) {
			taskHelper.setTask((Task) data.task);
		} else {
			taskHelper.setTask((IAsyncTask) data.task);
		}
		taskHelper.setCallback(new MyCallBack(index, data, exeNext));
		taskHelper.execute();

		taskHelper2 = taskHelper;
	}

	public class MyCallBack<DATA> implements Callback<Object, Object> {
		private int index = 0;
		private TestCaseData data;
		private boolean exeNext;

		public MyCallBack(int index, TestCaseData data, boolean exeNext) {
			this.index = index;
			this.data = data;
			this.exeNext = exeNext;
		}

		@Override
		public void onPreExecute() {
			data.status = 1;
			data.result = "";

			tasksAdapter.notifyDataSetChanged();
			onItemClickListener.onItemClick(tasksAdapter, null, selectPosition);
		}

		@Override
		public void onProgressUpdate(int percent, long current, long total, Object exraData) {

		}

		@Override
		public final void onPostExecute(Code code, Exception exception, Object success, Object fail) {
			switch (code) {
			case SUCESS:
				data.status = 2;
				data.result = gson.toJson(success);
				break;
			case EXCEPTION:
				data.status = -1;
				data.result = getStackTraceString(exception);
				break;
			case FAIL:
				data.status = -1;
				data.result = gson.toJson(fail);
				break;
			default:
				if (TextUtils.isEmpty(data.result)) {
					data.status = 0;
				}
				break;
			}
			tasksAdapter.notifyDataSetChanged();
			onItemClickListener.onItemClick(tasksAdapter, null, selectPosition);

			if (code != Code.CANCLE) {
				if (exeNext) {
					if (index + 1 < datas.size()) {
						TestCaseData newData = datas.get(index + 1);
						exe(index + 1, newData, true);
					}
				}
			}
		}

		public String getStackTraceString(Throwable e) {
			if (e == null) {
				return "";
			}
			StringWriter localStringWriter = new StringWriter();
			PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
			e.printStackTrace(localPrintWriter);
			return localStringWriter.toString();
		}

	}

	/**
	 * 隐藏软键盘
	 */
	public static void hideSoftKeyboard(View mEt) {
		((InputMethodManager) mEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mEt.getWindowToken(), 0);
	}
}
