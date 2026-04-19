package com.liveStock.tracker.Util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public KafkaConsumer(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @KafkaListener(topics = "live-crypto-tracking")
    public void consumeMessage(String message) {
        log.info("Consumed message: {}", message);
        simpMessagingTemplate.convertAndSend("/topic/Market", message);
    }

}
