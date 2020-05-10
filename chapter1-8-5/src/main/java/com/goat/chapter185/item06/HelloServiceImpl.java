package com.goat.chapter185.item06;

import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2020/5/9.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/9---17:49
 */

@Service
public class HelloServiceImpl implements HelloService {
	@Override
	public void say() {
		System.out.println("gaga");
	}
}
