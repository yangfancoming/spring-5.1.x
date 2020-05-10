package com.goat.chapter185.item06;

import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2020/5/9.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/9---17:47
 */
public class App {

	@Test
	public void test(){
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(RootConfig.class);
		// ApplicationContext里面是持久AutowireCapableBeanFactory这个工具的，它真实的实现类一般都是：DefaultListableBeanFactory
		AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
		// 我们吧Child的创建过程都交给Bean工厂去帮我们处理，自己连new都不需要了 （createBean方法执行多次，就会创建多个child实例）
		Child child = (Child) autowireCapableBeanFactory.createBean(Child.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
		//简直残暴，没有@Autowired注解都给注入进来了~~~  至于为什么，看看下面的分析，你就知道了
		System.out.println(child.getHelloService()); //com.fsx.service.HelloServiceImpl@6a78afa0
		// 抛出异常 No qualifying bean of type 'com.fsx.bean.Child' available
		// 能佐证：我们的Bean并没交给Spring容器管理，它只是帮我们创建好了，并把对应属性注入进去了
		Child bean = applicationContext.getBean(Child.class);
		System.out.println(bean);
	}
}
