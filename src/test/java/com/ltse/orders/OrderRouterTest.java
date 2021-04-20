package com.ltse.orders;

import com.ltse.model.AbstractOrder;
import com.ltse.model.OrderComparatorFactory;
import com.ltse.queues.QueuesFactory;
import com.ltse.queues.SymbolQueues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrderRouterTest {

    private static OrderRouter orderRouter;
    private static QueuesFactory queuesFactory;
    private static Map<String, SymbolQueues> queues;
    private static PriorityBlockingQueue<? extends AbstractOrder> orderQueue;
    private static PriorityBlockingQueue<? extends AbstractOrder> cancellationQueue;

    @BeforeEach
    public void init() throws IOException {
        queuesFactory = new QueuesFactory(new OrderComparatorFactory());
        queues = queuesFactory.createSymbolQueues("./src/test/resources/symbols.csv");
        orderQueue = queuesFactory.createTimeOrderedQueue();
        cancellationQueue = queuesFactory.createTimeOrderedQueue();
        orderRouter = new OrderRouter("./src/test/resources/orders.csv", queues, orderQueue, cancellationQueue);

    }

    @Test
    public void testSubscribe() {
        orderRouter.subscribeToOrders(() -> {
            assertEquals(4, orderQueue.size());
            assertEquals("FB", orderQueue.poll().getSymbol());
            assertEquals("GOOG", orderQueue.poll().getSymbol());
            assertEquals("AAPL", orderQueue.poll().getSymbol());
            assertEquals("AMZN", orderQueue.poll().getSymbol());
            assertEquals(0, orderQueue.size());
        });
    }

    @Test
    public void testStart() {
        orderRouter.start();
        orderRouter.subscribeToOrders(() -> {
            while(!orderQueue.isEmpty()) {
                continue;
            }
            assertEquals(5, queues.size());
            assertEquals(1954823, queues.get("FB").getMarketQueue().poll().getNanoseconds());
            assertEquals(9252071, queues.get("AMZN").getMarketQueue().poll().getNanoseconds());
            assertNull(queues.get("GOOG").getMarketQueue().poll());
            assertEquals(7614357, queues.get("AAPL").getBuyQueue().poll().getNanoseconds());
            assertEquals(4212484, cancellationQueue.poll().getNanoseconds());
        });
    }

}
