package com.goat.chapter505;

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
public class TestServlet extends HttpServlet {

	private String message;

	@Override
	public void init(){
		// 执行必需的初始化
		message = "Hello World";
		System.out.println("==TestServlet init");
	}

	@Override
	public void destroy() {
		System.out.println("==TestServlet destroy");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
		// 注意这儿的代码，跟下面讲的ServletContextListener有关联，如要运行该段代码，请完整复制整篇内容
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
}