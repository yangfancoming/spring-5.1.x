package com.goat.chapter521.controller;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
public class DemoController implements Controller {

	// 测试url：    http://localhost:8521/demo
	@Nullable
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)  {
		System.out.println("进入 DemoController（Handler）处理器。。。");
		return null;
	}
}