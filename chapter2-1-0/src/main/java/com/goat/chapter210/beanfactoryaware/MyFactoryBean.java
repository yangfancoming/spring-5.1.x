package com.goat.chapter210.beanfactoryaware;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * Created by 64274 on 2019/8/11.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/11---16:22
 */
@Component
public class MyFactoryBean implements FactoryBean<TestBean> {

	@Override
	public TestBean getObject() throws Exception  {
		return new TestBean();
	}

	@Override
	public Class<?> getObjectType() {
		return TestBean.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
