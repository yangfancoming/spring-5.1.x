package com.goat.chapter201.Import;

import com.goat.chapter201.model.Green;
import org.springframework.context.annotation.Bean;

/**
 * Created by Administrator on 2020/4/2.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/2---21:21
 */
public class OtherConfig {

	@Bean
	public Green green(){
		return new Green();
	}
}
