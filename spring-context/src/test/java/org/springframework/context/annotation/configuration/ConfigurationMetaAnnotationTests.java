

package org.springframework.context.annotation.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.tests.sample.beans.TestBean;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Ensures that @Configuration is supported properly as a meta-annotation.
 */
public class ConfigurationMetaAnnotationTests {

	@Test
	public void customConfigurationStereotype() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(Config.class);
		ctx.refresh();
		assertThat(ctx.containsBean("customName"), is(true));
		TestBean a = ctx.getBean("a", TestBean.class);
		TestBean b = ctx.getBean("b", TestBean.class);
		assertThat(b, sameInstance(a.getSpouse()));
	}


	@TestConfiguration("customName")
	static class Config {
		@Bean
		public TestBean a() {
			TestBean a = new TestBean();
			a.setSpouse(b());
			return a;
		}
		@Bean
		public TestBean b() {
			return new TestBean();
		}
	}


	@Configuration
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TestConfiguration {
		String value() default "";
	}
}
