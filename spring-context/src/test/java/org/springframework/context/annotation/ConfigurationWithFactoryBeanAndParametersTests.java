

package org.springframework.context.annotation;

import org.junit.Test;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.*;

/**
 * Test case cornering the bug initially raised with SPR-8762, in which a
 * NullPointerException would be raised if a FactoryBean-returning @Bean method also accepts parameters
 * @since 3.1
 */
public class ConfigurationWithFactoryBeanAndParametersTests {

	@Test
	public void test() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class, Bar.class);
		assertNotNull(ctx.getBean(Bar.class).foo);
	}


	@Configuration
	static class Config {

		@Bean
		public FactoryBean<Foo> fb(@Value("42") String answer) {
			return new FooFactoryBean();
		}
	}


	static class Foo {
	}


	static class Bar {

		Foo foo;

		@Autowired
		public Bar(Foo foo) {
			this.foo = foo;
		}
	}


	static class FooFactoryBean implements FactoryBean<Foo> {

		@Override
		public Foo getObject() {
			return new Foo();
		}

		@Override
		public Class<Foo> getObjectType() {
			return Foo.class;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}

}
