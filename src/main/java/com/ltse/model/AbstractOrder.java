package com.ltse.model;


public abstract class AbstractOrder {

    private OrderType type;
    private String symbol;
    private int time;
    private int nanoseconds;

    /**
     * Since the order is extremely important we split epoch time into 2 separate integer parts to avoid
     * accuracy issues which are specific for floating point data types
     * @param type
     * @param symbol
     * @param time
     * @param nanoseconds
     */
    public AbstractOrder(OrderType type, String symbol, int time, int nanoseconds) {
        this.type = type;
        this.symbol = symbol;
        this.time = time;
        this.nanoseconds = nanoseconds;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getTime() {
        return time;
    }

    public int getNanoseconds() {
        return nanoseconds;
    }

    public OrderType getType() {
        return type;
    }

}
