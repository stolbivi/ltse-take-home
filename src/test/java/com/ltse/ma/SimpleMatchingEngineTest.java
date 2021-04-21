package com.ltse.ma;

import com.ltse.model.*;
import com.ltse.queues.QueuesFactory;
import com.ltse.queues.SymbolQueues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SimpleMatchingEngineTest {

    private static SimpleMatchingEngine engine;
    private static final QueuesFactory queuesFactory = new QueuesFactory(new ComparatorFactory()) {
        @Override
        protected List<String> readFile(String fileName) throws IOException {
            List<String> mockList = new ArrayList();
            mockList.add("header");
            mockList.add("AAPL,false");
            return mockList;
        }
    };
    private SymbolQueues queues;
    private PriorityBlockingQueue<AbstractOrder> cancellationQueue;
    private PriorityBlockingQueue<Trade> tradeQueue;

    @BeforeEach
    public void init() throws IOException {
        Map<String, SymbolQueues> map = queuesFactory.createSymbolQueues("dummyFile");
        queues = map.get("AAPL");
        cancellationQueue = queuesFactory.createAbstractOrderQueue();
        tradeQueue = queuesFactory.createTimestampedQueue();
        engine = new SimpleMatchingEngine(queues, cancellationQueue, tradeQueue);
    }

    @Test
    public void testMatchMarketTrade() {
        engine.matchMarketOrder(
                new MarketOrder(OrderType.BUY, "AAPL", 1, 1),
                null,
                new LimitOrder(OrderType.SELL, "AAPL", 1, 2, 100.1)
        );
        assertEquals(1, tradeQueue.size());
        assertEquals(0, cancellationQueue.size());
        assertEquals(100.1, tradeQueue.poll().getPrice());
        engine.matchMarketOrder(
                new MarketOrder(OrderType.SELL, "AAPL", 1, 1),
                new LimitOrder(OrderType.BUY, "AAPL", 1, 2, 200.2),
                null
        );
        assertEquals(1, tradeQueue.size());
        assertEquals(0, cancellationQueue.size());
        assertEquals(200.2, tradeQueue.poll().getPrice());
    }

    @Test
    public void testMatchMarketCancelled() {
        engine.matchMarketOrder(
                new MarketOrder(OrderType.BUY, "AAPL", 1, 1),
                new LimitOrder(OrderType.BUY, "AAPL", 1, 2, 100.1),
                null
        );
        assertEquals(0, tradeQueue.size());
        assertEquals(1, cancellationQueue.size());
        assertNotNull(cancellationQueue.poll().getCancellationReason());
        engine.matchMarketOrder(
                new MarketOrder(OrderType.SELL, "AAPL", 1, 1),
                null,
                new LimitOrder(OrderType.SELL, "AAPL", 1, 2, 100.1)
        );
        assertEquals(0, tradeQueue.size());
        assertEquals(1, cancellationQueue.size());
        assertNotNull(cancellationQueue.poll().getCancellationReason());
    }

    @Test
    public void testMatchLimitTrade() {
        LimitOrder buyOrder = new LimitOrder(OrderType.BUY, "AAPL", 1, 1, 101);
        LimitOrder sellOrder = new LimitOrder(OrderType.SELL, "AAPL", 1, 2, 100.1);
        queues.getBuyQueue().add(buyOrder);
        queues.getSellQueue().add(sellOrder);
        engine.matchLimitOrder(buyOrder, sellOrder);
        assertEquals(0, queues.getBuyQueue().size());
        assertEquals(0, queues.getSellQueue().size());
        assertEquals(1, tradeQueue.size());
        assertEquals(0, cancellationQueue.size());
        assertEquals(100.1, tradeQueue.poll().getPrice());
    }

    @Test
    public void testMatchLimitNoTrade() {
        LimitOrder buyOrder = new LimitOrder(OrderType.BUY, "AAPL", 1, 1, 101);
        LimitOrder sellOrder = new LimitOrder(OrderType.SELL, "AAPL", 1, 2, 101.1);
        queues.getBuyQueue().add(buyOrder);
        queues.getSellQueue().add(sellOrder);
        engine.matchLimitOrder(buyOrder, sellOrder);
        assertEquals(1, queues.getBuyQueue().size());
        assertEquals(1, queues.getSellQueue().size());
        assertEquals(0, tradeQueue.size());
        assertEquals(0, cancellationQueue.size());
    }

}
