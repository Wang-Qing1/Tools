package org.tools.kafka;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;

/**
 * The type Kafka tools.
 */
public class KafkaTools {
    static final String KAFKA_SERVERS = "192.168.30.43:9092";
    static final String TOPIC = "tools-test";

    public static void main(String[] args) {
        kafkaProducer();
//        kafkaConsumer();
    }

    /**
     * 生产者
     */
    public static void kafkaProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVERS);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);

        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties)){
            // send方法中ProducerRecord有三个参数，第一个是指定发送的主题，第二个是设置消息的key(非必填),第三个是消息value
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            jsonObject.put("id", "25b4e520befd4175bb39075d029e95f2");
            jsonObject.put("orgId", "0424811733694cfbb8e8e771a79387c9");
            jsonObject.put("account", "TEST3");
            jsonObject.put("name", "测试3");
            jsonObject.put("phone", "18212341234");
            jsonObject.put("email", null);
            jsonArray.add(jsonObject);
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, jsonArray.toJSONString());
            Headers headers = record.headers();
            headers.add("dataType", "1".getBytes(StandardCharsets.UTF_8));
            headers.add("optionType", "add".getBytes(StandardCharsets.UTF_8));
            kafkaProducer.send(record);
        }

//        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties)){
//            // send方法中ProducerRecord有三个参数，第一个是指定发送的主题，第二个是设置消息的key(非必填),第三个是消息value
//            JSONArray jsonArray = new JSONArray();
//            JSONObject jsonObject = new JSONObject();
//            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
//            jsonObject.put("id", "b96192d3fbeb49c8873861d062c128c7");
//            jsonObject.put("parentId", "90e72710c5f6433194853213b169b472");
//            jsonObject.put("name", "商业部A");
//            jsonObject.put("shortName", "商业部");
//            jsonObject.put("type", 3);
//            jsonObject.put("disabled", false);
//            jsonObject.put("deleted", false);
//            jsonArray.add(jsonObject);
//            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, jsonArray.toJSONString());
//            Headers headers = record.headers();
//            headers.add("dataType", "2".getBytes(StandardCharsets.UTF_8));
//            headers.add("optionType", "delete".getBytes(StandardCharsets.UTF_8));
//            kafkaProducer.send(record);
//        }
    }

    /**
     * 消费者
     */
    public static void kafkaConsumer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KAFKA_SERVERS);
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "group-8");
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
                    Headers headers = record.headers();
                    Iterable<Header> dataType = headers.headers("dataType");
                    if (dataType != null) {
                        Iterator<Header> iterator = dataType.iterator();
                        if (iterator.hasNext()) {
                            Header next = iterator.next();
                            System.out.println(next.key() + " , " + new String(next.value(), StandardCharsets.UTF_8));
                        }
                    }
                    Iterator<Header> aaa = headers.headers("aaa").iterator();
                    if (aaa.hasNext()) {
                        System.out.println(aaa.next().key() + " , " + new String(aaa.next().value(), StandardCharsets.UTF_8));
                    }
                    System.out.println("======================");
                    System.out.println(record.key() + ", " + record.value());
                }
//            }
        }
    }
}
