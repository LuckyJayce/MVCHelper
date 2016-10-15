/*
 * Copyright (C) 2015 Drakeet <drakeet.me@gmail.com>
 *
 * This file is part of Meizhi
 *
 * Meizhi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Meizhi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Meizhi.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.shizhefei.test.models.datasource.rxjava_retrofit;

import com.shizhefei.test.models.enties.BaseData;
import com.shizhefei.test.models.enties.Gank;
import com.shizhefei.test.models.enties.Meizhi;
import com.shizhefei.utils.ArrayListMap;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

// @formatter:off

/**
 * Created by drakeet on 8/9/15.
 */
public interface GankApi {

    @GET("data/福利/" + 10 + "/{page}")
    Observable<BaseData<List<Meizhi>>> getMeizhiData(
            @Path("page") int page);

    @GET("day/{year}/{month}/{day}")
    Observable<BaseData<ArrayListMap<String, List<Gank>>>> getGankData(
            @Path("year") String year,
            @Path("month") String month,
            @Path("day") String day);
}
