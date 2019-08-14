package com.goat.chapter588.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Locale;

@Controller
public class SampleController {

	/**
	 *
	 *  测试url：  http://localhost:8588/acceptHeaderLocaleResolver
	 */
	@GetMapping(value = "/acceptHeaderLocaleResolver" , produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String test(HttpServletRequest request) {
		String clientLocale = "";
		Enumeration<Locale> enus =  request.getLocales();
		while (enus.hasMoreElements()){
			Locale locale = enus.nextElement();
			clientLocale += locale + ",";
		}
		RequestContext requestContext = new RequestContext(request);
		String value = requestContext.getMessage("message.locale");
		return "客户端支持的Locale有："+clientLocale+" </br>当前使用的Locale是：" + requestContext.getLocale() + " </br>使用的资源Locale文件是：" + value ;
	}
}
