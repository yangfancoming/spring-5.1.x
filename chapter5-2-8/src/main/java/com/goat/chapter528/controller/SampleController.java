package com.goat.chapter528.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SampleController {

	/** 测试url：  http://localhost:8528/home */
	@RequestMapping("/home")
	public String loadHomePage(Model m) {
		m.addAttribute("name", "CodeTutr");
		return "home";
	}
}
