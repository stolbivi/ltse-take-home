package com.ltse.model;

public class LimitOrder extends AbstractOrder {

    private double price;

    public LimitOrder(OrderType type, String symbol, long time, int nano, double price) {
        super(type, symbol, time, nano);
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

}
