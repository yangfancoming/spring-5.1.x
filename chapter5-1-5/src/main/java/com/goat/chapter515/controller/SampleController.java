package com.goat.chapter515.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {

	/**
	 *  测试 controller 映射
	 *  测试url：  http://localhost:8515/home1
	*/
	@RequestMapping("/home1")
	public String home1(Model m) {
		m.addAttribute("name", "home1");
		return "home";
	}

	/**
	 *  测试视图解析器
	 *  测试url：  http://localhost:8515/home2
	 */
	@RequestMapping("/home2")
	@ResponseBody
	public String home2(Model m) {
		m.addAttribute("name", "home2");
		return "home";
	}
}
