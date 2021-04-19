package com.ltse.queues;

import com.ltse.model.OrderComparatorFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class QueuesFactoryTest {

    private static final QueuesFactory factory = new QueuesFactory(new OrderComparatorFactory());

    @Test
    public void testSymbolsFile() throws IOException {
        Map<String, SymbolQueues> map = factory.createSymbolQueues("./src/main/resources/symbols.csv");
        assertEquals(5, map.size());
        assertFalse(map.get("AAPL").getHalted());
        assertTrue( map.get("GOOG").getHalted());
        assertEquals( 0, map.get("AMZN").getBuyQueue().size());
        assertEquals( 0, map.get("AMZN").getSellQueue().size());
    }

}
