package com.goat.chapter651;


import com.goat.chapter651.config.AppConfig;
import com.goat.chapter651.service.BookService;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;

public class App {

	@Test
	public void test(){
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
		BookService bookService = ac.getBean(BookService.class);
		List<Map> test = bookService.test();
		System.out.println(test);
	}
}
