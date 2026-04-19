package com.liveStock.tracker.service;

import com.liveStock.tracker.Util.BinanceParser;
import com.liveStock.tracker.Util.KafkaProducer;
import com.liveStock.tracker.model.MarketDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BinanceWebSocketClient {
    private WebSocketSession session;
    private static final List<String> symbols = List.of("btcusdt", "ethusdt", "solusdt");

    @Autowired
    private BinanceParser parser;

    private static final String streams = symbols.stream().map(s -> s + "@trade").collect(Collectors.joining("/"));
    private static final String url = "wss://stream.binance.com:9443/stream?streams="+streams;

    @Autowired
    private KafkaProducer kafkaProducer;

    @PostConstruct
    public void init() {
        connect();
    }

    public void connect() {

        StandardWebSocketClient client = new StandardWebSocketClient();

        WebSocketConnectionManager manager =
                new WebSocketConnectionManager(
                        client,
                        new TextWebSocketHandler() {

                            @Override
                            public void afterConnectionEstablished(WebSocketSession session) {
                                BinanceWebSocketClient.this.session = session;
                                log.info("Connected to Binance WebSocket");
                            }

                            @Override
                            public void handleTextMessage(WebSocketSession session,
                                                          org.springframework.web.socket.TextMessage message) {

                                try {
//                                    log.info("Received: {}", message.getPayload());

                                    MarketDto dto = parser.parse(message.getPayload());
                                    kafkaProducer.sendMessage("live-crypto-tracking",new ObjectMapper().writeValueAsString(dto));
//                                    kafkaProducer.sendMessage("live-crypto-tracking",dto);
//                                    log.info("sent to kafka: {}", dto);
//                                    messagingTemplate.convertAndSend("/topic/Market", dto);

                                } catch (Exception e) {
                                    log.error("Parsing/Sending error", e);
                                }
                            }

                            @Override
                            public void handleTransportError(WebSocketSession session, Throwable exception) {
                                log.error("Error: ", exception);
                                reconnect();
                            }

                        },
                        url
                );

        manager.start();
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