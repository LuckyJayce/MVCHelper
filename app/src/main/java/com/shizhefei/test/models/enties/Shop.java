package com.shizhefei.test.models.enties;

/**
 * Created by LuckyJayce on 2016/7/20.
 */
public class Shop {
    public String name;
    public int amount;
    public long startTime;
    public long duration;
    public int price;
    public int id;

    public Shop() {
    }

    public Shop(int id, String name, int amount, long startTime, long duration, int price) {
        this.name = name;
        this.amount = amount;
        this.startTime = startTime;
        this.duration = duration;
        this.price = price;
    }
}
