package com.goat.chapter510.init;

import com.goat.chapter510.config.WebConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * Created by Administrator on 2021/6/9.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/9---16:02
 */
public class MyWebApplicationInitializer2 implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) {
		// Load Spring web application configuration
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(WebConfig.class);
		// Create and register the DispatcherServlet
		DispatcherServlet servlet = new DispatcherServlet(context);
		ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
		registration.setLoadOnStartup(1);
//		registration.addMapping("/*"); // doit  为啥用这个 就提示找不页面映射？
		registration.addMapping("/");
	}
}