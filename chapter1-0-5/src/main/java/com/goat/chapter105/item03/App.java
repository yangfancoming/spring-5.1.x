package com.goat.chapter105.item03;

import com.goat.chapter105.BaseTest;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: 注解方式 获取bean
 * @ author  山羊来了
 * @ date 2020/3/17---13:24
 */
public class App extends BaseTest {

	ApplicationContext ac = new AnnotationConfigApplicationContext(PersonConfig.class);

	/**
	 * ***---***	 personConfig
	 * ***---***	 bill
	 * ***---***	 linus
	*/
	@Test
	public void gaga(){
		look(ac);
	}

}
