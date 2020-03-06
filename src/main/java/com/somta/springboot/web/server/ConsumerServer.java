package com.somta.springboot.web.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.annotation.PostConstruct;

import com.somta.springboot.web.util.kafka.ConsumerPrototype;
import com.somta.springboot.web.util.kafka.ExecutorUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONObject;

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
					List<String> messages = consumer.poll();
					for (String message : messages) {
						LOGGER.info("接收到数据:" + message);
						JSONObject json = JSONObject.parseObject(message);//直接用fastjson转。比较麻烦
						String id = json.getString("id");

						handler();
					}
				}
			} catch (Exception e) {
				LOGGER.error("拉取脚本下发任务异常,睡眠5s后重试", e);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					LOGGER.warn("", e1);
				}
			}
		}

		private void handler() {
			executorService.submit(new Runnable() {

				@Override
				public void run() {
					String type = null;
					ActionJob actionJob = null;
					if (actionJob == null) {
						LOGGER.warn("未找到脚本下发任务id:{}");
						return;
					}
					String message = null;
					try {
						//里面的message涉及到国际化
						// SecurityContextHolder.setLanguage(tenantLogic.getTenantLanguage(actionJob.getTenantId()));
						//校验下发任务

						if (StringUtils.isBlank(message)) {
							switch (type) {
							case "1": {
								message = start(actionJob);
								break;
							}
							case "2": {
								//如果主机没权限,则taskHost表里没数据,这时候重新执行则需要重新解析执行目标(相对于开始)

								break;
							}
							default:
								break;
							}
						}
					} catch (Exception e) {
						StringWriter stringWriter = new StringWriter();
						e.printStackTrace(new PrintWriter(stringWriter));
						message = stringWriter.toString();
						IOUtils.closeQuietly(stringWriter);
					} finally {

						if (message != null) {
							LOGGER.error(message);
							try {
								//								actionJobLogAnalysisLogic.saveLog(id, System.currentTimeMillis(), message);
							} catch (Throwable e) {
								LOGGER.error("", e);
							}
							try {
								//								actionJobLogic.updateActionJobStatusById(id, actionJob.getStatus(), JobConstants.JOB_STATUS_FAIL, null, new Date());
							} catch (Throwable e) {
								LOGGER.error("", e);
							}
						}
					}
				}
			});
		}

		private String start(ActionJob actionJob) {

			return null;
		}

		private String retry(ActionJob actionJob) {
			return null;
		}

		private class ActionJob {
		}
	}
}
