package com.goat.chapter185.item03;

import org.springframework.beans.factory.BeanClassLoaderAware;

/**
 * Created by Administrator on 2020/4/21.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/21---13:38
 */
public class BeanClassLoaderAwareTest implements BeanClassLoaderAware {

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		System.out.println("setBeanClassLoader..." + classLoader);
	}
}
