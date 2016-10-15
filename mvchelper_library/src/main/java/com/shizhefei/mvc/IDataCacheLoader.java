/*
Copyright 2015 shizhefei（LuckyJayce）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.shizhefei.mvc;

/**
 * DataSource 可以实现这个接口，进行加载缓存数据
 * 
 * @author zsy
 *
 * @param <DATA>
 */
public interface IDataCacheLoader<DATA> {

	/**
	 * 加载缓存<br>
	 * 注意这个方法执行于UI线程，不要做太过耗时的操作<br>
	 * 每次刷新的时候触发该方法，该方法在DataSource refresh之前执行<br>
	 * 
	 * @param isEmpty
	 *            adapter是否有数据，这个值是adapter.isEmpty()决定
	 * @return 加载的数据，返回后会执行adapter.notifyDataChanged(data, true)<br>
	 *         相当于refresh执行后adapter.notifyDataChanged(data, true)
	 */
	public DATA loadCache(boolean isEmpty);

}
