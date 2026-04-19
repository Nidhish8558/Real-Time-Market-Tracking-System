package com.liveStock.tracker.Util;

import com.liveStock.tracker.model.MarketDto;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class BinanceParser {

    private final ObjectMapper mapper = new ObjectMapper();

    public MarketDto parse(String json) {
        try {
            JsonNode node = mapper.readTree(json);

            String stream = node.get("stream").asString();
            JsonNode data = node.get("data");
            MarketDto dto = new MarketDto();
            dto.setSymbol(stream.split("@")[0].toUpperCase());
            dto.setPrice(data.get("p").asText());

            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Parsing failed", e);
        }
    }
}