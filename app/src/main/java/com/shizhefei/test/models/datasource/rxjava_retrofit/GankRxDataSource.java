package com.shizhefei.test.models.datasource.rxjava_retrofit;


import com.shizhefei.test.models.enties.BaseData;
import com.shizhefei.test.models.enties.Gank;
import com.shizhefei.utils.ArrayListMap;

import java.util.List;

import rx.Observable;

/**
 * Created by LuckyJayce on 2016/7/11.
 */
public class GankRxDataSource extends MRxDataSource<ArrayListMap<String, List<Gank>>> {

    private String year;
    private String month;
    private String day;

    public GankRxDataSource(int year, int month, int day) {
        this.year = String.valueOf(year);
        this.month = String.valueOf(month);
        this.day = String.valueOf(day);
    }

    public GankRxDataSource(String year, String month, String day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public Observable<BaseData<ArrayListMap<String, List<Gank>>>> refreshRXM(DoneActionRegister<ArrayListMap<String, List<Gank>>> register) throws Exception {
        return getGankApi().getGankData(year, month, day);
    }

    @Override
    public Observable<BaseData<ArrayListMap<String, List<Gank>>>> loadMoreRXM(DoneActionRegister<ArrayListMap<String, List<Gank>>> register) throws Exception {
        return null;
    }

    @Override
    public boolean hasMore() {
        return false;
    }
}
