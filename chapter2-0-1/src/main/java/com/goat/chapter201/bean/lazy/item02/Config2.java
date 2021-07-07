package com.goat.chapter201.bean.lazy.item02;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Created by Administrator on 2021/7/7.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/7/7---9:52
 */
@Configuration
public class Config2 {

	@Bean
	public A a(){
		return new A();
	}

	@Bean
	@Lazy
	public B b(){
		return new B();
	}
}
