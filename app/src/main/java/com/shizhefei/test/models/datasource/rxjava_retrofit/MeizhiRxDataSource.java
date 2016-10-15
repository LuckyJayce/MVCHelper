package com.shizhefei.test.models.datasource.rxjava_retrofit;


import com.shizhefei.test.models.enties.BaseData;
import com.shizhefei.test.models.enties.Meizhi;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by LuckyJayce on 2016/7/21.
 */
public class MeizhiRxDataSource extends MRxDataSource<List<Meizhi>> {
    private int mPage = 1;

    @Override
    public Observable<BaseData<List<Meizhi>>> refreshRXM(DoneActionRegister<List<Meizhi>> register) throws Exception {
        return load(1, register);
    }

    @Override
    public Observable<BaseData<List<Meizhi>>> loadMoreRXM(DoneActionRegister<List<Meizhi>> register) throws Exception {
        return load(mPage + 1, register);
    }

    private Observable<BaseData<List<Meizhi>>> load(final int page, DoneActionRegister<List<Meizhi>> register) throws Exception {
        register.addAction(new Action1<List<Meizhi>>() {
            @Override
            public void call(List<Meizhi> meizhis) {
                mPage = page;
            }
        });
        return getGankApi().getMeizhiData(page);
    }


    @Override
    public boolean hasMore() {
        return true;
    }


}
