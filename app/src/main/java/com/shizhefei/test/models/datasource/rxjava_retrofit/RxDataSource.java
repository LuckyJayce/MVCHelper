package com.shizhefei.test.models.datasource.rxjava_retrofit;

import android.util.Log;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by LuckyJayce on 2016/7/21.
 */
public abstract class RxDataSource<DATA> implements IAsyncDataSource<DATA> {

    @Override
    public final RequestHandle refresh(final ResponseSender<DATA> sender) throws Exception {
        DoneActionRegister<DATA> register = new DoneActionRegister<>();
        return load(sender, refreshRX(register), register);
    }

    @Override
    public final RequestHandle loadMore(ResponseSender<DATA> sender) throws Exception {
        DoneActionRegister<DATA> register = new DoneActionRegister<>();
        return load(sender, loadMoreRX(register), register);
    }

    private RequestHandle load(final ResponseSender<DATA> sender, final Observable<DATA> observable, final DoneActionRegister<DATA> register) {
        final Subscriber<DATA> subscriber = new Subscriber<DATA>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(DATA data) {
                for (Action1<DATA> subscriber : register.subscribers) {
                    subscriber.call(data);
                }
                Log.d("pppp", "sendData:");
                sender.sendData(data);
            }
        };
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return new RequestHandle() {
            @Override
            public void cancle() {
                Log.d("pppp", "cancle:");
                if (!subscriber.isUnsubscribed()) {
                    subscriber.unsubscribe();
                }
            }

            @Override
            public boolean isRunning() {
                return false;
            }
        };
    }

    public abstract Observable<DATA> refreshRX(DoneActionRegister<DATA> register) throws Exception;

    public abstract Observable<DATA> loadMoreRX(DoneActionRegister<DATA> register) throws Exception;

    public static class DoneActionRegister<DATA> {
        private List<Action1<DATA>> subscribers = new ArrayList<>();


        public void addAction(final Action1<DATA> doneAction) {
            subscribers.add(doneAction);
        }
    }

}
