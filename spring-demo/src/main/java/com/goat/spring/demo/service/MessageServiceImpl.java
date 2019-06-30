package com.goat.spring.demo.service;

import org.springframework.stereotype.Service;

/**
 * Created by 64274 on 2019/6/28.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/28---10:39
 */
@Service
public class MessageServiceImpl implements MessageService {

	public String getMessage() {
		return "hello world";
	}
}
