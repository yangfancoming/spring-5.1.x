package com.goat.chapter510.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Test1Controller {

	/** 测试url：  http://localhost:8510/test1 */
	@RequestMapping("/test1")
	public String test1(Model m) {
		m.addAttribute("name", "CodeTutr");
		System.out.println(1);
		return "home";
	}
}
