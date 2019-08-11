package com.goat.chapter210.initializingbean;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Created by 64274 on 2019/8/11.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/11---17:47
 */
@Component
public class MyInitializingBean implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out .println("【InitializingBean接口】调用InitializingBean.afterPropertiesSet()");
	}
}
