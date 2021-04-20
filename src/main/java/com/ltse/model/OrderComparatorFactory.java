package com.ltse.model;

import java.util.Comparator;

public class OrderComparatorFactory {

    /**
     * Provides desc order by price and asc order by timestamp for buy orders
     */
    private class BidPriceLimitComparator extends AbstractOrderComparator<LimitOrder> {
        @Override
        public int compare(LimitOrder o1, LimitOrder o2) {
            if (o1.getPrice() == o2.getPrice()) {
                return super.compare(o1, o2);
            } else {
                if (o1.getPrice() < o2.getPrice()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
    }

    /**
     * Provides ask order by price and asc order by timestamp for sell orders
     */
    private class AskPriceLimitComparator extends AbstractOrderComparator<LimitOrder> {
        @Override
        public int compare(LimitOrder o1, LimitOrder o2) {
            if (o1.getPrice() == o2.getPrice()) {
                return super.compare(o1, o2);
            } else {
                if (o1.getPrice() < o2.getPrice()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    }

    /**
     * Provides asc order by timestamp for any market order
     */
    private class TimeOnlyComparator extends AbstractOrderComparator<AbstractOrder> {
        @Override
        public int compare(AbstractOrder o1, AbstractOrder o2) {
            return super.compare(o1, o2);
        }
    }


    /**
     * Creates comparator for specific queue based on order type.
     *
     * @param type
     * @return
     */
    public Comparator<LimitOrder> createLimitOrderComparator(OrderType type) {
        return (
                switch (type) {
                    case BUY -> new BidPriceLimitComparator();
                    case SELL -> new AskPriceLimitComparator();
                    default -> throw new RuntimeException("Unsupported order typ:" + type);
                }
        );
    }

    /**
     * Creates comparator for any market order
     *
     * @return
     */
    public Comparator<AbstractOrder> createTimeOnlyComparator() {
        return new TimeOnlyComparator();
    }

}
