package com.somta.springboot.web.server;

import com.somta.springboot.web.constants.Constants;
import com.somta.springboot.web.util.kafka.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;

/**
 * @author DH
 * @date 2020-03-05 09:27
 */
@Service
public class ProduerServer {

	@Autowired
	private Producer producer;

	public String send(String data){
		for (int i = 0; i < 100000; i++) {
			producer.send(Constants.KAFKA_TOPIC, i + "", data);
		}
		return Constants.SUCCESS;
	}

}
