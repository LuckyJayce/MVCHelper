package com.shizhefei.recyclerview;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class HFRecyclerAdapter extends RecyclerView.Adapter {

	public static final int TYPE_MANAGER_OTHER = 0;
	public static final int TYPE_MANAGER_LINEAR = 1;
	public static final int TYPE_MANAGER_GRID = 2;
	public static final int TYPE_MANAGER_STAGGERED_GRID = 3;

	public static final int TYPE_HEADER = 7898;
	public static final int TYPE_FOOTER = 7899;

	private List<View> mHeaders = new ArrayList<View>();
	private List<View> mFooters = new ArrayList<View>();

	private int mManagerType;
	private RecyclerView.Adapter mIntermediary;

	public HFRecyclerAdapter(RecyclerView.Adapter intermediary) {
		this.mIntermediary = intermediary;
		mIntermediary.registerAdapterDataObserver(adapterDataObserver);
	}

	private AdapterDataObserver adapterDataObserver = new AdapterDataObserver() {
		public void onChanged() {
			HFRecyclerAdapter.this.notifyDataSetChanged();
		}

		public void onItemRangeChanged(int positionStart, int itemCount) {
			HFRecyclerAdapter.this.notifyItemRangeChanged(positionStart, itemCount);
		}

		public void onItemRangeInserted(int positionStart, int itemCount) {
			HFRecyclerAdapter.this.notifyItemRangeInserted(positionStart, itemCount);
		}

		public void onItemRangeRemoved(int positionStart, int itemCount) {
			HFRecyclerAdapter.this.notifyItemRangeRemoved(positionStart, itemCount);
		}

		public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
			HFRecyclerAdapter.this.notifyItemMoved(fromPosition, toPosition);
		}
	};

	public int getManagerType() {
		return mManagerType;
	}

	public int getHeadSize() {
		return mHeaders.size();
	}

	public int getFootSize() {
		return mFooters.size();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
		// if our position is one of our items (this comes from
		// getItemViewType(int position) below)
		if (type != TYPE_HEADER && type != TYPE_FOOTER) {
			return mIntermediary.onCreateViewHolder(viewGroup, type);
			// else we have a header/footer
		} else {
			// create a new framelayout, or inflate from a resource
			FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
			// make sure it fills the space
			frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			return new HeaderFooterViewHolder(frameLayout);
		}
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder vh, int position) {
		// check what type of view our position is
		if (isHeader(position)) {
			View v = mHeaders.get(position);
			// add our view to a header view and display it
			prepareHeaderFooter((HeaderFooterViewHolder) vh, v);
		} else if (isFooter(position)) {
			View v = mFooters.get(position - mIntermediary.getItemCount() - mHeaders.size());
			// add our view to a footer view and display it
			prepareHeaderFooter((HeaderFooterViewHolder) vh, v);
		} else {
			// it's one of our items, display as required
			mIntermediary.onBindViewHolder(vh, position - mHeaders.size());
		}
	}

	private void prepareHeaderFooter(HeaderFooterViewHolder vh, View view) {

		// if it's a staggered grid, span the whole layout
		if (mManagerType == TYPE_MANAGER_STAGGERED_GRID) {
			StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			layoutParams.setFullSpan(true);
			vh.itemView.setLayoutParams(layoutParams);
		}

		// if the view already belongs to another layout, remove it
		if (view.getParent() != null) {
			((ViewGroup) view.getParent()).removeView(view);
		}

		// empty out our FrameLayout and replace with our header/footer
		vh.base.removeAllViews();
		vh.base.addView(view);

	}

	private boolean isHeader(int position) {
		return (position < mHeaders.size());
	}

	private boolean isFooter(int position) {
		return (position >= mHeaders.size() + mIntermediary.getItemCount());
	}

	@Override
	public int getItemCount() {
		return mHeaders.size() + mIntermediary.getItemCount() + mFooters.size();
	}

	@Override
	public int getItemViewType(int position) {
		// check what type our position is, based on the assumption that the
		// order is headers > items > footers
		if (isHeader(position)) {
			return TYPE_HEADER;
		} else if (isFooter(position)) {
			return TYPE_FOOTER;
		}
		int type = mIntermediary.getItemViewType(position - getHeadSize());
		if (type == TYPE_HEADER || type == TYPE_FOOTER) {
			throw new IllegalArgumentException("Item type cannot equal " + TYPE_HEADER + " or " + TYPE_FOOTER);
		}
		return type;
	}

	// add a header to the adapter
	public void addHeader(View header) {
		if (!mHeaders.contains(header)) {
			mHeaders.add(header);
			// animate
			notifyItemInserted(mHeaders.size() - 1);
		}
	}

	// remove a header from the adapter
	public void removeHeader(View header) {
		if (mHeaders.contains(header)) {
			// animate
			notifyItemRemoved(mHeaders.indexOf(header));
			mHeaders.remove(header);
		}
	}

	// add a footer to the adapter
	public void addFooter(View footer) {
		if (!mFooters.contains(footer)) {
			mFooters.add(footer);
			// animate
			notifyItemInserted(mHeaders.size() + mIntermediary.getItemCount() + mFooters.size() - 1);
		}
	}

	// remove a footer from the adapter
	public void removeFooter(View footer) {
		if (mFooters.contains(footer)) {
			// animate
			notifyItemRemoved(mHeaders.size() + mIntermediary.getItemCount() + mFooters.indexOf(footer));
			mFooters.remove(footer);
		}
	}

	// our header/footer RecyclerView.ViewHolder is just a FrameLayout
	public static class HeaderFooterViewHolder extends RecyclerView.ViewHolder {
		FrameLayout base;

		public HeaderFooterViewHolder(View itemView) {
			super(itemView);
			base = (FrameLayout) itemView;
		}
	}

}
