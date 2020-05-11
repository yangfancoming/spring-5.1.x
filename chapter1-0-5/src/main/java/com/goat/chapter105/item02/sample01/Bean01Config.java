package com.goat.chapter105.item02.sample01;

import com.goat.chapter105.model.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2020/3/30.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/30---15:44
 */
@Configuration
public class Bean01Config {

	@Bean
	public Person person(){
		System.out.println("person 默认单例模式  容器启动时直接创建");
		return new Person("app01", 110);
	}
}
