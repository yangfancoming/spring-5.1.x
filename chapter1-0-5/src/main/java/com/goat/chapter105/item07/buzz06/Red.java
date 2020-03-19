package com.goat.chapter105.item07.buzz06;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/17---16:58
 */
@Component
public class Red implements ApplicationContextAware, BeanNameAware, EmbeddedValueResolverAware {

	ApplicationContext applicationContext;

	// ApplicationContextAware
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// 传入的ioc容器：org.springframework.context.annotation.AnnotationConfigApplicationContext@3b764bce, started on Thu Mar 19 09:34:06 CST 2020
		System.out.println("传入的ioc容器：" + applicationContext);
		this.applicationContext = applicationContext;
	}

	// BeanNameAware
	@Override
	public void setBeanName(String name) {
		System.out.println("当前bean的名字：" + name); // 当前bean的名字：red
	}

	// EmbeddedValueResolverAware
	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		String s = resolver.resolveStringValue("你好${os.name} 我是#{20*2}");
		System.out.println(s);// 你好Windows 10 我是40
	}
}
