package com.goat.chapter651;


import com.goat.chapter651.config.AppConfig;
import com.goat.chapter651.service.BookService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;

public class App {

	// 容器初始化 不涉及连接数据库查询
	@Test
	public void test0(){
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
		System.out.println(ac);
	}

	// 容器初始化 并执行mybatis查询
	@Test
	public void test(){
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
		BookService bookService = ac.getBean(BookService.class);
		List<Map> test = bookService.test();
		Assert.assertEquals(10,test.size());
	}
}
