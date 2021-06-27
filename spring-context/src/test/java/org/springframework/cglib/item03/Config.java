package org.springframework.cglib.item03;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2021/6/27.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/27---10:45
 */
@Configuration
public class Config {

	@Bean
	public TestBean testBean(){
		return  new TestBean();
	}
}
