package com.goat.chapter105.item03;

import com.goat.chapter105.model.Person;
import org.springframework.context.annotation.*;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: @Conditional 注解作用于方法
 * @ author  山羊来了
 * @ date 2020/3/17---13:48
 */
@Configuration // 该注解就相当于传统的xml文件
public class ConditionalMethodConfig {

	/**
	 * @Conditional({Condition}) ： 按照一定的条件进行判断，满足条件给容器中注册bean
	 * 如果系统是windows，给容器中注册("bill")
	 * 如果是linux系统，给容器中注册("linus")
	 */
	@Conditional(ConditionWindows.class)
	@Bean
	public Person bill(){
		return new Person("Bill Gates",62);
	}

	@Conditional(ConditionLinux.class)
	@Bean
	public Person linus(){
		return new Person("linus", 48);
	}
}
