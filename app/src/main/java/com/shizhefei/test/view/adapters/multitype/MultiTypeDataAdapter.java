package com.shizhefei.test.view.adapters.multitype;

import com.shizhefei.mvc.IDataAdapter;
import com.shizhefei.recyclerview.HFRecyclerAdapter;
import com.shizhefei.view.multitype.ItemBinderFactory;
import com.shizhefei.view.multitype.MultiTypeAdapter;

import java.util.List;

/**
 * Created by LuckyJayce on 2017/2/11.
 */

public class MultiTypeDataAdapter<ITEM_DATA> extends HFRecyclerAdapter implements IDataAdapter<List<ITEM_DATA>> {

    public MultiTypeDataAdapter(ItemBinderFactory factory) {
        super(new MultiTypeAdapter<>(factory));
    }

    public MultiTypeDataAdapter(List<? extends ITEM_DATA> addList, ItemBinderFactory factory) {
        super(new MultiTypeAdapter<>(addList ,factory));
    }

    @Override
    public MultiTypeAdapter<ITEM_DATA> getAdapter() {
        return (MultiTypeAdapter<ITEM_DATA>) super.getAdapter();
    }

    @Override
    public void notifyDataChanged(List<ITEM_DATA> items, boolean isRefresh) {
        getAdapter().notifyDataChanged(items, isRefresh);
    }

    public void notifyDataChanged2(List<? extends ITEM_DATA> items, boolean isRefresh) {
        getAdapter().notifyDataChanged2(items, isRefresh);
    }

    @Override
    public List<ITEM_DATA> getData() {
        return getAdapter().getData();
    }

    @Override
    public boolean isEmpty() {
        return getAdapter().isEmpty();
    }
}
