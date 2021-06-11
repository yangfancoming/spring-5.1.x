package com.goat.chapter515.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2021/6/10.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/10---20:27
 */
public class MyInterceptor2 implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)  {
		System.out.println("MyInterceptor2 ----------  preHandle");
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)  {
		System.out.println("MyInterceptor2 ----------  postHandle");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)  {
		System.out.println("MyInterceptor2 ----------  afterCompletion");
	}
}
