package org.springframework.beans.factory.test;

import org.springframework.beans.factory.FactoryBean;

/**
 * Created by Administrator on 2020/3/25.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/25---21:34
 */
public class NullReturningFactoryBean implements FactoryBean<Object> {

	@Override
	public Object getObject() {
		System.out.println("NullReturningFactoryBean....getObject()");
		return null;
	}

	@Override
	public Class<?> getObjectType() {
		System.out.println("NullReturningFactoryBean....getObjectType()");
		return null;
	}

	@Override
	public boolean isSingleton() {
		System.out.println("NullReturningFactoryBean....isSingleton()");
		return true;
	}
}