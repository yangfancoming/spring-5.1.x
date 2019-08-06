package com.goat.chapter100;

import com.goat.chapter100.knight.Knight;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Mytest1 {

	ApplicationContext context = new ClassPathXmlApplicationContext("classpath:knight.xml");


	@Test
	public void test() {
		Knight knight2 = (Knight)context.getBean("knight2");
		knight2.embarkOnQuest();
	}

}
