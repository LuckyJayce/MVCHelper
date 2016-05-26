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
package com.shizhefei.test.view.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizhefei.mvc.IDataAdapter;
import com.shizhefei.mvc.data.Data3;
import com.shizhefei.test.models.enties.Discuss;
import com.shizhefei.test.models.enties.Movie;
import com.shizhefei.view.mvc.demo.R;

public class MovieDetailAdapter extends RecyclerView.Adapter<ViewHolder> implements IDataAdapter<Data3<Movie, List<Discuss>, List<Movie>>> {

	private Data3<Movie, List<Discuss>, List<Movie>> mData = new Data3<Movie, List<Discuss>, List<Movie>>(null, new ArrayList<Discuss>(),
			new ArrayList<Movie>());
	private LayoutInflater inflater;

	public MovieDetailAdapter(Context context) {
		super();
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder viewHolder;
		switch (viewType) {
		case ITEM_MOVIE:
			viewHolder = new MovieViewHolder(inflater.inflate(R.layout.item_movie, parent, false));
			break;
		case ITEM_DISCUSS:
			viewHolder = new DiscussViewHolder(inflater.inflate(R.layout.item_discuss, parent, false));
			break;
		case ITEM_OTHER_MOVIE:
		default:
			viewHolder = new OtherViewHolder(inflater.inflate(R.layout.item_othermovie, parent, false));
			break;
		}
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {

		switch (getItemViewType(position)) {
		case ITEM_MOVIE:
			onBindMovieViewHolder(holder, position);
			break;
		case ITEM_DISCUSS:
			onBindDiscussViewHolder(holder, position);
			break;
		case ITEM_OTHER_MOVIE:
			onBindOtherMovieViewHolder(holder, position);
		default:
			break;
		}
	}

	private void onBindMovieViewHolder(ViewHolder holder, int position) {
		MovieViewHolder viewHolder = (MovieViewHolder) holder;
		Movie movie = mData.getValue1();
		viewHolder.description.setText(movie.getDescription());
		viewHolder.name.setText(movie.getName());
		viewHolder.time.setText(movie.getTime());
	}

	private void onBindDiscussViewHolder(ViewHolder holder, int position) {
		DiscussViewHolder viewHolder = (DiscussViewHolder) holder;
		Discuss discuss = mData.getValue2().get(position - 1);
		viewHolder.content.setText(discuss.getContent());
		viewHolder.user.setText(discuss.getName());
		viewHolder.time.setText(DateFormat.format("MM-dd HH:mm", discuss.getTime()));
	}

	private void onBindOtherMovieViewHolder(ViewHolder holder, int position) {
		OtherViewHolder viewHolder = (OtherViewHolder) holder;
		Movie movie = mData.getValue3().get(position - 1 - mData.getValue2().size());
		viewHolder.description.setText(movie.getDescription());
		viewHolder.name.setText(movie.getName());
		viewHolder.time.setText(movie.getTime());
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return ITEM_MOVIE;
		} else if (position < mData.getValue2().size() + 1) {
			return ITEM_DISCUSS;
		}
		return ITEM_OTHER_MOVIE;
	}

	private static final int ITEM_MOVIE = 0;
	private static final int ITEM_DISCUSS = 1;
	private static final int ITEM_OTHER_MOVIE = 2;

	@Override
	public int getItemCount() {
		return (mData.getValue1()==null?0:1) + mData.getValue2().size() + mData.getValue3().size();
	}

	@Override
	public void notifyDataChanged(Data3<Movie, List<Discuss>, List<Movie>> data, boolean isRefresh) {
		if (isRefresh) {
			mData.setValue1(data.getValue1());
			mData.getValue2().clear();
			mData.getValue3().clear();
		}
		if (data.getValue2() != null) {
			mData.getValue2().addAll(data.getValue2());
		}
		if (data.getValue3() != null) {
			mData.getValue3().addAll(data.getValue3());
		}
		notifyDataSetChanged();
	}

	@Override
	public Data3<Movie, List<Discuss>, List<Movie>> getData() {
		return mData;
	}

	@Override
	public boolean isEmpty() {
		return mData.getValue1() == null;
	}

	private static class MovieViewHolder extends ViewHolder {

		private TextView description;
		private TextView name;
		private TextView time;

		public MovieViewHolder(View itemView) {
			super(itemView);
			description = (TextView) itemView.findViewById(R.id.item_movie_description_textView);
			name = (TextView) itemView.findViewById(R.id.item_movie_name_textView);
			time = (TextView) itemView.findViewById(R.id.item_movie_time_textView);
		}
	}

	private static class DiscussViewHolder extends ViewHolder {

		private TextView content;
		private TextView user;
		private TextView time;

		public DiscussViewHolder(View itemView) {
			super(itemView);
			content = (TextView) itemView.findViewById(R.id.item_dicuss_content_textView);
			user = (TextView) itemView.findViewById(R.id.item_dicuss_user_textView);
			time = (TextView) itemView.findViewById(R.id.item_dicuss_time_textView);
		}
	}

	private static class OtherViewHolder extends ViewHolder {
		private TextView description;
		private TextView name;
		private TextView time;

		public OtherViewHolder(View itemView) {
			super(itemView);
			description = (TextView) itemView.findViewById(R.id.item_otherMovie_description_textView);
			name = (TextView) itemView.findViewById(R.id.item_otherMovie_name_textView);
			time = (TextView) itemView.findViewById(R.id.item_otherMovie_time_textView);
		}
	}

}
