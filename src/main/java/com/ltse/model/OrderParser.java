package com.ltse.model;

public class OrderParser {

    private static final String MARKET = "market";
    private static final String LIMIT = "limit";
    private static final String BUY = "buy";
    private static final String SELL = "sell";

    public AbstractOrder parse(String line) {
        String[] tokens = line.split(",");
        if (tokens.length < 5) {
            return null;
        }
        if (tokens[0].isBlank()) {
            return null;
        }
        OrderType type = null;
        if (BUY.equals(tokens[1])) {
            type = OrderType.BUY;
        }
        if (SELL.equals(tokens[1])) {
            type = OrderType.SELL;
        }
        if (type == null) {
            return null;
        }
        if (tokens[4] == null || tokens[4].isBlank()) {
            return null;
        }
        String[] timestamp = tokens[4].split("\\.");
        if (timestamp.length != 2) {
            return null;
        }
        try {
            if (MARKET.equals(tokens[2])) {
                return new MarketOrder(type, tokens[0], Long.parseLong(timestamp[0]), Integer.parseInt(timestamp[1]));
            } else if (LIMIT.equals(tokens[2])) {
                if (tokens[3].isBlank()) {
                    return null;
                }
                return new LimitOrder(type, tokens[0], Long.parseLong(timestamp[0]), Integer.parseInt(timestamp[1]), Double.parseDouble(tokens[3]));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

}
