package com.ltse;

import com.ltse.ma.SimpleMatchingEngine;
import com.ltse.model.*;
import com.ltse.orders.OrderRouter;
import com.ltse.queues.QueuesFactory;
import com.ltse.queues.SymbolQueues;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Map<String, SimpleMatchingEngine> engines = new HashMap<>();
        try {
            QueuesFactory queuesFactory = new QueuesFactory(new ComparatorFactory());

            PriorityBlockingQueue<AbstractOrder> orderQueue = queuesFactory.createAbstractOrderQueue();
            PriorityBlockingQueue<AbstractOrder> cancellationQueue = queuesFactory.createAbstractOrderQueue();
            PriorityBlockingQueue<Trade> tradeQueue = queuesFactory.createTimestampedQueue();

            Map<String, SymbolQueues> symbolQueues = queuesFactory.createSymbolQueues("./src/main/resources/symbols.csv");

            OrderRouter orderRouter = new OrderRouter("./src/main/resources/orders.csv", symbolQueues, orderQueue, cancellationQueue);
            orderRouter.start();
            orderRouter.subscribeToOrders(() -> {
                System.out.println("All records from the file have been processed");
            });

            for (Map.Entry<String, SymbolQueues> entry : symbolQueues.entrySet()) {
                SimpleMatchingEngine engine = new SimpleMatchingEngine(entry.getValue(), cancellationQueue, tradeQueue);
                engines.put(entry.getKey(), engine);
                engine.start();
            }

            System.out.println("The system is designed to process incoming order continuously and requires explicit shutdown.");
            System.out.println("Waiting for 10 secs to allow system to complete processing all orders");
            Thread.sleep(10000);

            System.out.println("Shutting down now");
            orderRouter.stop();
            for (SimpleMatchingEngine engine : engines.values()) {
                engine.stop();
            }

            System.out.println("Writing results");
            printQueues("./trades.txt", tradeQueue);
            printQueues("./rejected.txt", cancellationQueue);
            List<PriorityBlockingQueue<? extends AbstractOrder>> allQueues = new ArrayList<>();
            symbolQueues.values().stream().map(v -> v.getBuyQueue()).collect(Collectors.toCollection(() -> allQueues));
            symbolQueues.values().stream().map(v -> v.getSellQueue()).collect(Collectors.toCollection(() -> allQueues));
            printQueues("./orderbook.txt", allQueues.toArray(new PriorityBlockingQueue[allQueues.size()]));

            System.out.println("Bye!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printQueues(String fileName, PriorityBlockingQueue<? extends Timestamped>... queues) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(fileName)))) {
            for (int i = 0; i < queues.length; i++) {
                while (!queues[i].isEmpty()) {
                    Timestamped timestamped = queues[i].poll();
                    if (timestamped instanceof LimitOrder) {
                        LimitOrder limitOrder = (LimitOrder) timestamped;
                        if (limitOrder.getCancellationReason() != null) {
                            bufferedWriter.write(String.format("%s,%s,%f,%d.%d,%s\n", limitOrder.getSymbol(), limitOrder.getType(),
                                    limitOrder.getPrice(), limitOrder.getTime(), limitOrder.getNano(), limitOrder.getCancellationReason()));
                        } else {
                            bufferedWriter.write(String.format("%s,%s,%f,%d.%d\n", limitOrder.getSymbol(), limitOrder.getType(),
                                    limitOrder.getPrice(), limitOrder.getTime(), limitOrder.getNano()));
                        }
                    } else if (timestamped instanceof MarketOrder) {
                        MarketOrder marketOrder = (MarketOrder) timestamped;
                        if (marketOrder.getCancellationReason() != null) {
                            bufferedWriter.write(String.format("%s,%s,%d.%d,%s\n", marketOrder.getSymbol(), marketOrder.getType(),
                                    marketOrder.getTime(), marketOrder.getNano(), marketOrder.getCancellationReason()));
                        } else {
                            bufferedWriter.write(String.format("%s,%s,%d.%d\n", marketOrder.getSymbol(), marketOrder.getType(),
                                    marketOrder.getTime(), marketOrder.getNano()));
                        }
                    } else if (timestamped instanceof Trade) {
                        Trade trade = (Trade) timestamped;
                        bufferedWriter.write(String.format("%s,%f,%d.%d\n", trade.getSymbol(), trade.getPrice(), trade.getTime(), trade.getNano()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
