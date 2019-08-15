package com.goat.chapter528.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 64274 on 2019/8/14.
 *
 * @ Description: 参数绑定 测试
 * @ author  山羊来了
 * @ date 2019/8/14---20:08
 */
@Controller
@RequestMapping("/ParameterBind")
public class ParameterBindController {

	/**
	 * 测试url：  http://localhost:8528/ParameterBind/test1?id=999
	 *
	 *
	*/
	@ResponseBody
	@RequestMapping("/test1")
	public String test1(int id){
		System.out.println(id);
		return "test1";
	}
}