package com.ltse.model;

public class Trade extends Timestamped {

    private String symbol;
    private double price;

    public Trade(String symbol, double price, long time, int nanoseconds) {
        super(time, nanoseconds);
        this.symbol = symbol;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Trade{symbol=" + getSymbol() + ",price=" + price + '}';
    }

}
