package com.goat.chapter505.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by 64274 on 2019/8/19.
 * @ Description:  再ServletContextListener中给 ServletContext set 属性  然后再Servlet中可以get到
 * @ author  山羊来了
 * @ date 2019/8/19---16:36
 *
 *  ServletContextListener 用来监听ServletContext对象创建和销毁的监听器
 *  创建:服务器启动,web应用加载后立即创建代表当前web应用的ServletContext对象
 *  销毁:服务器关闭或web应用被移除出容器时,随着web应用的销毁而销毁
 */
public class MyServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("调用 ServletContextListener 初始化方法 contextInitialized()...");
		ServletContext servletContext = sce.getServletContext();
		servletContext.setAttribute("name","Jacks");
		servletContext.setAttribute("age","23");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("调用 ServletContextListener 销毁 destroyed()...");
	}
}