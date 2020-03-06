package com.somta.springboot.web.util.kafka;

import com.somta.springboot.web.constants.Constants;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * @author DH
 * @date 2020-03-05 09:31
 */
@Component
@PropertySource({"classpath:properties/kafka.properties"})
public class Producer {

	private static final Logger logger = LogManager.getLogger(Producer.class);

	private KafkaProducer<String, String> producer;
	@Value("${kafkaHosts}")
	private String servers;

	@PostConstruct
	public void init() {
		Properties props = new Properties();
		props.put("bootstrap.servers", servers);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("key.serializer", StringSerializer.class.getName());
		props.put("value.serializer", StringSerializer.class.getName());
		this.producer = new KafkaProducer<String, String>(props);
	}

	public void add(String msg) {
		add(null, msg);
	}

	public void add(String key, String msg) {
		producer.send(new ProducerRecord<>(Constants.KAFKA_TOPIC, key, msg), new MyProduerCallback(msg));
	}

	public void send(String topic, String key, String msg) {
		producer.send(new ProducerRecord<>(topic, key, msg), new MyProduerCallback(msg));
	}

	public class MyProduerCallback implements Callback {
		private String msg;

		public MyProduerCallback() {
		}

		public MyProduerCallback(String msg) {
			this.msg = msg;
		}

		@Override
		public void onCompletion(RecordMetadata recordMetadata, Exception exception) {
			if (exception != null) {
				logger.warn("输入kafka数据错误,msg:{},", msg, exception);
			}

		}
	}

}
