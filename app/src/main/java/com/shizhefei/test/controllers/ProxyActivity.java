package com.shizhefei.test.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.shizhefei.view.mvc.demo.R;

public class ProxyActivity extends FragmentActivity {

	/**
	 * Fragment 的类名，用于反射创建fragment
	 */
	public static final String INTENT_STRING_FRAGMENT_NAME = "intent_string_fragment_name";
	public static final String INTENT_STRING_FRAGMENT_TITLE = "intent_string_fragment_title";
	private Fragment fragment;
	private View backView;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_proxy);
		View headLayout = findViewById(R.id.proxy_head_layout);
		TextView titleTextView = (TextView) findViewById(R.id.proxy_title_textView);
		backView = findViewById(R.id.proxy_back_view);
		String title = getIntent().getStringExtra(INTENT_STRING_FRAGMENT_TITLE);
		if (TextUtils.isEmpty(title)) {
			headLayout.setVisibility(View.GONE);
		} else {
			headLayout.setVisibility(View.VISIBLE);
			titleTextView.setText(title);
		}
		try {
			@SuppressWarnings("unchecked")
			Class<Fragment> fragmentClass = (Class<Fragment>) Class
					.forName(getIntent().getStringExtra(
							INTENT_STRING_FRAGMENT_NAME));
			fragment = fragmentClass.newInstance();
			Bundle bundle = new Bundle(getIntent().getExtras());
			fragment.setArguments(bundle);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.proxy_fragment, fragment)
					.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
		backView.setOnClickListener(onClickListener);

	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			return true;
		}
		return false;
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == backView) {
				finish();
			}
		}
	};

	public static void startActivity(Context context,
			Class<? extends Fragment> fragmentClass, Bundle bundle) {
		startActivity(context, fragmentClass, null, bundle);
	}

	public static void startActivity(Context context,
			Class<? extends Fragment> fragmentClass, String title) {
		startActivity(context, fragmentClass, title, null);
	}

	public static void startActivity(Context context,
			Class<? extends Fragment> fragmentClass) {
		startActivity(context, fragmentClass, null, null);
	}

	public static void startActivity(Context context,
			Class<? extends Fragment> fragmentClass, String title, Bundle bundle) {
		Intent intent = new Intent(context, ProxyActivity.class);
		intent.putExtra(ProxyActivity.INTENT_STRING_FRAGMENT_NAME,
				fragmentClass.getName());
		if (title != null) {
			intent.putExtra(ProxyActivity.INTENT_STRING_FRAGMENT_TITLE, title);
		}
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

}