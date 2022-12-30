package org.tools.middleware;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;

/**
 * The type Kafka tools.
 */
public class KafkaTools {
    static final String KAFKA_SERVERS = "192.168.107.129:9092";
    static final String TOPIC = "tools-test";

    public static void main(String[] args) {
//        kafkaProducer();
        kafkaConsumer();
    }

    /**
     * 生产者
     */
    public static void kafkaProducer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KAFKA_SERVERS);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);

        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties)){
            // send方法中ProducerRecord有三个参数，第一个是指定发送的主题，第二个是设置消息的key(非必填),第三个是消息value
            kafkaProducer.send(new ProducerRecord<String, String>(TOPIC, "hello"));
        }
    }

    /**
     * 消费者
     */
    public static void kafkaConsumer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KAFKA_SERVERS);
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "group-1");
        properties.put("auto.offset.reset", "earliest");
        properties.put("auto.commit.intervals.ms", "true"); // 自动提交偏移量
        properties.put("auto.commit.interval.ms", "1000"); // 自动提交时间

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)){
            ArrayList<String> topics = new ArrayList<>();
            topics.add(TOPIC);
            consumer.subscribe(topics); // 可以订阅多个Topic的消息
//            while (true) {
                ConsumerRecords<String, String> poll = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : poll) {
                    System.out.println(record.key() + ", " + record.value());
                }
//            }
        }
    }
}
