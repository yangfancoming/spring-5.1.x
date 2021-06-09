package com.goat.chapter510.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2021/6/9.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/9---20:29
 */
@RestController
public class RequestMappingHandlerController {

	/** 测试url：  http://localhost:8510/req */
	@RequestMapping("/req")
	public String test1() {
		return "RequestMappingHandlerController";
	}

}
