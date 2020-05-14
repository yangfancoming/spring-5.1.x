package com.goat.chapter505.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by Administrator on 2020/5/14.
 *
 * @ Description: HttpSessionListener 用来监听HttpSession对象创建和销毁的监听器，浏览器的每次请求都会触发 初始化和销毁方法
 * @ author  山羊来了
 * @ date 2020/5/14---14:52
 */
public class MyHttpSessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent httpSessionEvent){
		System.out.println("调用 HttpSessionListener 初始化方法 sessionCreated()...");
		System.out.println("sessionCreated---------------------" + httpSessionEvent.getSession());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent httpSessionEvent){
		System.out.println("调用 HttpSessionListener 销毁方法 sessionDestroyed()...");
	}
}
