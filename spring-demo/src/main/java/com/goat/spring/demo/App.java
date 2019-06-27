package com.goat.spring.demo;


import com.goat.spring.demo.config.AppConfig;
import com.goat.spring.demo.service.TestService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by 64274 on 2019/6/27.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/27---20:33
 */
public class App {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
		System.out.println(ctx.getBean(TestService.class));
	}
}
