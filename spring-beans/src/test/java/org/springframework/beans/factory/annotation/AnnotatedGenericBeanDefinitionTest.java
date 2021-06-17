package org.springframework.beans.factory.annotation;

import org.junit.Test;
import org.springframework.stereotype.Component;

import java.util.Set;


/**
 * Created by Administrator on 2020/5/11.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/11---10:24
 */
public class AnnotatedGenericBeanDefinitionTest {

	@Test
	public void test(){
		AnnotatedBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(RootConfig.class);
		// 就这么一下子，就把注解们都拿到了，简直不要太方便，简直可以当工具类来用
		Set<String> annotationTypes = beanDefinition.getMetadata().getAnnotationTypes();
		System.out.println(annotationTypes); //[org.springframework.context.annotation.ComponentScan, org.springframework.context.annotation.Configuration]
		System.out.println(beanDefinition.isSingleton()); //true
		System.out.println(beanDefinition.getBeanClassName()); //com.config.RootConfig
	}

	@Component
	class RootConfig {

	}
}