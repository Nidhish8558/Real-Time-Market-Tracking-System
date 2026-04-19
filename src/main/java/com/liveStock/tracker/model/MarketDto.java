package com.liveStock.tracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketDto {
    private String symbol;
    private String price;

    @Override
    public String toString() {
        return "{" +
                "symbol='" + symbol + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}