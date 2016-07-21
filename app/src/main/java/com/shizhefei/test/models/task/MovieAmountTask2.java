package com.shizhefei.test.models.task;

import android.os.AsyncTask;
import android.os.Build;
import android.text.format.DateFormat;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.shizhefei.task.ICacheConfig;
import com.shizhefei.task.IAsyncTask;
import com.shizhefei.test.models.enties.MovieAmount;

import java.util.Random;

/**
 * Created by LuckyJayce on 2016/7/20.
 */
public class MovieAmountTask2 implements IAsyncTask<MovieAmount> {

    public String name;

    public MovieAmountTask2(String name) {
        this.name = name;
    }

    @Override
    public RequestHandle execute(final ResponseSender<MovieAmount> sender) throws Exception {
        final AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(2000);
                    Random random = new Random();
                    int commentCount = random.nextInt(100);
                    int playCount = random.nextInt(1000) + 10;
                    long updateTime = System.currentTimeMillis();
                    sender.sendData(new MovieAmount(name, commentCount, playCount, name + " " + DateFormat.format("dd kk:mm:ss", updateTime)));
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendError(e);
                }
                return null;
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            asyncTask.execute();
        }
        return new RequestHandle() {
            @Override
            public void cancle() {
                asyncTask.cancel(true);
            }

            @Override
            public boolean isRunning() {
                return false;
            }
        };
    }

    @Override
    public String toString() {
        return name;
    }

    public static class CacheConfig implements ICacheConfig<MovieAmount> {
        //缓存过期时间
        private int expirationTime;

        public CacheConfig(int expirationTime) {
            this.expirationTime = expirationTime;
        }

        @Override
        public String getTaskKey(Object taskOrDataSource) {
            MovieAmountTask2 task = (MovieAmountTask2) taskOrDataSource;
            return new StringBuilder("MovieAmountTask2:").append(task.name).toString();
        }

        @Override
        public boolean isUsefulCacheData(Object taskOrDataSource, long requestTime, long saveTime, MovieAmount o) {
            long current = System.currentTimeMillis();
            return current - saveTime < expirationTime;
        }

        @Override
        public boolean isNeedSave(Object taskOrDataSource, long requestTime, long saveTime, MovieAmount o) {
            return true;
        }
    }
}



