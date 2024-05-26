package com.heima.kafka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/26
 */
@RestController
public class KafkaController {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping ("/{message}")
    public String addMessage(@PathVariable("message") String message) {
        kafkaTemplate.send("test", message);
        return "ok";
    }
}
