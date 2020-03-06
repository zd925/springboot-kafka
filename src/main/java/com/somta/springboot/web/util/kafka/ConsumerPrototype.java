package com.somta.springboot.web.util.kafka;

import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 线程安全--但如果需要断开kafkak需要手动调用destroy方法
 *
 * @author DH
 * @date 2020-03-05 14:21
 */
@Component("consumerPrototype")
@Scope("prototype")
@PropertySource({"classpath:properties/kafka.properties"})
public class ConsumerPrototype {

	private KafkaConsumer<String, String> consumer;
	@Value("${kafkaHosts}")
	private String kafkaHosts;

	public ConsumerPrototype init(String topic, String groupId, String... propsContent) {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaHosts);
		props.put("group.id", groupId);
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("max.poll.records", 1000);
		props.put("auto.offset.reset", "earliest");
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", StringDeserializer.class.getName());
		for (int i = 0, size = propsContent.length - 1; i < size; i += 2) {
			String key = propsContent[i];
			String value = propsContent[i + 1];
			if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
				props.put(key, value);
			}
		}
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(topic));
		this.consumer = consumer;
		return this;
	}

	public List<String> poll() {
		while (true) {
			List<String> result = poll(1000);
			if (result != null) {
				return result;
			}
		}
	}

	/**
	 * @param timeout 单位 毫秒
	 * @return
	 */
	private List<String> poll(int timeout) {
		List<String> results = null;
		ConsumerRecords<String, String> records = consumer.poll(timeout);
		if (records.count() > 0) {
			results = new ArrayList<>();
			for (ConsumerRecord<String, String> record : records) {
				results.add(record.value());
			}
		}
		return results;
	}

	public void destroy() {
		IOUtils.closeQuietly(this.consumer);
	}

}
