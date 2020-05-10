package com.goat.chapter185.item07.sample1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2020/5/10.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/10---20:40
 */
@Component
public class A {

	@Autowired
	private B b;

	public B getB() {
		return b;
	}
}


