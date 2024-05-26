package com.heima.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/25
 */
public class ConsumerQuickStart {
    public static void main(String[] args) {
        Properties properties = new Properties();
        // kafka监听接口
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "124.223.106.233:9092");

        // 反序列化器
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        /**
         * 设置组。
         * 当多个消费者在同一个组时，同一条消息只会被一个消费者获取，
         * 当消费者在不同组时，同一条消息会被不同组都获取。
         */
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-test");

        // 创建消费者
        KafkaConsumer consumer = new KafkaConsumer(properties);

        // 订阅主题，主题可以是多个
        consumer.subscribe(Collections.singletonList("topic-1"));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.key());
                System.out.println(record.value());
            }
        }
    }
}
