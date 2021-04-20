package com.ltse.queues;

import com.ltse.model.OrderComparatorFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class QueuesFactoryTest {

    private static final QueuesFactory factory = new QueuesFactory(new OrderComparatorFactory()){
        @Override
        protected List<String> readFile(String fileName) throws IOException {
            List<String> mockList = new ArrayList();
            mockList.add("header");
            mockList.add("AAPL,false");
            mockList.add("GOOG,true");
            mockList.add("AMZN,false");
            return mockList;
        }
    };

    @Test
    public void testSymbolsFile() throws IOException {
        Map<String, SymbolQueues> map = factory.createSymbolQueues("dummyPath");
        assertEquals(3, map.size());
        assertFalse(map.get("AAPL").getHalted());
        assertTrue( map.get("GOOG").getHalted());
        assertEquals( 0, map.get("AMZN").getMarketQueue().size());
        assertEquals( 0, map.get("AMZN").getBuyQueue().size());
        assertEquals( 0, map.get("AMZN").getSellQueue().size());
    }

}
