package com.ltse.queues;

import com.ltse.model.LimitOrder;
import com.ltse.model.MarketOrder;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class SymbolQueues {

    private String symbol;
    private Boolean halted;
    private PriorityBlockingQueue marketQueue;
    private PriorityBlockingQueue buyQueue;
    private PriorityBlockingQueue sellQueue;

    public SymbolQueues(String symbol, Boolean halted, PriorityBlockingQueue marketQueue, PriorityBlockingQueue buyQueue, PriorityBlockingQueue sellQueue) {
        this.symbol = symbol;
        this.halted = halted;
        this.marketQueue = marketQueue;
        this.buyQueue = buyQueue;
        this.sellQueue = sellQueue;
    }

    public String getSymbol() {
        return symbol;
    }

    public Boolean getHalted() {
        return halted;
    }

    public PriorityBlockingQueue<MarketOrder> getMarketQueue() {
        return marketQueue;
    }

    public PriorityBlockingQueue<LimitOrder> getBuyQueue() {
        return buyQueue;
    }

    public PriorityBlockingQueue<LimitOrder> getSellQueue() {
        return sellQueue;
    }

}
