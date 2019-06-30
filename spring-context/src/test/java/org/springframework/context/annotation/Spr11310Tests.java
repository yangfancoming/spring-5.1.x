

package org.springframework.context.annotation;

import java.util.List;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;

import static org.junit.Assert.*;

/**
 * @author Stephane Nicoll
 */
public class Spr11310Tests {

	@Test
	public void orderedList() {
		ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
		StringHolder holder = context.getBean(StringHolder.class);
		assertEquals("second", holder.itemsList.get(0));
		assertEquals("first", holder.itemsList.get(1));
		assertEquals("unknownOrder", holder.itemsList.get(2));
	}

	@Test
	public void orderedArray() {
		ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
		StringHolder holder = context.getBean(StringHolder.class);
		assertEquals("second", holder.itemsArray[0]);
		assertEquals("first", holder.itemsArray[1]);
		assertEquals("unknownOrder", holder.itemsArray[2]);
	}


	@Configuration
	static class Config {

		@Bean
		@Order(50)
		public String first() {
			return "first";
		}

		@Bean
		public String unknownOrder() {
			return "unknownOrder";
		}

		@Bean
		@Order(5)
		public String second() {
			return "second";
		}

		@Bean
		public StringHolder stringHolder() {
			return new StringHolder();
		}

	}


	private static class StringHolder {
		@Autowired
		private List<String> itemsList;

		@Autowired
		private String[] itemsArray;

	}
}
