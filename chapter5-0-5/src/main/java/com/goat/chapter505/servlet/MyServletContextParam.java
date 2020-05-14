package com.goat.chapter505.servlet;


import org.junit.Assert;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import java.util.Enumeration;

/**
 * Created by Administrator on 2020/5/14.
 * @ Description: 测试  web.xml 中 全局参数 <context-param> 和 局部参数 <init-param> 的获取
 * @ author  山羊来了
 * @ date 2020/5/14---16:53
 */
public class MyServletContextParam extends HttpServlet {

	@Override
	public void init(){
		// 获取本servlet名称  对应 <servlet-name> 标签
		ServletConfig servletConfig = getServletConfig();
		Assert.assertEquals("myServletContextParam",servletConfig.getServletName());

		// 获取单个初始化参数  对应 <init-param> 标签
		Assert.assertEquals("GBK",servletConfig.getInitParameter("encoding"));

		// 获取 所有 <init-param> 标签
		Enumeration<String> initParameterNames = servletConfig.getInitParameterNames();
		while(initParameterNames.hasMoreElements()){
			String name = initParameterNames.nextElement();
			String value = servletConfig.getInitParameter(name); // 再通过key属性 获取对应的value值
			System.out.println("name：" + name + "value：" + value);
		}

		//获取 ServletContext 对象
		ServletContext servletContext = servletConfig.getServletContext();

		// 对应 MyServletContextListener 中set进去的值
		Enumeration<String> attributeNames = servletContext.getAttributeNames();
		while(attributeNames.hasMoreElements()){
			System.out.println(attributeNames.nextElement());
		}

		// 对应全局 <context-param>  参数
		Enumeration<String> initParameterNames1 = servletContext.getInitParameterNames();
		while(initParameterNames1.hasMoreElements()){
			System.out.println(initParameterNames1.nextElement());
		}

	}

}
