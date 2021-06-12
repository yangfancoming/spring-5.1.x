package com.goat.chapter185.item08;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by Administrator on 2021/6/11.
 * @ Description:  Bean的生命周期
 * @ author  山羊来了
 * @ date 2021/6/11---19:45
 *
 * InitializingBean 与 initMethod 方法的区别：
 * 1：spring为bean提供了两种初始化bean的方式，实现InitializingBean接口，实现afterPropertiesSet方法，或者在配置文件中同过init-method指定，两种方式可以同时使用
 * 2：实现InitializingBean接口是直接调用afterPropertiesSet方法，比通过反射调用init-method指定的方法效率相对来说要高点。
 * 		但是init-method方式消除了对spring的依赖。（destroyMethod同理）
 * 3：如果调用afterPropertiesSet方法时出错，则不调用init-method指定的方法。
 *
 */
public class Baby implements BeanNameAware , BeanFactoryAware, ApplicationContextAware, InitializingBean, DisposableBean {

	private String name;
	private Integer age;

	public Baby() {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ Baby 无参构造函数 执行");
	}

	// 实现 BeanNameAware 接口 ，来获取当前bean在容器中的名称
	@Override
	public void setBeanName(String name) {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ BeanNameAware 接口的setBeanName 拿到beanName"+name);
	}

	// 实现 BeanFactoryAware 接口 ，这里可以拿到 spring 容器！
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ BeanFactoryAware 接口的setBeanFactory 拿到spring容器 beanFactory");
	}

	// 实现 ApplicationContextAware 接口 ，这里可以拿到 spring 上下文！
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ ApplicationContextAware 接口的setApplicationContext 拿到spring容器 applicationContext");
	}

	//   Bean的属性设置成功后（被spring容器），初始化
	//      不鼓励使用该接口,因为这样会将代码与Spring耦合在一起
	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ InitializingBean 接口的 afterPropertiesSet");
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ DisposableBean 接口的 destroy");
	}


	public void init1() {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ init-method 方法调用");
	}

	public void destroy1() {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ destroyMethod 方法调用");
	}

	public void hello(){
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ destroyMethod 方法调用  name ：" + name + "  age ：" + age);
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
}
