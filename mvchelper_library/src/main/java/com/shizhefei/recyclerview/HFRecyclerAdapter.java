package com.shizhefei.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

public class HFRecyclerAdapter extends HFAdapter {

    private Adapter adapter;

    public HFRecyclerAdapter(Adapter adapter) {
        this(adapter, true);
    }

    public HFRecyclerAdapter(Adapter adapter, boolean needSetClickListener) {
        super(needSetClickListener);
        this.adapter = adapter;
        adapter.registerAdapterDataObserver(adapterDataObserver);
    }

    private AdapterDataObserver adapterDataObserver = new AdapterDataObserver() {

        public void onChanged() {
            HFRecyclerAdapter.this.notifyDataSetChanged();
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            HFRecyclerAdapter.this.notifyItemRangeChanged(positionStart + getHeadSize(), itemCount);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            HFRecyclerAdapter.this.notifyItemRangeInserted(positionStart + getHeadSize(), itemCount);
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            HFRecyclerAdapter.this.notifyItemRangeRemoved(positionStart + getHeadSize(), itemCount);
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            HFRecyclerAdapter.this.notifyItemMoved(fromPosition + getHeadSize(), toPosition + getHeadSize());
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            HFRecyclerAdapter.this.notifyItemRangeChanged(positionStart + getHeadSize(), itemCount, payload);
        }
    };

    @Override
    public ViewHolder onCreateViewHolderHF(ViewGroup viewGroup, int type) {
        return adapter.onCreateViewHolder(viewGroup, type);
    }

    @Override
    public void onBindViewHolderHF(ViewHolder vh, int position) {
        adapter.onBindViewHolder(vh, position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        adapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        adapter.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        adapter.onViewAttachedToWindow(holder);
    }

    @Override
    public boolean onFailedToRecycleView(ViewHolder holder) {
        return adapter.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        adapter.onViewRecycled(holder);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        adapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCountHF() {
        return adapter.getItemCount();
    }

    @Override
    public int getItemViewTypeHF(int position) {
        return adapter.getItemViewType(position);
    }

    @Override
    public long getItemIdHF(int position) {
        return adapter.getItemId(position);
    }

}
