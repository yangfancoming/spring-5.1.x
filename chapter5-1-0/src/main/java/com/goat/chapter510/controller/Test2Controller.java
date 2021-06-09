package com.goat.chapter510.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Test2Controller {

	/** 测试url：  http://localhost:8510/test2 */
	@RequestMapping("/test2")
	public String test1(Model m) {
		m.addAttribute("name", "CodeTutr");
		System.out.println(2);
		return "home";
	}
}
