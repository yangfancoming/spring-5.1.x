package com.goat.chapter105.item07.buzz05;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan("com.goat.chapter105.item07.buzz05")
public class MyConfig2 {

	@Bean
	public Boss3 boss3(Car car){
		Boss3 boss3 = new Boss3();
		boss3.setCar(car);
		return boss3;
	}
}
