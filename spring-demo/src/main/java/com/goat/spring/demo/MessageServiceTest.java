package com.goat.spring.demo;


import com.goat.spring.demo.service.MessageService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by 64274 on 2019/6/27.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/27---20:33
 */
public class MessageServiceTest {

	public static void main(String[] args) {

		// 用我们的配置文件来启动一个 ApplicationContext
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:application.xml");
		// 从 context 中取出我们的 Bean，而不是用 new MessageServiceImpl() 这种方式
		MessageService messageService = context.getBean(MessageService.class);
		System.out.println(messageService.getMessage()); // 这句将输出: hello world
	}
}
