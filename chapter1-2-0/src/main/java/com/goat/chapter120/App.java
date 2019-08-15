package com.goat.chapter120;


import com.goat.chapter120.bean.User;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by 64274 on 2019/8/12.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/12---20:31
 */
public class App {

	ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:customer-tag.xml");

	/**  测试 xml 配置方式  */
	@Test
	public void test1(){
		User user=(User)ac.getBean("testBean");
		System.out.println("username:"+user.getUserName()+":"+"email:"+user.getEmail());
	}
}
