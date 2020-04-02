

package org.springframework.context.annotation.configuration;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * A configuration class that registers a non-static placeholder configurer {@code @Bean}
 * method cannot also have {@code @Value} fields. Logically, the config class must be
 * instantiated in order to invoke the placeholder configurer bean method, and it is a
 * chicken-and-egg problem to process the {@code @Value} field.
 *
 * Therefore, placeholder configurer bean methods should either be {@code static} or
 * put in separate configuration classes as has been done in the tests below. Simply said,
 * placeholder configurer {@code @Bean} methods and {@code @Value} fields in the same
 * configuration class are mutually exclusive unless the placeholder configurer
 * {@code @Bean} method is {@code static}.


 * @author Sam Brannen
 */
public class ConfigurationClassWithPlaceholderConfigurerBeanTests {

	/**
	 * Test which proves that a non-static property placeholder bean cannot be declared
	 * in the same configuration class that has a {@code @Value} field in need of
	 * placeholder replacement. It's an obvious chicken-and-egg issue.
	 *
	 * One solution is to do as {@link #valueFieldsAreProcessedWhenPlaceholderConfigurerIsSegregated()}
	 * does and segregate the two bean definitions across configuration classes.
	 *
	 * Another solution is to simply make the {@code @Bean} method for the property
	 * placeholder {@code static} as in
	 * {@link #valueFieldsAreProcessedWhenStaticPlaceholderConfigurerIsIntegrated()}.
	 */
	@Test
	@SuppressWarnings("resource")
	public void valueFieldsAreNotProcessedWhenPlaceholderConfigurerIsIntegrated() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(ConfigWithValueFieldAndPlaceholderConfigurer.class);
		System.setProperty("test.name", "foo");
		ctx.refresh();
		System.clearProperty("test.name");

		TestBean testBean = ctx.getBean(TestBean.class);
		// Proof that the @Value field did not get set:
		assertThat(testBean.getName(), nullValue());
	}

	@Test
	@SuppressWarnings("resource")
	public void valueFieldsAreProcessedWhenStaticPlaceholderConfigurerIsIntegrated() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(ConfigWithValueFieldAndStaticPlaceholderConfigurer.class);
		System.setProperty("test.name", "foo");
		ctx.refresh();
		System.clearProperty("test.name");

		TestBean testBean = ctx.getBean(TestBean.class);
		assertThat(testBean.getName(), equalTo("foo"));
	}

	@Test
	@SuppressWarnings("resource")
	public void valueFieldsAreProcessedWhenPlaceholderConfigurerIsSegregated() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(ConfigWithValueField.class);
		ctx.register(ConfigWithPlaceholderConfigurer.class);
		System.setProperty("test.name", "foo");
		ctx.refresh();
		System.clearProperty("test.name");

		TestBean testBean = ctx.getBean(TestBean.class);
		assertThat(testBean.getName(), equalTo("foo"));
	}

	@Test
	@SuppressWarnings("resource")
	public void valueFieldsResolveToPlaceholderSpecifiedDefaultValuesWithPlaceholderConfigurer() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(ConfigWithValueField.class);
		ctx.register(ConfigWithPlaceholderConfigurer.class);
		ctx.refresh();

		TestBean testBean = ctx.getBean(TestBean.class);
		assertThat(testBean.getName(), equalTo("bar"));
	}

	@Test
	@SuppressWarnings("resource")
	public void valueFieldsResolveToPlaceholderSpecifiedDefaultValuesWithoutPlaceholderConfigurer() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(ConfigWithValueField.class);
		// ctx.register(ConfigWithPlaceholderConfigurer.class);
		ctx.refresh();

		TestBean testBean = ctx.getBean(TestBean.class);
		assertThat(testBean.getName(), equalTo("bar"));
	}


	@Configuration
	static class ConfigWithValueField {

		@Value("${test.name:bar}")
		private String name;

		@Bean
		public ITestBean testBean() {
			return new TestBean(this.name);
		}
	}


	@Configuration
	static class ConfigWithPlaceholderConfigurer {

		@Bean
		public PropertySourcesPlaceholderConfigurer ppc() {
			return new PropertySourcesPlaceholderConfigurer();
		}
	}


	@Configuration
	static class ConfigWithValueFieldAndPlaceholderConfigurer {

		@Value("${test.name}")
		private String name;

		@Bean
		public ITestBean testBean() {
			return new TestBean(this.name);
		}

		@Bean
		public PropertySourcesPlaceholderConfigurer ppc() {
			return new PropertySourcesPlaceholderConfigurer();
		}
	}

	@Configuration
	static class ConfigWithValueFieldAndStaticPlaceholderConfigurer {

		@Value("${test.name}")
		private String name;

		@Bean
		public ITestBean testBean() {
			return new TestBean(this.name);
		}

		@Bean
		public static PropertySourcesPlaceholderConfigurer ppc() {
			return new PropertySourcesPlaceholderConfigurer();
		}
	}

}
