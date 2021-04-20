package com.ltse.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderParserTest {

    private static final OrderParser parser = new OrderParser();

    @Test
    public void testParseLimitOrder() {
        AbstractOrder order = parser.parse("AAPL,buy,limit,130.98,1608917400.7614357");
        assertTrue(order instanceof LimitOrder);
        LimitOrder limitOrder = (LimitOrder) order;
        assertEquals(OrderType.BUY, limitOrder.getType());
        assertEquals(1608917400, limitOrder.getTime());
        assertEquals(7614357, limitOrder.getNanoseconds());
        assertEquals(130.98, limitOrder.getPrice());
    }

    @Test
    public void testParseLimitOrderIncomplete() {
        assertNull(parser.parse(",buy,limit,130.98,1608917400.7614357"));
        assertNull(parser.parse("AAPL,,limit,,1608917400.7614357"));
        assertNull(parser.parse("AAPL,buy,,130.98,1608917400.7614357"));
        assertNull(parser.parse("AAPL,buy,limit,,1608917400.7614357"));
        assertNull(parser.parse("AAPL,buy,limit,130.98,"));
    }

    @Test
    public void testParseMarketOrder() {
        AbstractOrder order = parser.parse("AAPL,buy,market,,1608917400.7614357");
        assertTrue(order instanceof MarketOrder);
        MarketOrder marketOrder = (MarketOrder) order;
        assertEquals(OrderType.BUY, marketOrder.getType());
        assertEquals(1608917400, marketOrder.getTime());
        assertEquals(7614357, marketOrder.getNanoseconds());
    }

}
