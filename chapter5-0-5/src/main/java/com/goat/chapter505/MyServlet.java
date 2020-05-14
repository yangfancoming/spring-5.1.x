package com.goat.chapter505;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by 64274 on 2019/8/19.
 *
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
		System.out.println(servletConfig);
		String servletInfo = getServletInfo();
		System.out.println(servletInfo);
		ServletContext servletContext = getServletContext();
		System.out.println(servletContext);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
		ServletContext servletContext = getServletContext();
		// doit 为啥 不加 MyServletContextListener 类 这里就会报错 空异常？
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
		// 设置响应内容类型
		resp.setContentType("text/html");
		// 实际的逻辑是在这里
		PrintWriter out = resp.getWriter();
		out.println("<h1>" + message + "</h1>");
	}

	@Override
	public void destroy() {
		System.out.println("==TestServlet destroy");
	}
}