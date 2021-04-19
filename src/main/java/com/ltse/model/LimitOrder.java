package com.ltse.model;

public class LimitOrder extends AbstractOrder {

    private double price;

    public LimitOrder(OrderType type, String symbol, int time, int nanoseconds, double price) {
        super(type, symbol, time, nanoseconds);
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

}
