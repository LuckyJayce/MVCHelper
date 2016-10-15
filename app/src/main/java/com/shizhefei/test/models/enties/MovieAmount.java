package com.shizhefei.test.models.enties;

/**
 * Created by LuckyJayce on 2016/7/20.
 */
public class MovieAmount {
    public String name;
    public int commentCount;
    public int playCount;
    public String updateTime;

    public MovieAmount(String name, int commentCount, int playCount, String updateTime) {
        this.name = name;
        this.commentCount = commentCount;
        this.playCount = playCount;
        this.updateTime = updateTime;
    }
}
