

package org.springframework.context.annotation;

import org.junit.Test;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Tests to verify that FactoryBean semantics are the same in Configuration classes as in XML.
 */
public class Spr6602Tests {

	//  测试 xml 配置
	@Test
	public void testXmlBehavior() throws Exception {
		ClassPathXmlApplicationContext xml = new ClassPathXmlApplicationContext("Spr6602Tests-context.xml", Spr6602Tests.class);
		test1(xml);
		test2(xml);
		test3(xml);
	}

	// 测试 注解 配置
	@Test
	public void testConfigurationClassBehavior() throws Exception {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(FooConfig.class);
		test1(ctx);
//		test2(ctx);
//		test3(ctx);
	}

	private void  test1(ApplicationContext ctx){
		Foo foo = ctx.getBean(Foo.class);
		Bar bar = ctx.getBean(Bar.class);
		assertThat(bar, is(foo.bar));
	}

	private void test2(ApplicationContext ctx){
		Bar bar1 = ctx.getBean(Bar.class);
		Bar bar2 = ctx.getBean(Bar.class);
		assertThat(bar1, is(bar2));
	}

	private void test3(ApplicationContext ctx) throws Exception {
		BarFactory barFactory1 = ctx.getBean(BarFactory.class);
		BarFactory barFactory2 = ctx.getBean(BarFactory.class);
		assertThat(barFactory1, is(barFactory2));
		Bar bar3 = barFactory1.getObject();
		Bar bar4 = barFactory1.getObject();
		assertThat(bar3, is(not(bar4)));
	}

	@Configuration
	public static class FooConfig {
		@Bean
		public Foo foo() throws Exception {
			return new Foo(barFactory().getObject());
		}
		@Bean
		public BarFactory barFactory() {
			return new BarFactory();
		}
	}


	public static class Foo {
		final Bar bar;
		public Foo(Bar bar) {
			System.out.println("Foo 单参构造函数 执行");
			this.bar = bar;
		}
	}

	public static class Bar {
		public Bar() {
			System.out.println("Bar 单参构造函数 执行");
		}
	}

	public static class BarFactory implements FactoryBean<Bar> {
		@Override
		public Bar getObject() throws Exception {
			return new Bar();
		}
		@Override
		public Class<? extends Bar> getObjectType() {
			return Bar.class;
		}
		@Override
		public boolean isSingleton() {
			return true;
		}
	}
}
