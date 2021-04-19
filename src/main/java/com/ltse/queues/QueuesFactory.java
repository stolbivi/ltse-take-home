package com.ltse.queues;

import com.ltse.model.LimitOrder;
import com.ltse.model.OrderComparatorFactory;
import com.ltse.model.OrderType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

public class QueuesFactory {

    private static final int DEFAULT_QUEUE_SIZE = 1000;
    private OrderComparatorFactory comparatorFactory;

    public QueuesFactory(OrderComparatorFactory comparatorFactory) {
        this.comparatorFactory = comparatorFactory;
    }

    /**
     * Returns a map of symbols to queues with information if the symbol is halted
     *
     * @param symbolsFile path to symbol file
     * @return
     * @throws IOException
     */
    public Map<String, SymbolQueues> createSymbolQueues(String symbolsFile) throws IOException {
        Map<String, SymbolQueues> result = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(symbolsFile), StandardCharsets.UTF_8);
        boolean skipHeader = true;
        for (String line : lines) {
            if (skipHeader) {
                skipHeader = false;
                continue;
            }
            String[] tokens = line.split(",");
            SymbolQueues queues = new SymbolQueues(
                    tokens[0],
                    Boolean.valueOf(tokens[1]),
                    createQueue(comparatorFactory.createLimitOrderComparator(OrderType.BUY)),
                    createQueue(comparatorFactory.createLimitOrderComparator(OrderType.SELL))
            );
            result.put(tokens[0], queues);
        }
        return result;
    }

    private PriorityBlockingQueue createQueue(Comparator<LimitOrder> comparator) {
        return new PriorityBlockingQueue(DEFAULT_QUEUE_SIZE, comparator);
    }

}
