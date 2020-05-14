package com.goat.chapter505.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * Created by Administrator on 2020/5/14.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/14---14:53
 *
 *  ServletRequestListener  用来监听ServletRequest对象创建和销毁的监听，浏览器的每次请求都会触发 初始化和销毁方法
 *  创建:请求开始创建代表请求的request对象
 *  销毁:请求结束时代表请求的request对象销毁
 */
public class MyServletRequestListener implements ServletRequestListener {

	@Override
	public void requestInitialized(ServletRequestEvent servletRequestEvent){
		System.out.println("调用 ServletRequestListener 初始化方法 requestInitialized()...");
	}

	@Override
	public void requestDestroyed(ServletRequestEvent servletRequestEvent){
		System.out.println("调用 ServletRequestListener 销毁方法 requestDestroyed()...");
	}
}
