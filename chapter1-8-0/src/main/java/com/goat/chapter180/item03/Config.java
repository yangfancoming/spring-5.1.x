package com.goat.chapter180.item03;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2021/6/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/16---14:52
 */
@Configuration
public class Config {

	@Bean
	public MyService service() {
		return new MyService();
	}
}
