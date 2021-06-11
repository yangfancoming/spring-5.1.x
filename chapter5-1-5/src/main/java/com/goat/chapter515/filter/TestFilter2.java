package com.goat.chapter515.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2021/6/11.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/11---9:03
 */
public class TestFilter2 extends OncePerRequestFilter {

	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		System.out.println("############TestFilter2 doFilterInternal executed############");
		filterChain.doFilter(request, response);
		System.out.println("############TestFilter2 doFilter after############");
	}

}
