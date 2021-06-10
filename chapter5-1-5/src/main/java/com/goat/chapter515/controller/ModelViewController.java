package com.goat.chapter515.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
public class ModelViewController {

	/**
	 *  测试url：  http://localhost:8515/view/1
	*/
	@RequestMapping("/1")
	public String test1(Model model) {
		model.addAttribute("1",1);
		return "home";
	}

	/**
	 *  测试url：  http://localhost:8515/view/2
	 */
	@RequestMapping("/2")
	public String test2(Model model) {
		model.addAttribute("2",2);
		return "home";
	}
}
