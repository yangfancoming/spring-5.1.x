

package org.springframework.util;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

public class PropertyPlaceholderHelperTests {

	private final PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");

	// 测试单属性 Properties
	@Test
	public void testWithProperties() {
		String text = "foo=${foo}";
		Properties props = new Properties();
		props.setProperty("foo", "bar");
		assertEquals("foo=bar", helper.replacePlaceholders(text, props));
	}


	// 测试多属性 Properties
	@Test
	public void testWithMultipleProperties() {
		String text = "foo=${foo},bar=${bar}";
		Properties props = new Properties();
		props.setProperty("foo", "bar");
		props.setProperty("bar", "baz");
		assertEquals("foo=bar,bar=baz", this.helper.replacePlaceholders(text, props));
	}

	// 测试递归属性 Properties
	@Test
	public void testRecurseInProperty() {
		String text = "foo=${bar}";
		Properties props = new Properties();
		props.setProperty("bar", "${baz}");
		props.setProperty("baz", "bar");
		assertEquals("foo=bar", this.helper.replacePlaceholders(text, props));
	}

	// 测试递归属性 Placeholder
	@Test
	public void testRecurseInPlaceholder() {
		String text = "foo=${b${inner}}";
		Properties props = new Properties();
		props.setProperty("bar", "bar");
		props.setProperty("inner", "ar");
		assertEquals("foo=bar", this.helper.replacePlaceholders(text, props));

		text = "${top}";
		props = new Properties();
		props.setProperty("top", "${child}+${child}");
		props.setProperty("child", "${${differentiator}.grandchild}");
		props.setProperty("differentiator", "first");
		props.setProperty("first.grandchild", "actualValue");
		assertEquals("actualValue+actualValue", this.helper.replacePlaceholders(text, props));
	}

	MyPlaceholderResolver myPlaceholderResolver = new MyPlaceholderResolver();
	// 测试 PlaceholderResolver 函数式编程接口
	@Test
	public void testWithResolver() {
		String text = "foo=${goat}";
		assertEquals("foo=bar",this.helper.replacePlaceholders(text, myPlaceholderResolver));
	}

	public static class MyPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {
		@Override
		public String resolvePlaceholder(String placeholderName) {
			if ("goat".equals(placeholderName))
				return "bar";
			else
				return null;
		}
	}

	// 测试  Properties中未能匹配到的占位符  将被忽略
	@Test
	public void testUnresolvedPlaceholderIsIgnored() {
		String text = "foo=${foo},bar=${bar}";
		Properties props = new Properties();
		props.setProperty("foo", "bar");
		assertEquals("foo=bar,bar=${bar}", this.helper.replacePlaceholders(text, props));
	}

	// 测试 ignoreUnresolvablePlaceholders 参数指定为false时 并且没解析到对应的占位符 将导致抛出异常
	@Test(expected = IllegalArgumentException.class)
	public void testUnresolvedPlaceholderAsError() {
		String text = "foo=${foo},bar=${bar}";
		Properties props = new Properties();
		props.setProperty("foo", "bar");

		PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}", null, false);
		assertEquals("foo=bar,bar=${bar}", helper.replacePlaceholders(text, props));
	}

	// 其他类型占位符测试
	@Test
	public void test1() {
		PropertyPlaceholderHelper test = new PropertyPlaceholderHelper("*", "*", null, false);
		String text = "foo=*foo*";
		Properties props = new Properties();
		props.setProperty("foo", "bar");
		assertEquals("foo=bar", test.replacePlaceholders(text, props));
	}

	@Test
	public void test2() {
		PropertyPlaceholderHelper test = new PropertyPlaceholderHelper("[", "]", null, false);
		String text = "foo=[foo]";
		Properties props = new Properties();
		props.setProperty("foo", "bar");
		assertEquals("foo=bar", test.replacePlaceholders(text, props));
	}

	@Test
	public void test3()throws Exception {
		String a = "{name}{age}{sex}";
		String b = "{name{age}{sex}}";
		// doit 获取配置文件方式？
		InputStream in = new BufferedInputStream(new FileInputStream(new File("org/springframework/util/PropertyPlaceholderHelper.properties")));
		Properties properties = new Properties();
		properties.load(in);

		System.out.println("替换前:" + a);
		System.out.println("替换后:" + helper.parseStringValue(a, placeholderName->{
			String value = properties.getProperty(placeholderName);
			return value;
		}, new HashSet<>()));

		System.out.println("====================================================");
		System.out.println("替换前:" + b);
		System.out.println("替换后:" + helper.parseStringValue(b, placeholderName->{
			String value = properties.getProperty(placeholderName);
			return value;
		}, new HashSet<>()));
	}
}
