package com.ltse.model;

public abstract class Timestamped {

    private long time;
    private int nano;

    public Timestamped(long time, int nano) {
        this.time = time;
        this.nano = nano;
    }

    public long getTime() {
        return time;
    }

    public int getNano() {
        return nano;
    }

}
