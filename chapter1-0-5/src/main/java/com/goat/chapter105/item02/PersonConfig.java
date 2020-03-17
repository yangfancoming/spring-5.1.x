package com.goat.chapter105.item02;

import com.goat.chapter105.model.Person;
import org.springframework.context.annotation.*;

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
	public Person pers1on(){
		Person person = new Person("goat", 19);
		return person;
	}

}
