package com.goat.chapter105.item07.buzz04;

import com.goat.chapter105.item07.common.TestDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
@ComponentScan("com.goat.chapter105.item07.common")
public class MyConfig {

	@Bean
//	@Primary
	public TestDao testDao2(){
		TestDao testDao = new TestDao();
		testDao.setMark("2");
		return testDao;
	}

}
