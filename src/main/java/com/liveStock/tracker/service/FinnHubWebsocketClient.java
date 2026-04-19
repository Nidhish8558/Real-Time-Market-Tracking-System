package com.liveStock.tracker.service;

import com.liveStock.tracker.Util.KafkaProducer;
import com.liveStock.tracker.model.MarketDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class FinnHubWebsocketClient {
    private WebSocketSession session;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String url = "wss://ws.finnhub.io?token=" + "d7hmu7hr01qhiu0c71qgd7hmu7hr01qhiu0c71r0";

    @Autowired
    private KafkaProducer kafkaProducer;

    @PostConstruct
    public void init() {
        connect();
    }

    public void connect() {
        // Implement connection logic to FinnHub WebSocket API
        // Similar to BinanceWebSocketClient but with FinnHub's specific URL and message handling
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        WebSocketConnectionManager manager = new WebSocketConnectionManager(
                standardWebSocketClient,
                new TextWebSocketHandler() {
                    @Override
                    public void afterConnectionEstablished(WebSocketSession session) {
                        FinnHubWebsocketClient.this.session = session;
                        subscribe("AAPL"); // Apple
                        subscribe("TSLA"); // Tesla
                        subscribe("NVDA"); // Nvidia (VERY ACTIVE)
                        subscribe("AMZN"); // Amazon

                        log.info("Connected to FinnHub WebSocket");
                    }

                    @Override
                    public void handleTextMessage(WebSocketSession session,
                                                  org.springframework.web.socket.TextMessage message) {
                        log.info("Received message from FinnHub: " + message.getPayload());
                        JsonNode jsonNode = mapper.readTree(message.getPayload());
                        String type = jsonNode.get("type").asString();
                        if(!Strings.isEmpty(type) && null!=type && type.equalsIgnoreCase("trade")){
                            JsonNode data = jsonNode.get("data");
                            if(data.isArray()){
                                data.forEach(trade -> {
                                    String symbol = trade.get("s").asText();
                                    String price = trade.get("p").asText();
                                    // Create a DTO or Map to send to clients
                                    // For example:
                                    MarketDto stockUpdate = new MarketDto();
                                    stockUpdate.setSymbol(symbol);
                                    stockUpdate.setPrice(price);
                                    log.info("Parsed trade - Symbol: {}, Price: {}", symbol, price);
                                    kafkaProducer.sendMessage("live-crypto-tracking", new ObjectMapper().writeValueAsString(stockUpdate));
//                                    kafkaProducer.sendMessage("live-crypto-tracking", stockUpdate);
//                                    messagingTemplate.convertAndSend("/topic/Market", stockUpdate);
                                });
                            }
                        }
                        // Handle incoming messages from FinnHub and send updates to clients via messagingTemplate
                        // Parse the message and convert it to a suitable format before sending
                    }

                    @Override
                    public void handleTransportError(WebSocketSession session, Throwable exception) {
                        log.error("Error: ", exception);
                        reconnect();
                    }
                },
                url);
        manager.start();
    }

    private void subscribe(String text) {
        if (session != null && session.isOpen()) {
            String subscribeMessage = "{\"type\":\"subscribe\",\"symbol\":\"" + text + "\"}";
            try {
                session.sendMessage(new org.springframework.web.socket.TextMessage(subscribeMessage));
                log.info("Subscribed to " + text);
            } catch (Exception e) {
                log.error("Failed to subscribe to " + text, e);
            }
        } else {
            log.warn("WebSocket session is not open. Cannot subscribe to " + text);
        }
    }

    private void reconnect() {
        try {
            Thread.sleep(3000);
            connect();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
