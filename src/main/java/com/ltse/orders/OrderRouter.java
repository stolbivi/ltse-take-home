package com.ltse.orders;

import com.ltse.model.AbstractOrder;
import com.ltse.model.LimitOrder;
import com.ltse.model.MarketOrder;
import com.ltse.model.OrderParser;
import com.ltse.queues.SymbolQueues;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 */
public class OrderRouter {

    private String ordersFileName;
    private ExecutorService readExecutor = Executors.newSingleThreadExecutor();
    private OrderParser parser = new OrderParser();

    private PriorityBlockingQueue<AbstractOrder> orderQueue;
    private Map<String, SymbolQueues> symbolQueues;
    private PriorityBlockingQueue<AbstractOrder> cancellationQueue;
    private ExecutorService routeExecutor = Executors.newSingleThreadExecutor();

    public OrderRouter(String ordersFileName, Map<String, SymbolQueues> symbolQueues, PriorityBlockingQueue orderQueue, PriorityBlockingQueue cancellationQueue) {
        this.orderQueue = orderQueue;
        this.ordersFileName = ordersFileName;
        this.symbolQueues = symbolQueues;
        this.cancellationQueue = cancellationQueue;
    }

    /**
     * Routes orders from order queue to dedicated symbol queue
     */
    public void start() {
        routeExecutor.execute(() -> {
            while (!routeExecutor.isTerminated()) {
                try {
                    AbstractOrder order = orderQueue.poll();
                    if (order == null) {
                        continue;
                    }
                    String symbol = order.getSymbol();
                    SymbolQueues queues = symbolQueues.get(symbol);
                    if (queues == null) {
                        order.setCancellationReason("Symbol not supported");
                        cancellationQueue.add(order);
                        continue;
                    }
                    if (queues.getHalted()) {
                        order.setCancellationReason("Symbol is halted");
                        cancellationQueue.add(order);
                        continue;
                    }
                    if (order instanceof MarketOrder) {
                        queues.getMarketQueue().add((MarketOrder) order);
                    } else if (order instanceof LimitOrder) {
                        LimitOrder limitOrder = (LimitOrder) order;
                        switch (limitOrder.getType()) {
                            case BUY:
                                queues.getBuyQueue().add((LimitOrder) order);
                                break;
                            case SELL:
                                queues.getSellQueue().add((LimitOrder) order);
                                break;
                        }
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    /**
     * This method simulates external connectivity to order stream, in particular implementation it reads file in a separate thread and populates orderQueue
     */
    protected void subscribeToOrders(Runnable callback) {
        readExecutor.execute(() -> {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(this.ordersFileName));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    AbstractOrder order = parser.parse(line);
                    if (order == null) {
                        continue;
                    }
                    this.orderQueue.add(order);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (callback != null) {
                    callback.run();
                }
            }
        });
    }

}
