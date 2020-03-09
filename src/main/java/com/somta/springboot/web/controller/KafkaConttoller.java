package com.somta.springboot.web.controller;

import com.somta.springboot.web.server.ProduerServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author DH
 * @date 2020-03-05 09:59
 */

@RestController
@RequestMapping("/kafka")
public class KafkaConttoller {

	private static final Logger logger = LogManager.getLogger(KafkaConttoller.class);

	@Autowired
	private ProduerServer produerServer;

	@ResponseBody
	@GetMapping("test1")
	public String test(@RequestParam("data") String data) {
		String send = produerServer.send(data);
		return send;
	}
}
