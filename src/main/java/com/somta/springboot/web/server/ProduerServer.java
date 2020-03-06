package com.somta.springboot.web.server;

import com.somta.springboot.web.constants.Constants;
import com.somta.springboot.web.util.kafka.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author DH
 * @date 2020-03-05 09:27
 */
@Service
public class ProduerServer {

	@Autowired
	private Producer producer;

	public String send(String data){
		producer.send(Constants.KAFKA_TOPIC,"2",data);
		return Constants.SUCCESS;
	}

}
