package com.somta.springboot.web.server;

import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.annotation.PostConstruct;

import com.somta.springboot.web.util.kafka.ConsumerPrototype;
import com.somta.springboot.web.util.kafka.ExecutorUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConsumerServer {

	private static Logger LOGGER = LogManager.getLogger(ConsumerServer.class);
	private ExecutorService executorService;

	@Value("#{consumerPrototype.init(T(com.somta.springboot.web.constants.Constants).KAFKA_TOPIC,T(com.somta.springboot.web.constants.Constants).KAFKA_GROUPID)}")
	@Autowired
	private ConsumerPrototype consumer;

	@PostConstruct
	public void init() {
		//数据拉取一个线程,其余都是处理数据的线程
		executorService = ExecutorUtil.newBlockThreadPool(10, "script-push—worker");
		executorService.submit(new PullJob());
	}

	public class PullJob implements Runnable {

		@Override
		public void run() {
			try {
				while (true) {
					System.out.println("-");
					List<String> messages = consumer.poll();
					for (String message : messages) {
						System.out.println("收到消息====" + message);
						if (message.equals("222")) {
							int i = 1 / 0;
						}
						handler(message);
					}
				}
			} catch (Exception e) {
				System.out.println("脚本下发任务异常,睡眠5s后重试");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					LOGGER.warn("", e1);
				}
			}
		}

		private void handler(String message) {
			executorService.submit(new Runnable() {

				@Override
				public void run() {
					try {
						System.out.println("处理消息=====" + message);
						if (message.equals("2")) {
							int i = 1 / 0;
						}
					} catch (Exception e) {
						System.out.println("handler异常");
					}
				}
			});
		}

	}
}
