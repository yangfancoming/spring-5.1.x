package com.goat.chapter201.lifecycle.item02;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Administrator on 2021/6/11.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/11---19:51
 */
@ComponentScan("com.goat.chapter201.lifecycle.item02")
public class Config {

	@Bean(value = "normal baby",initMethod = "init1", destroyMethod = "destroy1")
	public Baby baby(){
		return new Baby();
	}

	@Bean
	public Daddy daddy(){
		return new Daddy();
	}
}
