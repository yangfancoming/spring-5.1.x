package com.goat.chapter201.bean.priority;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Priority;


/**
 * Created by Administrator on 2021/7/7.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/7/7---9:52
 */
@Configuration
public class Config3 {

	@Bean
	public UserService a1(){
		return new UserServiceImpl1();
	}

	@Bean
	public UserService a2(){
		return new UserServiceImpl2();
	}

	@Priority(23)
	public class UserServiceImpl1 implements UserService {

	}

	@Priority(23)
	public class UserServiceImpl2 implements UserService {

	}
}
