package com.goat.chapter201.lifecycle.item01;

import org.springframework.beans.factory.BeanNameAware;

/**
 * Created by Administrator on 2020/4/21.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/21---13:25
 */
public class BeanNameAwareTest implements BeanNameAware {

	@Override
	public void setBeanName(String name) {
		System.out.println("setBeanName...." + name);
	}
}
