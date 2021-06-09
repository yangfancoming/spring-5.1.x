package com.goat.chapter510.init;

import com.goat.chapter510.config.WebConfig;
import org.springframework.web.SpringServletContainerInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * Created by Administrator on 2021/6/9.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/9---16:02
 * Spring-web模块下的路径下有这么个文件 META-INF/services/javax.servlet.ServletContainerInitializer
 * 文件内容 org.springframework.web.SpringServletContainerInitializer 表示当tomcat容器启动的时候会去调用
 * 因此tomcat容器会调用一下代码，这是servlet 3.0 以后规定的一个新规范！
 * @see SpringServletContainerInitializer#onStartup(java.util.Set, javax.servlet.ServletContext)
 */
public class MyWebApplicationInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) {
		// Load Spring web application configuration  使用java注解的方式 启动web应用上下文。
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(WebConfig.class);
		// Create and register the DispatcherServlet  容器启动后  创建和注册 DispatcherServlet
		DispatcherServlet servlet = new DispatcherServlet(context);
		ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
		registration.setLoadOnStartup(1);
//		registration.addMapping("/*"); // doit  为啥用这个 就提示找不页面映射？
		registration.addMapping("/");
	}
}