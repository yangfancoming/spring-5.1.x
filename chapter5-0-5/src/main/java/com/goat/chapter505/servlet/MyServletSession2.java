package com.goat.chapter505.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * http://localhost:8505/Session_2
*/
public class MyServletSession2 extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		HttpSession httpSession = req.getSession();//如果当前会话已经有了session对象那么直接返回，如果当前会话还不存在会话，那么创建session并返回；
		String temp =  (String) httpSession.getAttribute("name");
		resp.getWriter().println(temp);// Session_1 中进行了设置属性到session   可以在Session_2中获取到  但是换一个浏览器就获取不到！
		httpSession.removeAttribute("name"); //网页刷新 则会显示  null
		String sessionid = httpSession.getId();  //获取 sessionId
		resp.getWriter().println(sessionid);
		httpSession.invalidate();// 手动强制session 失效！ 该方法通常用在 用户安全退出函数中
	}
}