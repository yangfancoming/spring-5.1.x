package com.goat.chapter505;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by 64274 on 2019/8/19.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/19---16:36
 */
public class MyServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("MyServletContextListener init...");
		ServletContext servletContext = sce.getServletContext();
		servletContext.setAttribute("name","Jacks");
		servletContext.setAttribute("age","23");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("MyServletContextListener destroyed...");
	}
}