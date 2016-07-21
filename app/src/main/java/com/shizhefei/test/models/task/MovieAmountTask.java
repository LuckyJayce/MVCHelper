package com.shizhefei.test.models.task;

import android.text.format.DateFormat;

import com.shizhefei.mvc.ProgressSender;
import com.shizhefei.task.ICacheConfig;
import com.shizhefei.task.ITask;
import com.shizhefei.test.models.enties.MovieAmount;

import java.util.Random;

/**
 * Created by LuckyJayce on 2016/7/20.
 */
public class MovieAmountTask implements ITask<MovieAmount> {
    public String name;

    public MovieAmountTask(String name) {
        this.name = name;
    }

    @Override
    public MovieAmount execute(ProgressSender progressSender) throws Exception {
        Thread.sleep(2000);
        Random random = new Random();
        int commentCount = random.nextInt(100);
        int playCount = random.nextInt(1000) + 10;
        long updateTime = System.currentTimeMillis();
        return new MovieAmount(name, commentCount, playCount, name + " " + DateFormat.format("dd kk:mm:ss", updateTime));
    }

    @Override
    public void cancel() {

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
            MovieAmountTask task = (MovieAmountTask) taskOrDataSource;
            return new StringBuilder("MovieAmountTask:").append(task.name).toString();
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
