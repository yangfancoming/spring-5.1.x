

package org.springframework.context.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import org.junit.Test;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.MethodMetadata;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


public class BeanMethodMetadataTests {

	@Test
	public void providesBeanMethodBeanDefinition() {
		AnnotationConfigApplicationContext context= new AnnotationConfigApplicationContext(Conf.class);
		BeanDefinition beanDefinition = context.getBeanDefinition("myBean");
		assertThat("should provide AnnotatedBeanDefinition", beanDefinition, instanceOf(AnnotatedBeanDefinition.class));
		MethodMetadata factoryMethodMetadata = ((AnnotatedBeanDefinition) beanDefinition).getFactoryMethodMetadata();
		Map<String, Object> annotationAttributes = factoryMethodMetadata.getAnnotationAttributes(MyAnnotation.class.getName());
		assertThat(annotationAttributes.get("value"), equalTo("test"));
		context.close();
	}


	@Configuration
	static class Conf {
		@Bean
		@MyAnnotation("test")
		public MyBean myBean() {
			return new MyBean();
		}
	}

	static class MyBean {
	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface MyAnnotation {
		String value();
	}
}
