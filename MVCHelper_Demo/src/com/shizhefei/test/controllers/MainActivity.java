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
package com.shizhefei.test.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shizhefei.test.controllers.other.BooDetailActivity;
import com.shizhefei.test.controllers.other.MovieDetailActivity;
import com.shizhefei.test.controllers.other.UltraRecyclerViewActivity;
import com.shizhefei.test.controllers.testhelpers.NormalActivity;
import com.shizhefei.test.controllers.testhelpers.PullrefshActivity;
import com.shizhefei.test.controllers.testhelpers.SwipeRefreshActivity;
import com.shizhefei.test.controllers.testhelpers.UltraActivity;
import com.shizhefei.view.mvc.demo.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void onClickDemo(View view) {
		startActivity(new Intent(getApplicationContext(), PullrefshActivity.class));
	}

	public void onClickDemo2(View view) {
		startActivity(new Intent(getApplicationContext(), UltraActivity.class));
	}

	public void onClickDemo3(View view) {
		startActivity(new Intent(getApplicationContext(), SwipeRefreshActivity.class));
	}

	public void onClickDemo4(View view) {
		startActivity(new Intent(getApplicationContext(), BooDetailActivity.class));
	}

	public void onClickDemo5(View view) {
		startActivity(new Intent(getApplicationContext(), NormalActivity.class));
	}

	public void onClickDemo6(View view) {
		startActivity(new Intent(getApplicationContext(), UltraRecyclerViewActivity.class));
	}

	public void onClickDemo7(View view) {
		startActivity(new Intent(getApplicationContext(), MovieDetailActivity.class));
	}

}
