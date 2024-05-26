package com.heima.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/25
 */
public class ProducerQuickStart {
    public static void main(String[] args) {
        Properties properties = new Properties();
        // kafka监听端口
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "124.223.106.233:9092");
        // 序列化器
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // 根据配置创建kafka
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // 创建消息
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<String, String>("topic-1", "key-1", "success!");
        // 发送消息
        producer.send(producerRecord);

        // 关闭通道才能成功发送
        producer.close();
    }
}
