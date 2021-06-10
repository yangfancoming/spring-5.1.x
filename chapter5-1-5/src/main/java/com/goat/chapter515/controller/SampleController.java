package com.goat.chapter515.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sample")
public class SampleController {

	/**
	 *  测试 controller 映射
	 *  测试url：  http://localhost:8515/sample/1
	*/
	@RequestMapping("/1")
	public String test1() {
		return "1";
	}

	/**
	 *  测试视图解析器
	 *  测试url：  http://localhost:8515/sample/2
	 */
	@RequestMapping("/2")
	public String test2() {
		return "2";
	}
}
