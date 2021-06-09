package com.goat.chapter510.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2021/6/9.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/9---20:29
 * 测试url：  http://localhost:8510/bean
 */
@Component("/bean")
public class BeanNameHandlerMappingController implements Controller {

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("进入 BeanNameHandlerMappingController 处理器。。。");
		return null;
	}
}
