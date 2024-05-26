package com.heima.kafka.listener;

import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.util.StringUtils;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/26
 */
@Component
public class MessageListener {
    @KafkaListener(topics = "test")
    public void getMessage(String message) {
        if (!StringUtils.isEmpty(message)) {
            System.out.println(message);
        }
    }
}
