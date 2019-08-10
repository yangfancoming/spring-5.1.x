package com.goat.chapter115.service;

/**
 * Created by 64274 on 2019/6/28.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/28---10:39
 */
public class WhatServiceImpl implements WhatService {

	
	public WhatServiceImpl() {
		System.out.println("WhatServiceImpl 构造函数执行");
	}

	public String getMessage() {
		return "正常实现类bean";
	}

}
