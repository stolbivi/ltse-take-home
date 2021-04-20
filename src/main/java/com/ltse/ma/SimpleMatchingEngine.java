package com.ltse.ma;

import com.ltse.model.*;
import com.ltse.queues.SymbolQueues;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class SimpleMatchingEngine {

    private SymbolQueues queues;
    private PriorityBlockingQueue<AbstractOrder> cancellationQueue;
    private PriorityBlockingQueue<Trade> tradeQueue;
    private ExecutorService matchingExecutor = Executors.newSingleThreadExecutor();

    public SimpleMatchingEngine(SymbolQueues queues, PriorityBlockingQueue<AbstractOrder> cancellationQueue, PriorityBlockingQueue<Trade> tradeQueue) {
        this.queues = queues;
        this.cancellationQueue = cancellationQueue;
        this.tradeQueue = tradeQueue;
    }

    public void start() {
        while (!matchingExecutor.isShutdown()) {
            MarketOrder marketOrder = queues.getMarketQueue().poll();
            LimitOrder buyOrder = queues.getBuyQueue().peek();
            LimitOrder sellOrder = queues.getSellQueue().peek();
            if (marketOrder != null) {
                matchMarketOrder(marketOrder, buyOrder, sellOrder);
            } else {
                matchLimitOrder(buyOrder, sellOrder);
            }

        }
    }

    // TODO check in market order timestamp is after corresponding limit order timestamp
    protected void matchMarketOrder(MarketOrder marketOrder, LimitOrder buyOrder, LimitOrder sellOrder) {
        if (marketOrder.getType().equals(OrderType.BUY)) {
            if (sellOrder != null) {
                Instant instant = Instant.now();
                tradeQueue.add(new Trade(marketOrder.getSymbol(), sellOrder.getPrice(), instant.getEpochSecond(), instant.getNano()));
            } else {
                marketOrder.setCancellationReason("No sell order found");
                cancellationQueue.add(marketOrder);
            }
        } else {
            if (buyOrder != null) {
                Instant instant = Instant.now();
                tradeQueue.add(new Trade(marketOrder.getSymbol(), buyOrder.getPrice(), instant.getEpochSecond(), instant.getNano()));
            } else {
                marketOrder.setCancellationReason("No buy order found");
                cancellationQueue.add(marketOrder);
            }
        }
    }

    protected void matchLimitOrder(LimitOrder buyOrder, LimitOrder sellOrder) {

    }

    public void stop() {
        matchingExecutor.shutdown();
    }

}
