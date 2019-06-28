package com.goat.spring.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 64274 on 2019/6/28.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/28---18:06
 */
@RestController
@RequestMapping("/test")
public class TestController {

	// http://localhost:8080/test/test1
	@GetMapping("/test1")
	public void test(){
		System.out.println("test.......TestController");
	}
}
