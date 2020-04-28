package com.goat.chapter185.item03;

import org.springframework.beans.factory.InitializingBean;

/**
 * Created by Administrator on 2020/4/21.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/21---13:50
 */
public class InitializingBeanTest implements InitializingBean {

	@Override
	public void afterPropertiesSet()  {
		System.out.println("afterPropertiesSet...");
	}
}
