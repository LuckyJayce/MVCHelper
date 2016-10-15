package com.shizhefei.task;

/**
 * Task的缓存配置
 *
 * @param <DATA>
 */
public interface ICacheConfig<DATA> {
    /**
     *
     * task的id,用于唯一标识task，相同的taskKey的task被执行会合并成同一个task执行,先执行的那个task运行，后面的task添加ICallback到task上进行回调
     *
     * @param taskOrDataSource
     * @return
     */
    String getTaskKey(Object taskOrDataSource);

    /**
     * 是否有用的缓存的数据
     * 如果返回true，则task就不会被执行，直接返回缓存的data数据给ICallback,并调用onPostExecute Code.Success
     * 如果返回false,执行task
     *
     * @param taskOrDataSource ITask，IDataSource，IAsyncTask，IDataSource，IAsyncDataSource 这四种类型
     * @param requestTime 请求的时间戳
     * @param saveTime 保存data请求的时间戳
     * @param data 缓存数据
     * @return
     */
    boolean isUsefulCacheData(Object taskOrDataSource, long requestTime, long saveTime, DATA data);

    /**
     * 是否将这个data保存下来
     * 返回true的话，ICacheStore.saveCache 就会被调用
     * 返回false的话，不保存
     *
     * @param taskOrDataSource ITask，IDataSource，IAsyncTask，IDataSource，IAsyncDataSource 这四种类型
     * @param requestTime      请求的时间戳
     * @param saveTime         保存data请求的时间戳
     * @param data             缓存数据
     * @return
     */
    boolean isNeedSave(Object taskOrDataSource, long requestTime, long saveTime, DATA data);

}
