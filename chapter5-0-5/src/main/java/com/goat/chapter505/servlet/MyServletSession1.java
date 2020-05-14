package com.goat.chapter505.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 测试 url ：  http://localhost:8505/Session_1
 *
 *  创建:第一次调用request.getSession方法时创建代表当前会话的session对象
 *      销毁:超过30分钟没人用销毁/调用invalidate方法自杀/
 *      服务器非正常关闭时随着web应用的销毁而销毁,如果服务器是正常关闭会被钝化起来（）
 *      当服务器正常关闭时,还存活着的session会随着服务器的关闭被以文件的形式存储在tomcat的work目录下,这个过程叫做session的钝化
 *      当服务器再次正常开启时,服务器会找到之前的SESSIONS.ser文件从中恢复之前保存起来的session对象这个过程叫做session的活化
 *      想要随着Session被钝化活化的对象它的类必须实现Serializable接口
 *
 *      httpSession驻留在服务器的内存中  为每个浏览器独享！ (也就是说httpSession是与浏览器相对应的！)
 *
 *  用途：
 *  1.网上商城的购物车系统
 *  2.保存登录用户的信息
 *  3.将某些数据放入的session中 供同一个用户在各个页面使用
 *  4.防止用户非法登录
 *
 *  注意： 测试方法 不能使用 rest工具，一定要使用浏览器进行测试
 */
public class MyServletSession1 extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
		HttpSession httpSession = req.getSession();//如果当前会话已经有了session对象那么直接返回，如果当前会话还不存在会话，那么创建session并返回；
		httpSession.setAttribute("name","山羊");
	}

}