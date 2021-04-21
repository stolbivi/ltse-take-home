package com.ltse.model;

public class MarketOrder extends AbstractOrder {

    public MarketOrder(OrderType type, String symbol, long time, int nano) {
        super(type, symbol, time, nano);
    }

    @Override
    public String toString() {
        return "LimitOrder{symbol=" + getSymbol() + ",type=" + this.getType() + '}';
    }

}
