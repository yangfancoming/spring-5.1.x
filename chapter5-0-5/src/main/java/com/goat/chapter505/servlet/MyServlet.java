package com.goat.chapter505.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * Created by 64274 on 2019/8/19.
 * @ Description: 测试 url ：  http://localhost:8505/myServlet?name=goat&age=23
 * @ author  山羊来了
 * @ date 2019/8/19---16:30
 */
public class MyServlet extends HttpServlet {

	private String message;

	@Override
	public void init(){
		message = "Hello World";
		System.out.println("==TestServlet init");
		ServletConfig servletConfig = getServletConfig();
		// 对应全局 <context-param>  参数
		Enumeration<String> initParameterNames = servletConfig.getServletContext().getInitParameterNames();
		while(initParameterNames.hasMoreElements()){
			System.out.println(initParameterNames.nextElement());
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
		ServletContext servletContext = getServletContext();
		String name = servletContext.getAttribute("name").toString();
		String age = servletContext.getAttribute("age").toString();
		// 设置响应内容类型
		resp.setContentType("text/html");
		// 实际的逻辑是在这里
		PrintWriter out = resp.getWriter();
		out.println("<h1>" + name + age + "</h1>");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println("<h1>" + message + "</h1>");
	}

	@Override
	public void destroy() {
		System.out.println("==TestServlet destroy");
	}
}