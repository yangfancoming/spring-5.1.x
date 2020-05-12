package com.goat.chapter500;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2020/5/12.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/12---9:57
 */
@WebServlet(urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().write("hello servlet...");
	}
}
