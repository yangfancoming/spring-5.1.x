package com.goat.chapter105.item02;

import com.goat.chapter105.model.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: 注解方式 获取bean
 * @ author  山羊来了
 * @ date 2020/3/17---13:48
 */
@Configuration // 该注解就相当于传统的xml文件
public class PersonConfig {

	/**
	 * 对比传统xml方式
	 * <bean id="person" class="com.goat.chapter105.model.Person"  scope="prototype">
	 *  默认将方法名作为 id  或  @Bean("xxxx")  后者优先级高
	*/
	@Bean
	public Person person333(){
		System.out.println("person333 默认单例模式  容器启动时直接创建");
		Person person = new Person("goat", 19);
		return person;
	}

	// 默认单例，除非显示指定为 prototype 原型模式 (懒加载)
	@Scope("prototype")
	@Bean
	public Person person111(){
		System.out.println("person111 原型模式  容器启动不会创建，getBean时才会创建");
		Person person = new Person("goat111", 1111);
		return person;
	}

	@Bean
	@Lazy
	public Person person222(){
		System.out.println("person222 默认单例模式  容器启动不会创建，getBean时才会创建");
		Person person = new Person("goat222", 2222);
		return person;
	}
}
