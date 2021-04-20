package com.ltse.model;

import java.util.Comparator;

/**
 * Provides basic asc ordering by timestamp
 * @param <T>
 */
public class TimestampedComparator<T extends Timestamped> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        if (o1.getTime() == o2.getTime()) {
            if (o1.getNano() == o2.getNano()) {
                return 0;
            } else if (o1.getNano() < o2.getNano()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (o1.getTime() < o2.getTime()) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
