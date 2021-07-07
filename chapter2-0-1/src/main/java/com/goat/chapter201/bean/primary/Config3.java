package com.goat.chapter201.bean.primary;

import com.goat.chapter201.model.A;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created by Administrator on 2021/7/7.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/7/7---9:52
 */
@Configuration
public class Config3 {

	@Bean
	@Primary
	public A a1(){
		return new A();
	}

	@Bean
	@Primary
	public A a2(){
		return new A();
	}
}
