package com.goat.chapter201.dependson.item03;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Created by Administrator on 2021/7/5.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/7/5---20:41
 */
@Configuration
public class Config3 {

	@Bean
	@DependsOn(value = {"b","c"})
	public void a(){
		new A();
	}

	@Bean
	public void b(){
		new B();
	}

	@Bean
	public void c(){
		new C();
	}

	public class A {

	}

	public class B {

	}

	public class C {

	}
}
