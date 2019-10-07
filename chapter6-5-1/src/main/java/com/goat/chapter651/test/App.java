package com.goat.chapter651.test;


import com.goat.chapter651.config.AppConfig;
import com.goat.chapter651.service.BookService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;

public class App {


	public static void main(String[] args) {

		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
		BookService bookService = ac.getBean(BookService.class);
		List<Map> test = bookService.test();
		System.out.println(test);
	}
}
