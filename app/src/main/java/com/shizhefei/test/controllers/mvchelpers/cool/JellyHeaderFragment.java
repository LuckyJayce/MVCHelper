package com.shizhefei.test.controllers.mvchelpers.cool;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.mvc.MVCCoolHelper;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.test.models.datasource.okhttp.BooksOkHttp_AsyncDataSource;
import com.shizhefei.test.models.enties.Book;
import com.shizhefei.test.view.adapters.ReBooksAdapter;
import com.shizhefei.view.coolrefreshview.CoolRefreshView;
import com.shizhefei.view.coolrefreshview.header.JellyHeader;
import com.shizhefei.view.mvc.demo.R;

import java.util.List;


/**
 * Created by LuckyJayce on 2016/11/29.
 */

public class JellyHeaderFragment extends LazyFragment implements RefreshEvent {
    private CoolRefreshView coolRefreshView;
    private RecyclerView recyclerView;
    private MVCHelper<List<Book>> mvcHelper;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_recyclerview);
        coolRefreshView = (CoolRefreshView) findViewById(R.id.recyclerview_funnyRefreshView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //这个header要白色才能突出效果
        coolRefreshView.setBackgroundColor(Color.WHITE);
        JellyHeader jellyHeader = new JellyHeader(getContext());
        jellyHeader.setDragLayoutColor(ContextCompat.getColor(getContext(), R.color.primary));
        jellyHeader.setLoadingView(R.layout.layout_jelley_header);
        coolRefreshView.setPullHeader(jellyHeader);

        mvcHelper = new MVCCoolHelper<>(coolRefreshView);
        mvcHelper.setDataSource(new BooksOkHttp_AsyncDataSource());
        mvcHelper.setAdapter(new ReBooksAdapter(getContext()));
        mvcHelper.refresh();
    }

    @Override
    public void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        mvcHelper.destory();
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        coolRefreshView.setRefreshing(refreshing);
    }

}
