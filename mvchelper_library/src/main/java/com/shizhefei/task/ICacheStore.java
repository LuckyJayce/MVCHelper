package com.shizhefei.task;

public interface ICacheStore {

    Cache getCache(String taskKey);

    void saveCache(String taskKey, long requestTime, long saveTime, Object result);

    class Cache {

        public long requestTime;
        public long saveTime;
        public Object data;

        public Cache() {
        }

        public Cache(long requestTime, long saveTime, Object data) {
            this.requestTime = requestTime;
            this.saveTime = saveTime;
            this.data = data;
        }
    }

}