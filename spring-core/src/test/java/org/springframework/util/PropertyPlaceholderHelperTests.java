package org.springframework.util;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import static org.junit.Assert.assertEquals;


public class PropertyPlaceholderHelperTests {

	private final PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");

	Properties props = new Properties();

	public PropertyPlaceholderHelperTests() throws IOException {}

	// 测试单属性 Properties
	@Test
	public void testWithProperties() {
		props.setProperty("foo", "bar");
		assertEquals("foo=bar", helper.replacePlaceholders("foo=${foo}", props));
	}

	// 测试多属性 Properties  不会嵌套关联 eg： foo只会映射成bar 不会映射成baz
	@Test
	public void testWithMultipleProperties() {
		props.setProperty("foo", "bar");
		props.setProperty("bar", "baz");
		assertEquals("foo=bar,bar=baz", helper.replacePlaceholders("foo=${foo},bar=${bar}", props));
	}

	// 测试递归属性 Properties  递归关联
	@Test
	public void testRecurseInProperty() {
		props.setProperty("bar", "${baz}");
		props.setProperty("baz", "bar");
		assertEquals("foo=bar", helper.replacePlaceholders("foo=${bar}", props));
	}

	// 测试递归属性 Placeholder
	@Test
	public void testRecurseInPlaceholder() {
		String text = "foo=${b${inner}}";
		props.setProperty("bar", "goat");
		props.setProperty("inner", "ar");
		assertEquals("foo=goat", helper.replacePlaceholders(text, props));

		text = "${top}";
		props = new Properties();
		props.setProperty("top", "${child}+${child}");
		props.setProperty("child", "${${differentiator}.grandchild}");
		props.setProperty("differentiator", "first");
		props.setProperty("first.grandchild", "actualValue");
		assertEquals("actualValue+actualValue", helper.replacePlaceholders(text, props));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnresolvedPlaceholderAsError() {
		String text = "foo=${foo},bar=${bar}";
		props.setProperty("foo", "bar");
		// 测试  Properties中未能匹配到的占位符  将被忽略
		assertEquals("foo=bar,bar=${bar}", helper.replacePlaceholders(text, props));

		// 测试 ignoreUnresolvablePlaceholders 参数指定为false时 并且没解析到对应的占位符 将导致抛出异常
		PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}", null, false);
		assertEquals("foo=bar,bar=${bar}", helper.replacePlaceholders(text, props));
	}

	MyPlaceholderResolver myPlaceholderResolver = new MyPlaceholderResolver();

	public static class MyPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {
		@Override
		public String resolvePlaceholder(String placeholderName) {
			if ("goat".equals(placeholderName))
				return "bar";
			else
				return null;
		}
	}

	// 测试 PlaceholderResolver 函数式编程接口
	@Test
	public void testWithResolver() {
		String text1 = "foo=${goat}";	// 可以正确解析的情况
		assertEquals("foo=bar",helper.replacePlaceholders(text1, myPlaceholderResolver));
		String text2 = "foo=${nocase}"; 	// 解析不了的情况
		assertEquals("foo=${nocase}",helper.replacePlaceholders(text2, myPlaceholderResolver));
	}

	// 其他类型占位符测试
	@Test
	public void test1() {
		PropertyPlaceholderHelper test = new PropertyPlaceholderHelper("*", "*", null, false);
		String text = "foo=*foo*";
		props.setProperty("foo", "bar");
		assertEquals("foo=bar", test.replacePlaceholders(text, props));
	}

	@Test
	public void test2() {
		PropertyPlaceholderHelper test = new PropertyPlaceholderHelper("[", "]", null, false);
		String text = "foo=[foo]";
		props.setProperty("foo", "bar");
		assertEquals("foo=bar", test.replacePlaceholders(text, props));
	}

	private final static String PROPERTIES = "org/springframework/util/PropertyPlaceholderHelper.properties";

	Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource(PROPERTIES));


	@Test
	public void testParseStringValue1()  {
		String a = "${name}${age}${sex}";
		System.out.println("a替换前:" + a);
		System.out.println("a替换后:" + helper.parseStringValue(a, placeholderName->{
			String value = properties.getProperty(placeholderName);
			return value;
		}, new HashSet<>()));
	}

	@Test
	public void testParseStringValue2() {
		String b = "{name{age}{sex}}";
		System.out.println("b替换前:" + b);
		System.out.println("b替换后:" + helper.parseStringValue(b, placeholderName->{
			String value = properties.getProperty(placeholderName);
			return value;
		}, new HashSet<>()));
	}
}
