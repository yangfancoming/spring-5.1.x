package org.springframework.core.env;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.mock.env.MockPropertySource;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @see PropertySourcesPropertyResolver
 * @since 3.1
 */
public class PropertySourcesPropertyResolverTests {

	private ConfigurablePropertyResolver propertyResolver;
	private Properties testProperties;
	private MutablePropertySources mutablePropertySources;

	/**
	 * 使用 ConfigurablePropertyResolver 解析器来解析指定数据源。
	*/
	@Before
	public void setUp() {
		// 新建可变数据源（持有数据源集合）
		mutablePropertySources = new MutablePropertySources();
		// 创建属性源解析器，输入带解析的属性源集合
		propertyResolver = new PropertySourcesPropertyResolver(mutablePropertySources);
		testProperties = new Properties();
		// 为属性源集合 添加待解析的属性源
		mutablePropertySources.addFirst(new PropertiesPropertySource("testProperties", testProperties));
	}

	/**
	 * 测试使用解析器 解析Properties属性源  containsProperty 方法
	*/
	@Test
	public void containsProperty() {
		Assert.assertFalse(propertyResolver.containsProperty("foo"));
		testProperties.put("foo", "bar");
		Assert.assertTrue(propertyResolver.containsProperty("foo"));
	}

	/**
	 * 测试 getProperty 获取的目标value中没有占位的情况
	*/
	@Test
	public void getProperty() {
		Assert.assertEquals(null,propertyResolver.getProperty("foo"));
		testProperties.put("foo", "bar");
		Assert.assertEquals("bar",propertyResolver.getProperty("foo"));
	}

	/**
	 * 测试 getProperty 获取的目标value中没有占位的情况，使用带默认值的情况
	 */
	@Test
	public void getProperty_withDefaultValue() {
		Assert.assertEquals("myDefault",propertyResolver.getProperty("foo", "myDefault"));
		testProperties.put("foo", "bar");
		Assert.assertEquals("bar",propertyResolver.getProperty("foo"));
	}

	@Test
	public void getProperty_propertySourceSearchOrderIsFIFO() {
		MutablePropertySources sources = new MutablePropertySources();
		PropertyResolver resolver = new PropertySourcesPropertyResolver(sources);
		sources.addFirst(new MockPropertySource("ps1").withProperty("pName", "ps1Value"));
		assertThat(resolver.getProperty("pName"), equalTo("ps1Value"));
		sources.addFirst(new MockPropertySource("ps2").withProperty("pName", "ps2Value"));
		assertThat(resolver.getProperty("pName"), equalTo("ps2Value"));
		sources.addFirst(new MockPropertySource("ps3").withProperty("pName", "ps3Value"));
		assertThat(resolver.getProperty("pName"), equalTo("ps3Value"));
	}

	@Test
	public void getProperty_withExplicitNullValue() {
		// java.util.Properties does not allow null values (because Hashtable does not)
		Map<String, Object> nullableProperties = new HashMap<>();
		mutablePropertySources.addLast(new MapPropertySource("nullableProperties", nullableProperties));
		nullableProperties.put("foo", null);
		assertThat(propertyResolver.getProperty("foo"), nullValue());
	}

	@Test
	public void getProperty_withTargetType_andDefaultValue() {
		assertThat(propertyResolver.getProperty("foo", Integer.class, 42), equalTo(42));
		testProperties.put("foo", 13);
		assertThat(propertyResolver.getProperty("foo", Integer.class, 42), equalTo(13));
	}

	/**
	 * 测试 getProperty 获取的目标value为 String[] 的情况
	 */
	@Test
	public void getProperty_withStringArrayConversion() {
		testProperties.put("foo", "bar,baz");
		assertThat(propertyResolver.getProperty("foo", String[].class), equalTo(new String[] { "bar", "baz" }));
	}

	@Test
	public void getProperty_withNonConvertibleTargetType() {
		testProperties.put("foo", "bar");
		class TestType { }
		try {
			propertyResolver.getProperty("foo", TestType.class);
			fail("Expected ConverterNotFoundException due to non-convertible types");
		}catch (ConverterNotFoundException ex) {
			// expected
		}
	}

	@Test
	public void getProperty_doesNotCache_replaceExistingKeyPostConstruction() {
		String key = "foo";
		String value1 = "bar";
		String value2 = "biz";
		HashMap<String, Object> map = new HashMap<>();
		map.put(key, value1); // before construction
		MutablePropertySources propertySources = new MutablePropertySources();
		propertySources.addFirst(new MapPropertySource("testProperties", map));
		PropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);
		assertThat(propertyResolver.getProperty(key), equalTo(value1));
		map.put(key, value2); // after construction and first resolution
		assertThat(propertyResolver.getProperty(key), equalTo(value2));
	}

	@Test
	public void getProperty_doesNotCache_addNewKeyPostConstruction() {
		HashMap<String, Object> map = new HashMap<>();
		MutablePropertySources propertySources = new MutablePropertySources();
		propertySources.addFirst(new MapPropertySource("testProperties", map));
		PropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);
		assertThat(propertyResolver.getProperty("foo"), equalTo(null));
		map.put("foo", "42");
		assertThat(propertyResolver.getProperty("foo"), equalTo("42"));
	}

	@Test
	public void getPropertySources_replacePropertySource() {
		mutablePropertySources = new MutablePropertySources();
		propertyResolver = new PropertySourcesPropertyResolver(mutablePropertySources);
		mutablePropertySources.addLast(new MockPropertySource("local").withProperty("foo", "localValue"));
		mutablePropertySources.addLast(new MockPropertySource("system").withProperty("foo", "systemValue"));
		// 'local' was added first so has precedence
		assertThat(propertyResolver.getProperty("foo"), equalTo("localValue"));
		// replace 'local' with new property source
		mutablePropertySources.replace("local", new MockPropertySource("new").withProperty("foo", "newValue"));
		// 'system' now has precedence
		assertThat(propertyResolver.getProperty("foo"), equalTo("newValue"));
		assertThat(mutablePropertySources.size(), is(2));
	}

	@Test
	public void getRequiredProperty() {
		testProperties.put("exists", "xyz");
		assertThat(propertyResolver.getRequiredProperty("exists"), is("xyz"));
		try {
			propertyResolver.getRequiredProperty("bogus");
			fail("expected IllegalStateException");
		}catch (IllegalStateException ex) {
			// expected
		}
	}

	@Test
	public void getRequiredProperty_withStringArrayConversion() {
		testProperties.put("exists", "abc,123");
		assertThat(propertyResolver.getRequiredProperty("exists", String[].class), equalTo(new String[] { "abc", "123" }));
		try {
			propertyResolver.getRequiredProperty("bogus", String[].class);
			fail("expected IllegalStateException");
		}catch (IllegalStateException ex) {
			// expected
		}
	}

	@Test
	public void resolvePlaceholders() {
		MutablePropertySources propertySources = new MutablePropertySources();
		propertySources.addFirst(new MockPropertySource().withProperty("key", "value"));
		PropertyResolver resolver = new PropertySourcesPropertyResolver(propertySources);
		assertThat(resolver.resolvePlaceholders("Replace this ${key}"), equalTo("Replace this value"));
	}

	@Test
	public void resolvePlaceholders_withUnresolvable() {
		MutablePropertySources propertySources = new MutablePropertySources();
		propertySources.addFirst(new MockPropertySource().withProperty("key", "value"));
		PropertyResolver resolver = new PropertySourcesPropertyResolver(propertySources);
		assertThat(resolver.resolvePlaceholders("Replace this ${key} plus ${unknown}"),equalTo("Replace this value plus ${unknown}"));
	}

	@Test
	public void resolvePlaceholders_withDefaultValue() {
		MutablePropertySources propertySources = new MutablePropertySources();
		propertySources.addFirst(new MockPropertySource().withProperty("key", "value"));
		PropertyResolver resolver = new PropertySourcesPropertyResolver(propertySources);
		assertThat(resolver.resolvePlaceholders("Replace this ${key} plus ${unknown:defaultValue}"),equalTo("Replace this value plus defaultValue"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void resolvePlaceholders_withNullInput() {
		new PropertySourcesPropertyResolver(new MutablePropertySources()).resolvePlaceholders(null);
	}

	@Test
	public void resolveRequiredPlaceholders() {
		MutablePropertySources propertySources = new MutablePropertySources();
		propertySources.addFirst(new MockPropertySource().withProperty("key", "value"));
		PropertyResolver resolver = new PropertySourcesPropertyResolver(propertySources);
		assertThat(resolver.resolveRequiredPlaceholders("Replace this ${key}"), equalTo("Replace this value"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void resolveRequiredPlaceholders_withUnresolvable() {
		MutablePropertySources propertySources = new MutablePropertySources();
		propertySources.addFirst(new MockPropertySource().withProperty("key", "value"));
		PropertyResolver resolver = new PropertySourcesPropertyResolver(propertySources);
		resolver.resolveRequiredPlaceholders("Replace this ${key} plus ${unknown}");
	}

	@Test
	public void resolveRequiredPlaceholders_withDefaultValue() {
		MutablePropertySources propertySources = new MutablePropertySources();
		propertySources.addFirst(new MockPropertySource().withProperty("key", "value"));
		PropertyResolver resolver = new PropertySourcesPropertyResolver(propertySources);
		assertThat(resolver.resolveRequiredPlaceholders("Replace this ${key} plus ${unknown:defaultValue}"),equalTo("Replace this value plus defaultValue"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void resolveRequiredPlaceholders_withNullInput() {
		new PropertySourcesPropertyResolver(new MutablePropertySources()).resolveRequiredPlaceholders(null);
	}

	@Test
	public void setRequiredProperties_andValidateRequiredProperties() {
		// no properties have been marked as required -> validation should pass
		propertyResolver.validateRequiredProperties();
		// mark which properties are required
		propertyResolver.setRequiredProperties("foo", "bar");
		// neither foo nor bar properties are present -> validating should throw
		try {
			propertyResolver.validateRequiredProperties();
			fail("expected validation exception");
		}catch (MissingRequiredPropertiesException ex) {
			assertThat(ex.getMessage(), equalTo("The following properties were declared as required but could not be resolved: [foo, bar]"));
		}

		// add foo property -> validation should fail only on missing 'bar' property
		testProperties.put("foo", "fooValue");
		try {
			propertyResolver.validateRequiredProperties();
			fail("expected validation exception");
		}catch (MissingRequiredPropertiesException ex) {
			assertThat(ex.getMessage(), equalTo("The following properties were declared as required but could not be resolved: [bar]"));
		}
		// add bar property -> validation should pass, even with an empty string value
		testProperties.put("bar", "");
		propertyResolver.validateRequiredProperties();
	}

	@Test
	public void resolveNestedPropertyPlaceholders() {
		MutablePropertySources ps = new MutablePropertySources();
		ps.addFirst(new MockPropertySource()
			.withProperty("p1", "v1")
			.withProperty("p2", "v2")
			.withProperty("p3", "${p1}:${p2}")              // nested placeholders
			.withProperty("p4", "${p3}")                    // deeply nested placeholders
			.withProperty("p5", "${p1}:${p2}:${bogus}")     // unresolvable placeholder
			.withProperty("p6", "${p1}:${p2}:${bogus:def}") // unresolvable w/ default
			.withProperty("pL", "${pR}")                    // cyclic reference left
			.withProperty("pR", "${pL}")                    // cyclic reference right
		);
		ConfigurablePropertyResolver pr = new PropertySourcesPropertyResolver(ps);
		assertThat(pr.getProperty("p1"), equalTo("v1"));
		assertThat(pr.getProperty("p2"), equalTo("v2"));
		assertThat(pr.getProperty("p3"), equalTo("v1:v2"));
		assertThat(pr.getProperty("p4"), equalTo("v1:v2"));
		try {
			pr.getProperty("p5");
		}catch (IllegalArgumentException ex) {
			assertThat(ex.getMessage(), containsString("Could not resolve placeholder 'bogus' in value \"${p1}:${p2}:${bogus}\""));
		}
		assertThat(pr.getProperty("p6"), equalTo("v1:v2:def"));
		try {
			pr.getProperty("pL");
		}catch (IllegalArgumentException ex) {
			assertTrue(ex.getMessage().toLowerCase().contains("circular"));
		}
	}

	@Test
	public void ignoreUnresolvableNestedPlaceholdersIsConfigurable() {
		MutablePropertySources ps = new MutablePropertySources();
		ps.addFirst(new MockPropertySource()
			.withProperty("p1", "v1")
			.withProperty("p2", "v2")
			.withProperty("p3", "${p1}:${p2}:${bogus:def}") // unresolvable w/ default
			.withProperty("p4", "${p1}:${p2}:${bogus}")     // unresolvable placeholder
		);
		ConfigurablePropertyResolver pr = new PropertySourcesPropertyResolver(ps);
		assertThat(pr.getProperty("p1"), equalTo("v1"));
		assertThat(pr.getProperty("p2"), equalTo("v2"));
		assertThat(pr.getProperty("p3"), equalTo("v1:v2:def"));
		// placeholders nested within the value of "p4" are unresolvable and cause an exception by default
		try {
			pr.getProperty("p4");
		}catch (IllegalArgumentException ex) {
			assertThat(ex.getMessage(), containsString("Could not resolve placeholder 'bogus' in value \"${p1}:${p2}:${bogus}\""));
		}
		// relax the treatment of unresolvable nested placeholders
		pr.setIgnoreUnresolvableNestedPlaceholders(true);
		// and observe they now pass through unresolved
		assertThat(pr.getProperty("p4"), equalTo("v1:v2:${bogus}"));
		// resolve[Nested]Placeholders methods behave as usual regardless the value of
		// ignoreUnresolvableNestedPlaceholders
		assertThat(pr.resolvePlaceholders("${p1}:${p2}:${bogus}"), equalTo("v1:v2:${bogus}"));
		try {
			pr.resolveRequiredPlaceholders("${p1}:${p2}:${bogus}");
		}catch (IllegalArgumentException ex) {
			assertThat(ex.getMessage(), containsString("Could not resolve placeholder 'bogus' in value \"${p1}:${p2}:${bogus}\""));
		}
	}

}
