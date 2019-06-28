package com.goat.spring.demo.service;

import org.springframework.stereotype.Component;

/**
 * Created by 64274 on 2019/6/27.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/27---22:07
 */
@Component
public class TestService {

	public TestService() {
		System.out.println(".......TestService........");
	}

	public void test(){
		System.out.println("wahaha");
	}
}
