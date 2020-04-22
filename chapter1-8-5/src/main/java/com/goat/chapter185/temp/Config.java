package com.goat.chapter185.temp;

import com.goat.chapter185.common.Dog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Administrator on 2020/4/6.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/6---18:45
 */
@ComponentScan("com.goat.chapter185.temp")
public class Config {

	@Bean
	public Dog dog(){
		return new Dog();
	}
}
