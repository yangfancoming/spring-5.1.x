package com.goat.chapter201.cyclicdependency.item02;

import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2020/4/3.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/3---15:15
 */
@Service
public class B {
	public B(A a) {
	}
}