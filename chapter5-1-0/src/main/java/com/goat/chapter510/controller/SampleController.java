package com.goat.chapter510.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SampleController {

	// http://localhost:8510/home
	@RequestMapping("/home")
	public String loadHomePage(Model m) {
		m.addAttribute("name", "CodeTutr");
		return "home";
	}
}
