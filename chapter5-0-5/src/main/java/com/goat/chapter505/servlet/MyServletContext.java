package com.goat.chapter505.servlet;


import org.junit.Assert;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2020/5/14.
 * @ Description: 测试  ServletContext 的获取方式
 * @ author  山羊来了
 * @ date 2020/5/14---16:53
 */
public class MyServletContext extends HttpServlet {

	@Override
	public void init(){
		// 1.在 HttpServlet 中直接获取  this.getServletContext()  (HttpServlet extends GenericServlet)
		ServletContext servletContext1 = getServletContext();
		// 2.通过 servletConfig 来获取 ServletContext servletContext = servletConfig.getServletContext();
		ServletContext servletContext = getServletConfig().getServletContext();
		Assert.assertEquals(servletContext,servletContext1);
		Assert.assertNotNull(servletContext);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		// 3.通过 httpServletRequest 来获取 httpServletRequest.getSession().getServletContext();
		ServletContext servletContext = req.getSession().getServletContext();
		Assert.assertNotNull(servletContext);
	}
}
