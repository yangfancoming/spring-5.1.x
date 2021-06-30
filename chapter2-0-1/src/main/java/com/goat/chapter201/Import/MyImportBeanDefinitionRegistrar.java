package com.goat.chapter201.Import;

import com.goat.chapter201.model.RainBow;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 把所有需要添加到容器中的bean；调用 BeanDefinitionRegistry.registerBeanDefinition手工注册进来
 */
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

	public MyImportBeanDefinitionRegistrar() {
		System.out.println("MyImportBeanDefinitionRegistrar 无参构造函数 执行");
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		boolean blue = registry.containsBeanDefinition("com.goat.chapter201.model.Blue".trim());
		boolean red = registry.containsBeanDefinition("com.goat.chapter201.model.Red".trim());
		if(blue && red){
			// 创建要导入的bean定义
			RootBeanDefinition beanDefinition = new RootBeanDefinition(RainBow.class);
			// 注册到容器中
			registry.registerBeanDefinition("rainBow", beanDefinition);
		}
	}
}
