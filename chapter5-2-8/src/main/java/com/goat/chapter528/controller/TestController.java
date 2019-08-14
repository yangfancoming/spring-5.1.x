package com.goat.chapter528.controller;

import com.goat.chapter528.bean.Employee;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by 64274 on 2019/8/14.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/14---20:08
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {

	// 带有@RequestBody的自定义对象   http://localhost:8528/test/testRb?name=1&age=3
	@RequestMapping("/testRb")
	public Employee testRb(@RequestBody Employee e) {
		return e;
	}

	// 自定义对象 http://localhost:8528/test/testCustomObj?name=1&age=3
	@RequestMapping("/testCustomObj")
	public Employee testCustomObj(Employee e) {
		return e;
	}

	// 带有@RequestParam的自定义对象
	@RequestMapping("/testCustomObjWithRp")
	public Employee testCustomObjWithRp(@RequestParam Employee e) {
		return e;
	}

	// 日期对象
	@RequestMapping("/testDate")
	public Date testDate(Date date) {
		return date;
	}

}