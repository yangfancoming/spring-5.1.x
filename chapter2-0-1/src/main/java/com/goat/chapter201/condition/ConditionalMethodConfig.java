package com.goat.chapter201.condition;

import com.goat.chapter201.common.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: @Conditional 注解作用于方法
 * @ author  山羊来了
 * @ date 2020/3/17---13:48
 */
@Configuration
public class ConditionalMethodConfig {

	/**
	 * @Conditional({Condition}) ： 按照一定的条件进行判断，满足条件给容器中注册bean
	 * 如果系统是windows，给容器中注册("bill")
	 * 如果是linux系统，给容器中注册("linus")
	 */

	@Bean
	@Conditional(ConditionWindows.class)
	public Person bill(){
		return new Person("Bill Gates",62);
	}

	@Bean
	@Conditional(ConditionLinux.class)
	public Person linus(){
		return new Person("linus", 48);
	}
}
