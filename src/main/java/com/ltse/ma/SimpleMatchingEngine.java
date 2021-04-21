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
        matchingExecutor.execute(() -> {
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
        });
    }

    protected void matchMarketOrder(MarketOrder marketOrder, LimitOrder buyOrder, LimitOrder sellOrder) {
        if (marketOrder.getType().equals(OrderType.BUY)) {
            if (sellOrder != null) {
                Instant instant = Instant.now();
                Trade trade = new Trade(marketOrder.getSymbol(), sellOrder.getPrice(), instant.getEpochSecond(), instant.getNano());
                tradeQueue.add(trade);
                System.out.println("Matching " + marketOrder + " with " + sellOrder + " resulted in trade " + trade);
            } else {
                marketOrder.setCancellationReason("No sell order found");
                cancellationQueue.add(marketOrder);
                System.out.println("Matching " + marketOrder + " resulted in cancellation");
            }
        } else {
            if (buyOrder != null) {
                Instant instant = Instant.now();
                Trade trade = new Trade(marketOrder.getSymbol(), buyOrder.getPrice(), instant.getEpochSecond(), instant.getNano());
                tradeQueue.add(trade);
                System.out.println("Matching " + marketOrder + " with " + buyOrder + " resulted in trade " + trade);
            } else {
                marketOrder.setCancellationReason("No buy order found");
                cancellationQueue.add(marketOrder);
                System.out.println("Matching " + marketOrder + " resulted in cancellation");
            }
        }
    }

    protected void matchLimitOrder(LimitOrder buyOrder, LimitOrder sellOrder) {
        if (buyOrder != null && sellOrder != null) {
            if (buyOrder.getPrice() >= sellOrder.getPrice()) {
                // since there's only 1 thread to consume an order we can safely assume polling order again can only result
                // in a buy order to heave equal or greater bid price and a sell order to have smaller or equal ask price
                LimitOrder bOrder = queues.getBuyQueue().poll();
                LimitOrder sOrder = queues.getSellQueue().poll();
                Instant instant = Instant.now();
                Trade trade = new Trade(bOrder.getSymbol(), sOrder.getPrice(), instant.getEpochSecond(), instant.getNano());
                System.out.println("Matching " + bOrder + " with " + sOrder + " resulted in trade " + trade);
                tradeQueue.add(trade);
            }
        }
    }

    public void stop() {
        matchingExecutor.shutdown();
    }

}
