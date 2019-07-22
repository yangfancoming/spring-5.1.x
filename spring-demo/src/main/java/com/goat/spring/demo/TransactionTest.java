package com.goat.spring.demo;

import com.goat.spring.demo.config.AppConfig;
import com.goat.spring.demo.service.TransactionService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by 64274 on 2019/7/22.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/7/22---14:16
 */
public class TransactionTest {


	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
		TransactionService bean = ctx.getBean(TransactionService.class);
		System.out.println(bean.test1("hoho"));

	}
}
