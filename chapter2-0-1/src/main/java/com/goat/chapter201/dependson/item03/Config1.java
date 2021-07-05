package com.goat.chapter201.dependson.item03;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Created by Administrator on 2021/7/5.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/7/5---17:36
 */
@Configuration
public class Config1 {

	@Bean
	@DependsOn("b")
	public void a(){
		new A();
	}

	@Bean
	@DependsOn("a")
	public void b(){
		new B();
	}

	public class A {

	}

	public class B {

	}
}
