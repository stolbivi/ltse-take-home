package com.ltse.model;

public class MarketOrder extends AbstractOrder{

    public MarketOrder(OrderType type, String symbol, int time, int nanoseconds) {
        super(type, symbol, time, nanoseconds);
    }

}
