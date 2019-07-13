

package org.springframework.jdbc.core.namedparam;

import java.sql.Types;
import java.util.Arrays;

import org.junit.Test;

import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;

/**
 * @author Rick Evans
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 */
public class BeanPropertySqlParameterSourceTests {

	@Test(expected = IllegalArgumentException.class)
	public void withNullBeanPassedToCtor() {
		new BeanPropertySqlParameterSource(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getValueWhereTheUnderlyingBeanHasNoSuchProperty() {
		BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(new TestBean());
		source.getValue("thisPropertyDoesNotExist");
	}

	@Test
	public void successfulPropertyAccess() {
		BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(new TestBean("tb", 99));
		assertTrue(Arrays.asList(source.getReadablePropertyNames()).contains("name"));
		assertTrue(Arrays.asList(source.getReadablePropertyNames()).contains("age"));
		assertEquals("tb", source.getValue("name"));
		assertEquals(99, source.getValue("age"));
		assertEquals(Types.VARCHAR, source.getSqlType("name"));
		assertEquals(Types.INTEGER, source.getSqlType("age"));
	}

	@Test
	public void successfulPropertyAccessWithOverriddenSqlType() {
		BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(new TestBean("tb", 99));
		source.registerSqlType("age", Types.NUMERIC);
		assertEquals("tb", source.getValue("name"));
		assertEquals(99, source.getValue("age"));
		assertEquals(Types.VARCHAR, source.getSqlType("name"));
		assertEquals(Types.NUMERIC, source.getSqlType("age"));
	}

	@Test
	public void hasValueWhereTheUnderlyingBeanHasNoSuchProperty() {
		BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(new TestBean());
		assertFalse(source.hasValue("thisPropertyDoesNotExist"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getValueWhereTheUnderlyingBeanPropertyIsNotReadable() {
		BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(new NoReadableProperties());
		source.getValue("noOp");
	}

	@Test
	public void hasValueWhereTheUnderlyingBeanPropertyIsNotReadable() {
		BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(new NoReadableProperties());
		assertFalse(source.hasValue("noOp"));
	}


	@SuppressWarnings("unused")
	private static final class NoReadableProperties {

		public void setNoOp(String noOp) {
		}
	}

}
