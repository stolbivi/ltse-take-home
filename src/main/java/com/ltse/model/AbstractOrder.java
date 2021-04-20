package com.ltse.model;


public abstract class AbstractOrder extends Timestamped{

    private OrderType type;
    private String symbol;
    private String cancellationReason;

    /**
     * Since the order is extremely important we split epoch time into 2 separate integer parts to avoid
     * accuracy issues which are specific for floating point data types
     * @param type
     * @param symbol
     * @param time
     * @param nanoseconds
     */
    public AbstractOrder(OrderType type, String symbol, long time, int nanoseconds) {
        super(time, nanoseconds);
        this.type = type;
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderType getType() {
        return type;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

}
