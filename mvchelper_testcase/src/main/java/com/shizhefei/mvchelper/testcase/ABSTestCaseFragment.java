package com.shizhefei.mvchelper.testcase;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shizhefei.mvc.testcase.R;
import com.shizhefei.mvchelper.testcase.TestCaseData.IParamValuesNotify;
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class ABSTestCaseFragment extends Fragment {
	private LinearLayout paramsRecyclerView;
	private TextView resultTextView;
	private Button runButton;
	private Button resetButton;
	private List<TestCaseData> datas;
	private Gson gson;
	private TasksAdapter tasksAdapter;
	private RecyclerView recyclerView;
	private View itemRunButton;
	private LayoutInflater inflater;
	private TextView resultStateTextView;
	private ArrayListMap<String, ParamLine> lines = new ArrayListMap<String, ParamLine>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.testcase, container, false);

		GsonBuilder builder = new GsonBuilder();
		// 格式化输出
		builder.setPrettyPrinting();
		// builder.serializeNulls();
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
		paramsRecyclerView = (LinearLayout) view.findViewById(R.id.testcase2_params_recyclerView);
		resultTextView = (TextView) view.findViewById(R.id.testcase2_result_textView);
		runButton = (Button) view.findViewById(R.id.testcase2_run_button);
		resetButton = (Button) view.findViewById(R.id.testcase2_reset_button);
		itemRunButton = view.findViewById(R.id.testcase2_run2_button);
		resultStateTextView = (TextView) view.findViewById(R.id.testcase2_resultState_textView);

		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setAdapter(tasksAdapter = new TasksAdapter());
		// recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
		//
		// // paramsRecyclerView.setLayoutManager(new
		// // LinearLayoutManager(getContext()));
		// // paramsRecyclerView.addItemDecoration(new
		// // DividerItemDecoration(getContext()));
		// // paramsRecyclerView.setAdapter(paramsAdapter = new ParamsAdapter());

		datas = getTestCaseDatas();

		resetButton.setOnClickListener(onClickListener);
		runButton.setOnClickListener(onClickListener);
		itemRunButton.setOnClickListener(onClickListener);
		tasksAdapter.setOnItemClickListener(onItemClickListener);
		resultTextView.setOnClickListener(onClickListener);

		updateRight();

		return view;
	}

	private void updateRight() {
		TestCaseData data = datas.get(selectPosition);
		// paramsRecyclerView.setText(param(data));
		resultTextView.setText(data.result);
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
		// paramsAdapter.notifyDataSetChanged();
		tasksAdapter.notifyDataSetChangedHF();

		String json;
		if (data.task instanceof DataSourceProxyTask) {
			DataSourceProxyTask sourceTask = (DataSourceProxyTask) data.task;
			json = gson.toJson(sourceTask.getDatasource());
		} else if (data.task instanceof AsyncDataSourceProxyTask) {
			AsyncDataSourceProxyTask sourceTask = (AsyncDataSourceProxyTask) data.task;
			json = gson.toJson(sourceTask.getDatasource());
		} else {
			json = gson.toJson(data.task);
		}
		map2 = gson.fromJson(json, map2.getClass());

		Object task = data.task;
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

		for (ParamLine paramLine : lines.values()) {
			paramLine.cancel();
		}
		lines.clear();

		paramsRecyclerView.removeAllViews();
		for (int i = 0; i < map2.size(); i++) {
			ParamLine paramLine = new ParamLine();
			View itemView = inflater.inflate(R.layout.testcase_param_item, paramsRecyclerView, false);
			paramsRecyclerView.addView(itemView);
			paramLine.itemView = itemView;
			paramLine.keyTextView = (TextView) itemView.findViewById(R.id.textView1);
			paramLine.valueEditText = (EditText) itemView.findViewById(R.id.editText1);
			paramLine.valueGetButton = (Button) itemView.findViewById(R.id.testcase_param_item_paramGet_button);
			Object value = map2.valueAt(i);
			String key = map2.keyAt(i);

			paramLine.key = key;
			paramLine.keyTextView.setText(key);
			paramLine.valueEditText.removeTextChangedListener(paramLine.textWatcher);
			paramLine.valueEditText.setText(String.valueOf(value));
			paramLine.valueEditText.addTextChangedListener(paramLine.textWatcher);
			paramLine.valueGetButton.setOnClickListener(paramLine.onClickListener);
			boolean has = false;
			if (data.paramGets.containsKey(key)) {
				has = true;
				paramLine.paramGetTask = data.paramGets.get(key);
			}
			if (!has) {
				out: for (Entry<String[], IAsyncTask<Map<String, String>, String>> entry : data.paramGetsMap.entrySet()) {
					for (String param : entry.getKey()) {
						if (param.equals(key)) {
							has = true;
							paramLine.paramGetTaskMap = entry.getValue();
							break out;
						}
					}
				}
			}
			if (has) {
				paramLine.valueGetButton.setVisibility(View.VISIBLE);
			} else {
				paramLine.valueGetButton.setVisibility(View.GONE);
			}

			try {
				Field field = object.getClass().getDeclaredField(key);
				field.setAccessible(true);
				paramLine.field = field;
			} catch (Exception e) {
				e.printStackTrace();
			}

			lines.put(key, paramLine);
		}
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
				updateRight();
			} else if (v == itemRunButton) {
				hideSoftKeyboard(itemRunButton);
				updateTastParams();
				exe(selectPosition, datas.get(selectPosition), false);
			} else if (v == resultTextView) {
				if (resultTextView.getText().length() == 0) {
					return;
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setItems(new String[] { "复制", "分享", "全屏" }, new AlertDialog.OnClickListener() {

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
						} else if (which == 1) {
							Intent intent = new Intent(Intent.ACTION_SEND);
							intent.setType("text/plain"); // 纯文本
							intent.putExtra(Intent.EXTRA_TEXT, text);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(Intent.createChooser(intent, "分享"));
						} else {
							final Dialog dialog2 = new Dialog(getActivity(), R.style.testCase_dialog);
							dialog2.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
							dialog2.setContentView(R.layout.testcase_fullscreen);

							TextView textView = (TextView) dialog2.findViewById(R.id.testcase2_fullsreen_result_textView);
							textView.setText(resultTextView.getText());
							dialog2.show();
							WindowManager.LayoutParams params = dialog2.getWindow().getAttributes();
							params.width = WindowManager.LayoutParams.MATCH_PARENT;
							params.height = WindowManager.LayoutParams.MATCH_PARENT;
							dialog2.getWindow().setAttributes(params);
						}
					}
				});
				builder.show();
			}
		}
	};

	protected abstract List<TestCaseData> getTestCaseDatas();

	private int selectPosition = 0;

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(HFAdapter adapter, ViewHolder vh, int position) {
			selectPosition = position;
			updateRight();
		}
	};

	private ArrayListMap<String, Object> map2 = new ArrayListMap<String, Object>();

	private void updateTastParams() {
		TestCaseData testCaseData = datas.get(selectPosition);
		Object task = testCaseData.task;
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
		testCaseData.task = task;
	}

	private class ParamLine {
		public Field field;
		protected View itemView;
		private TextView keyTextView;
		private EditText valueEditText;
		private String key;
		private Button valueGetButton;
		private IAsyncTask<String, String> paramGetTask;
		private IAsyncTask<Map<String, String>, String> paramGetTaskMap;

		public void cancel() {
			if (paramGetTaskHelper != null) {
				paramGetTaskHelper.cancle();
			}
			if (paramGetMapTaskHelper != null) {
				paramGetMapTaskHelper.cancle();
			}
		}

		public void setValue(String value) {
			valueEditText.removeTextChangedListener(textWatcher);
			valueEditText.setText(value);
			valueEditText.addTextChangedListener(textWatcher);
			updateParam();
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
				updateParam();
			}
		};

		private void updateParam() {
			try {
				String text = valueEditText.getText().toString();
				Object newValue;
				if (String.class.equals(field.getType())) {
					newValue = text;
				} else {
					newValue = gson.fromJson(text, field.getType());
				}
				map2.put(key, newValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private TaskHelper<String, String> paramGetTaskHelper;
		private TaskHelper<Map<String, String>, String> paramGetMapTaskHelper;

		private OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v == valueGetButton) {
					if (paramGetTask != null) {
						if (paramGetTask instanceof IParamValuesNotify) {
							IParamValuesNotify getParamTask = (IParamValuesNotify) paramGetTask;
							getParamTask.notifyCurrentParamValues(map2, v);
						}
						paramGetTaskHelper = new TaskHelper<String, String>();
						paramGetTaskHelper.setTask(paramGetTask);
						paramGetTaskHelper.setCallback(new Callback<String, String>() {

							@Override
							public void onProgressUpdate(int percent, long current, long total, Object exraData) {

							}

							@Override
							public void onPreExecute() {
								valueGetButton.setText("正在获取..");
							}

							@Override
							public void onPostExecute(Code code, Exception exception, String success, String fail) {
								switch (code) {
								case SUCESS:
									valueGetButton.setText("获取");
									valueEditText.setText(success);
									break;
								case FAIL:
									Toast.makeText(getContext(), "111", Toast.LENGTH_SHORT).show();
									break;
								case EXCEPTION:
									Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
									break;
								default:
									break;
								}
							}
						});
						paramGetTaskHelper.execute();
					} else if (paramGetTaskMap != null) {
						if (paramGetTask instanceof IParamValuesNotify) {
							IParamValuesNotify getParamTask = (IParamValuesNotify) paramGetTask;
							getParamTask.notifyCurrentParamValues(map2, v);
						}
						paramGetMapTaskHelper = new TaskHelper<Map<String, String>, String>();
						paramGetMapTaskHelper.setTask(paramGetTaskMap);
						paramGetMapTaskHelper.setCallback(new Callback<Map<String, String>, String>() {

							@Override
							public void onProgressUpdate(int percent, long current, long total, Object exraData) {

							}

							@Override
							public void onPreExecute() {
								valueGetButton.setText("正在获取..");
							}

							@Override
							public void onPostExecute(Code code, Exception exception, Map<String, String> success, String fail) {
								switch (code) {
								case SUCESS:
									valueGetButton.setText("获取");
									for (Entry<String, String> testCaseData : success.entrySet()) {
										ParamLine paramLine = lines.get(testCaseData.getKey());
										if (paramLine != null) {
											paramLine.setValue(testCaseData.getValue());
										}
									}
									break;
								case FAIL:
									Toast.makeText(getContext(), "111", Toast.LENGTH_SHORT).show();
									break;
								case EXCEPTION:
									Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
									break;
								default:
									break;
								}
							}
						});
						paramGetMapTaskHelper.execute();
					}
				}
			}
		};

	}

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
			updateRight();
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
			updateRight();

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
