package com.goat.chapter105.item07.buzz03;

import com.goat.chapter105.item07.common.TestDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan("com.goat.chapter105.item07.common")
public class MyConfig {

	@Bean
	public TestDao testDao2(){
		TestDao testDao = new TestDao();
		testDao.setMark("2");
		return testDao;
	}

}
