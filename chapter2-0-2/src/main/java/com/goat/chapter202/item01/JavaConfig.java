package com.goat.chapter202.item01;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 64274 on 2019/8/12.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/12---20:46
 */
@Configuration // 这个注解 为什么不加 可以？
public class JavaConfig {

	@Bean
	public Person foo(){
		Person person = new Person();
		person.setAge(23);
		person.setName("jordan");
		return person;
	}

}
