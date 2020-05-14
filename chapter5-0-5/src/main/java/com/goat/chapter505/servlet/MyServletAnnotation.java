package com.goat.chapter505.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by 64274 on 2019/8/19.
 * @ Description:  注解方式 使用 Servlet  此种方式 不用在web.xml 中再配置 <servlet> 标签了
 * @ author  山羊来了
 * @ date 2019/8/19---16:30
 */
@WebServlet(name = "AnnotationTest",urlPatterns={"/shit"})  //name(可以随意写)对应web.xml<servlet-name>  urlPatterns对应<url-pattern>
public class MyServletAnnotation extends HttpServlet {


	@Override
	public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException {
		String nicename = servletRequest.getParameter("nickname");//获取前台表单信息  通过key属性 获取对应的value值
		System.out.println(nicename);

		String[] interesting = servletRequest.getParameterValues("interesting"); // 获取前台表单中 多选框的信息  通过key属性 获取对应的多个value值
		for(String interest:interesting){
			System.out.println(interest);
		}

		Enumeration<String> temps = servletRequest.getParameterNames(); //0 = "nickname"  1 = "age" 2 = "interesting"
		while(temps.hasMoreElements()){
			String name = temps.nextElement();
			System.out.println("-->" + name);
		}

		Map<String,String[]> map =  servletRequest.getParameterMap();
		for(Map.Entry<String,String[]> entry:map.entrySet()){
			System.out.println(entry.getKey() + ":" + Arrays.asList(entry.getValue()));
		}

		HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
		String queryString =  httpServletRequest.getQueryString();
		System.out.println("-->" + httpServletRequest.getRequestURI());//   /servlet/shit
		System.out.println("-->" + httpServletRequest.getRequestURL()); //  http://localhost:8080/servlet/shit
		System.out.println("-->" + httpServletRequest.getMethod()); //  POST / GET
		System.out.println("-->" + queryString); //为GET时 才有  nickname=111&age=222&interesting=reading&interesting=game&interesting=party&interesting=shopping&interesting=sport

		System.out.println(httpServletRequest.getContextPath());//获取上下文路径  ==  /servlet
		System.out.println(httpServletRequest.getServletPath());//获取请求路径  ==  /shit
		System.out.println(httpServletRequest.getSession().getServletContext().getRealPath("/"));//获取绝对路径 == E:\J2EE\IDEA2016\servlet\out\artifacts\servlet_war_exploded\

		// servletResponse  响应信息
		servletResponse.setContentType( "text/html;charset=utf-8"); //这里如果不加 charset=utf-8 那么返回到前台jsp的中文则显示乱码
		PrintWriter pw = servletResponse.getWriter();
		pw.println("响应信息--------------"); //将该字符串 直接打印到浏览器上

		HttpServletResponse httpServletResponse = (HttpServletResponse)servletResponse;
		httpServletResponse.sendRedirect("");
	}
}