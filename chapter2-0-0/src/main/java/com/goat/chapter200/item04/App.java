package com.goat.chapter200.item04;


import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2021/6/16.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/16---16:17
 */
public class App {

	/**
	 * AUTOWIRE_BY_NAME
	 * @see AutowireCapableBeanFactory#resolveDependency(org.springframework.beans.factory.config.DependencyDescriptor, java.lang.String, java.util.Set, org.springframework.beans.TypeConverter)
	 * @see DefaultListableBeanFactory#doResolveDependency(org.springframework.beans.factory.config.DependencyDescriptor, java.lang.String, java.util.Set, org.springframework.beans.TypeConverter)
	 * @see DefaultListableBeanFactory#findAutowireCandidates(java.lang.String, java.lang.Class, org.springframework.beans.factory.config.DependencyDescriptor)
	*/
	@Test
	public void test(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
		GoatPlayer goatPlayer = ac.getBean(GoatPlayer.class);
		goatPlayer.insert();
	}
}
