package com.ltse.queues;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class SymbolQueues {

    private String symbol;
    private Boolean halted;
    private PriorityBlockingQueue buyQueue;
    private PriorityBlockingQueue sellQueue;

    public SymbolQueues(String symbol, Boolean halted, PriorityBlockingQueue buyQueue, PriorityBlockingQueue sellQueue) {
        this.symbol = symbol;
        this.halted = halted;
        this.buyQueue = buyQueue;
        this.sellQueue = sellQueue;
    }

    public String getSymbol() {
        return symbol;
    }

    public Boolean getHalted() {
        return halted;
    }

    public PriorityBlockingQueue getBuyQueue() {
        return buyQueue;
    }

    public PriorityBlockingQueue getSellQueue() {
        return sellQueue;
    }
}
