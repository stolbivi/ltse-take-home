package com.ltse.queues;

import com.ltse.model.AbstractOrder;
import com.ltse.model.ComparatorFactory;
import com.ltse.model.OrderType;
import com.ltse.model.Timestamped;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public class QueuesFactory {

    private static final int DEFAULT_QUEUE_SIZE = 1000;
    private ComparatorFactory comparatorFactory;

    public QueuesFactory(ComparatorFactory comparatorFactory) {
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
        List<String> lines = readFile(symbolsFile);
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
                    createQueue(comparatorFactory.createTimestampedComparator()),
                    createQueue(comparatorFactory.createLimitOrderComparator(OrderType.BUY)),
                    createQueue(comparatorFactory.createLimitOrderComparator(OrderType.SELL))
            );
            result.put(tokens[0], queues);
        }
        return result;
    }

    public PriorityBlockingQueue<AbstractOrder> createAbstractOrderQueue() {
        return new PriorityBlockingQueue(DEFAULT_QUEUE_SIZE, comparatorFactory.createTimestampedComparator());
    }

    public <T extends Timestamped> PriorityBlockingQueue<T> createTimestampedQueue() {
        return new PriorityBlockingQueue(DEFAULT_QUEUE_SIZE, comparatorFactory.createTimestampedComparator());
    }

    private PriorityBlockingQueue<? extends Timestamped> createQueue(Comparator<? extends Timestamped> comparator) {
        return new PriorityBlockingQueue(DEFAULT_QUEUE_SIZE, comparator);
    }

    protected List<String> readFile(String fileName) throws IOException {
        return Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
    }

}
