package com.goat.spring.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 64274 on 2019/7/22.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/7/22---14:13
 */
@Service
public class TransactionService {

	@Transactional
	public String test1(String str){
		System.out.println("进入 TransactionService 方法--- "+str);
		return str;
	}
}
