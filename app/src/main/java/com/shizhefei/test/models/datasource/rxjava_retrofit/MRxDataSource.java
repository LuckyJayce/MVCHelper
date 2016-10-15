package com.shizhefei.test.models.datasource.rxjava_retrofit;


import com.shizhefei.test.models.enties.BaseData;
import com.shizhefei.test.models.exception.BizException;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by LuckyJayce on 2016/7/22.
 */
public abstract class MRxDataSource<DATA> extends RxDataSource<DATA> {
    private static final int DEFAULT_TIMEOUT = 5;
    private static Retrofit retrofit;
    private static GankApi gankApi;

    protected GankApi getGankApi() {
        if (gankApi == null) {
            synchronized (this) {
                if (gankApi == null) {
                    //手动创建一个OkHttpClient并设置超时时间
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

                    retrofit = new Retrofit.Builder()
                            .client(builder.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .baseUrl("http://gank.io/api/")
                            .build();
                    gankApi = retrofit.create(GankApi.class);
                }
            }
        }
        return gankApi;
    }

    @Override
    public Observable<DATA> refreshRX(DoneActionRegister<DATA> register) throws Exception {
        return load(refreshRXM(register));
    }

    @Override
    public Observable<DATA> loadMoreRX(DoneActionRegister<DATA> register) throws Exception {
        return load(loadMoreRXM(register));
    }

    private Observable<DATA> load(Observable<BaseData<DATA>> observableAction) {
        return observableAction.flatMap(new Func1<BaseData<DATA>, Observable<DATA>>() {
            @Override
            public Observable<DATA> call(BaseData<DATA> baseData) {
                if (baseData.error) {
                    return Observable.error(new BizException("业务出错"));
                }
                return Observable.just(baseData.results);
            }
        }).map(new Func1<DATA, DATA>() {
            @Override
            public DATA call(DATA data) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return data;
            }
        });
    }

    public abstract Observable<BaseData<DATA>> refreshRXM(DoneActionRegister<DATA> register) throws Exception;

    public abstract Observable<BaseData<DATA>> loadMoreRXM(DoneActionRegister<DATA> register) throws Exception;
}
