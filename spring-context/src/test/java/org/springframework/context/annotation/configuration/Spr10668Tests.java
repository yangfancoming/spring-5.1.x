

package org.springframework.context.annotation.configuration;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.*;

/**
 * Tests for SPR-10668.
 */
public class Spr10668Tests {

	@Test
	public void testSelfInjectHierarchy()  {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ChildConfig.class);
		assertNotNull(context.getBean(MyComponent.class));
		context.close();
	}


	@Configuration
	public static class ParentConfig {

		@Autowired(required = false)
		MyComponent component;
	}


	@Configuration
	public static class ChildConfig extends ParentConfig {

		@Bean
		public MyComponentImpl myComponent() {
			return new MyComponentImpl();
		}
	}


	public interface MyComponent {}

	public static class MyComponentImpl implements MyComponent {}

}
