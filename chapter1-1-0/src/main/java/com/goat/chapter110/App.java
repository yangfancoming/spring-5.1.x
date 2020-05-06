package com.goat.chapter110;

import com.goat.chapter110.scan.User;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2020/5/6.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/6---20:59
 */
public class App {

	@Test
	public void test1(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext("com.goat.chapter110.scan");
//		User user = ctx.getBean(User.class);
//		User user = (User) ctx.getBean("user");
//		user.say("123");
	}
}
