package com.goat.chapter534;

import org.springframework.web.bind.annotation.*;

/**
 * Created by Administrator on 2020/1/10.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/1/10---15:44
 */

@RestController
@RequestMapping(value = "/test")
public class ConverterDemoController {

	// 自定义对象 http://localhost:8534/test/test1
	@GetMapping("/test1")
	public String postTelephone() {
		return "123";
	}
}
