package com.goat.chapter501;


import com.goat.chapter501.config.AppConfig;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

/**
 * Created by Administrator on 2021/6/9.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/9---16:11
 */
public class App {


	public static void main(String[] args) throws LifecycleException {
//		Tomcat tomcat = new Tomcat();
//		Server server = tomcat.getServer();
//		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//		connector.setPort(8080);
//		connector.setThrowOnFailure(true);
//		tomcat.getService().addConnector(connector);
//		tomcat.setConnector(connector);
//		tomcat.start();
//		server.await();


		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(AppConfig.class);
		context.refresh();
		File base = new File(System.getProperty("java.io.tmpdir"));
		Tomcat tomcat = new Tomcat();
		/**
		 * web项目
		 * contextPath tomcat的访问路径
		 * 项目的web目录
		 *
		 * 会调用相关的初始化实现类
		 */
		//    Context context1 = tomcat.addWebapp("/", base.getAbsolutePath());

		DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
		Context context1 = tomcat.addContext("/", base.getAbsolutePath());
		//tomcat启动的过程中会调用DispatcherServlet中的init方法
		Tomcat.addServlet(context1,"my",dispatcherServlet).setLoadOnStartup(1);
		//spring web环境
		context1.addServletMappingDecoded("/","my");


		//tomcat9
		Server server = tomcat.getServer();
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setPort(8080);
		connector.setThrowOnFailure(true);
		tomcat.getService().addConnector(connector);
		tomcat.setConnector(connector);
		tomcat.start();
		server.await();
	}


}
