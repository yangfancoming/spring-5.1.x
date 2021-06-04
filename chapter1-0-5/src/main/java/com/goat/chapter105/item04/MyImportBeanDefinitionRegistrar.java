package com.goat.chapter105.item04;

import com.goat.chapter105.model.RainBow;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * AnnotationMetadata：当前类的注解信息
 * BeanDefinitionRegistry:BeanDefinition注册类；
 * 把所有需要添加到容器中的bean；调用 BeanDefinitionRegistry.registerBeanDefinition手工注册进来
 */
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

	public MyImportBeanDefinitionRegistrar() {
		System.out.println("MyImportBeanDefinitionRegistrar 无参构造函数 执行");
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		boolean blue = registry.containsBeanDefinition("com.goat.chapter105.model.Blue".trim());
		boolean red = registry.containsBeanDefinition("com.goat.chapter105.model.Red".trim());
		if(blue && red){
			// 指定要导入的bean
			RootBeanDefinition beanDefinition = new RootBeanDefinition(RainBow.class);
			// 指定bean的id
			registry.registerBeanDefinition("rainBow", beanDefinition);
		}
	}
}
