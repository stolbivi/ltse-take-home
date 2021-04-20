package com.ltse.model;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static com.ltse.model.OrderType.BUY;
import static com.ltse.model.OrderType.SELL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderComparatorFactoryTest {

    private static final OrderComparatorFactory factory = new OrderComparatorFactory();

    private LimitOrder limitOrder(OrderType type, String symbol, int time, int nanoseconds, double price) {
        return new LimitOrder(type, symbol, time, nanoseconds, price);
    }

    private MarketOrder marketOrder(OrderType type, String symbol, int time, int nanoseconds) {
        return new MarketOrder(type, symbol, time, nanoseconds);
    }

    @Test
    public void testBuyLimitOrderComparator() {
        Comparator<LimitOrder> buyComparator = factory.createLimitOrderComparator(BUY);
        assertEquals(0, buyComparator.compare(
                limitOrder(BUY, "AAPL", 1, 1000, 100),
                limitOrder(BUY, "AAPL", 1, 1000, 100)
        ));
        assertEquals(-1, buyComparator.compare(
                limitOrder(BUY, "AAPL", 1, 1000, 100),
                limitOrder(BUY, "AAPL", 2, 1000, 100)
        ));
        assertEquals(1, buyComparator.compare(
                limitOrder(BUY, "AAPL", 2, 1000, 100),
                limitOrder(BUY, "AAPL", 1, 1000, 100)
        ));
        assertEquals(-1, buyComparator.compare(
                limitOrder(BUY, "AAPL", 1, 1, 100),
                limitOrder(BUY, "AAPL", 1, 1000, 100)
        ));
        assertEquals(1, buyComparator.compare(
                limitOrder(BUY, "AAPL", 1, 1000, 100),
                limitOrder(BUY, "AAPL", 1, 1, 100)
        ));
        assertEquals(1, buyComparator.compare(
                limitOrder(BUY, "AAPL", 1, 1000, 1),
                limitOrder(BUY, "AAPL", 1, 1000, 100)
        ));
        assertEquals(-1, buyComparator.compare(
                limitOrder(BUY, "AAPL", 1, 1000, 200),
                limitOrder(BUY, "AAPL", 1, 1000, 100)
        ));
    }

    @Test
    public void testSellLimitOrderComparator() {
        Comparator<LimitOrder> sellComparator = factory.createLimitOrderComparator(SELL);
        assertEquals(0, sellComparator.compare(
                limitOrder(BUY, "GOOG", 1, 1000, 100),
                limitOrder(BUY, "GOOG", 1, 1000, 100)
        ));
        assertEquals(-1, sellComparator.compare(
                limitOrder(BUY, "GOOG", 1, 1000, 100),
                limitOrder(BUY, "GOOG", 2, 1000, 100)
        ));
        assertEquals(1, sellComparator.compare(
                limitOrder(BUY, "GOOG", 2, 1000, 100),
                limitOrder(BUY, "GOOG", 1, 1000, 100)
        ));
        assertEquals(-1, sellComparator.compare(
                limitOrder(BUY, "GOOG", 1, 1, 100),
                limitOrder(BUY, "GOOG", 1, 1000, 100)
        ));
        assertEquals(1, sellComparator.compare(
                limitOrder(BUY, "GOOG", 1, 1000, 100),
                limitOrder(BUY, "GOOG", 1, 1, 100)
        ));
        assertEquals(-1, sellComparator.compare(
                limitOrder(BUY, "GOOG", 1, 1000, 1),
                limitOrder(BUY, "GOOG", 1, 1000, 100)
        ));
        assertEquals(1, sellComparator.compare(
                limitOrder(BUY, "GOOG", 1, 1000, 200),
                limitOrder(BUY, "GOOG", 1, 1000, 100)
        ));
    }

    @Test
    public void testMarketOrderComparator() {
        Comparator<AbstractOrder> comparator = factory.createTimeOnlyComparator();
        assertEquals(0, comparator.compare(
                marketOrder(BUY, "FB", 1, 1000),
                marketOrder(BUY, "FB", 1, 1000)
        ));
        assertEquals(-1, comparator.compare(
                marketOrder(SELL, "FB", 1, 1000),
                marketOrder(SELL, "FB", 2, 1000)
        ));
        assertEquals(1, comparator.compare(
                marketOrder(BUY, "FB", 2, 1000),
                marketOrder(SELL, "FB", 1, 1000)
        ));
        assertEquals(-1, comparator.compare(
                marketOrder(SELL, "FB", 1, 1),
                marketOrder(BUY, "FB", 1, 1000)
        ));
        assertEquals(1, comparator.compare(
                marketOrder(BUY, "FB", 1, 1000),
                marketOrder(BUY, "FB", 1, 1)
        ));
    }

}
