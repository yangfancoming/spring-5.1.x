

package org.springframework.context.annotation;

import javax.annotation.Resource;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Tests changes introduced for SPR-8874, allowing beans of primitive types to be looked
 * up via getBean(Class), or to be injected using @Autowired or @Injected or @Resource.
 * Prior to these changes, an attempt to lookup or inject a bean of type boolean would
 * fail because all spring beans are Objects, regardless of initial type due to the way
 * that ObjectFactory works.
 *
 * Now these attempts to lookup or inject primitive types work, thanks to simple changes
 * in AbstractBeanFactory using ClassUtils#isAssignable methods instead of the built-in
 * Class#isAssignableFrom. The former takes into account primitives and their object
 * wrapper types, whereas the latter does not.
 *
 * @author Chris Beams
 * @since 3.1
 */
public class PrimitiveBeanLookupAndAutowiringTests {

	@Test
	public void primitiveLookupByName() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
		boolean b = ctx.getBean("b", boolean.class);
		assertThat(b, equalTo(true));
		int i = ctx.getBean("i", int.class);
		assertThat(i, equalTo(42));
	}

	@Test
	public void primitiveLookupByType() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
		boolean b = ctx.getBean(boolean.class);
		assertThat(b, equalTo(true));
		int i = ctx.getBean(int.class);
		assertThat(i, equalTo(42));
	}

	@Test
	public void primitiveAutowiredInjection() {
		ApplicationContext ctx =
				new AnnotationConfigApplicationContext(Config.class, AutowiredComponent.class);
		assertThat(ctx.getBean(AutowiredComponent.class).b, equalTo(true));
		assertThat(ctx.getBean(AutowiredComponent.class).i, equalTo(42));
	}

	@Test
	public void primitiveResourceInjection() {
		ApplicationContext ctx =
				new AnnotationConfigApplicationContext(Config.class, ResourceComponent.class);
		assertThat(ctx.getBean(ResourceComponent.class).b, equalTo(true));
		assertThat(ctx.getBean(ResourceComponent.class).i, equalTo(42));
	}


	@Configuration
	static class Config {
		@Bean
		public boolean b() {
			return true;
		}

		@Bean
		public int i() {
			return 42;
		}
	}


	static class AutowiredComponent {
		@Autowired boolean b;
		@Autowired int i;
	}


	static class ResourceComponent {
		@Resource boolean b;
		@Autowired int i;
	}
}
