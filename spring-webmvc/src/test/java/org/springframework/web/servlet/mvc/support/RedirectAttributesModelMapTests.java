

package org.springframework.web.servlet.mvc.support;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.validation.DataBinder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 * Test fixture for {@link RedirectAttributesModelMap} tests.
 *
 *
 * @since 3.1
 */
public class RedirectAttributesModelMapTests {

	private RedirectAttributesModelMap redirectAttributes;

	private FormattingConversionService conversionService;

	@Before
	public void setup() {
		this.conversionService = new DefaultFormattingConversionService();
		DataBinder dataBinder = new DataBinder(null);
		dataBinder.setConversionService(conversionService);

		this.redirectAttributes = new RedirectAttributesModelMap(dataBinder);
	}

	@Test
	public void addAttributePrimitiveType() {
		this.redirectAttributes.addAttribute("speed", 65);
		assertEquals("65", this.redirectAttributes.get("speed"));
	}

	@Test
	public void addAttributeCustomType() {
		String attrName = "person";
		this.redirectAttributes.addAttribute(attrName, new TestBean("Fred"));

		assertEquals("ConversionService should have invoked toString()", "Fred", this.redirectAttributes.get(attrName));

		this.conversionService.addConverter(new TestBeanConverter());
		this.redirectAttributes.addAttribute(attrName, new TestBean("Fred"));

		assertEquals("Type converter should have been used", "[Fred]", this.redirectAttributes.get(attrName));
	}

	@Test
	public void addAttributeToString() {
		String attrName = "person";
		RedirectAttributesModelMap model = new RedirectAttributesModelMap();
		model.addAttribute(attrName, new TestBean("Fred"));

		assertEquals("toString() should have been used", "Fred", model.get(attrName));
	}

	@Test
	public void addAttributeValue() {
		this.redirectAttributes.addAttribute(new TestBean("Fred"));

		assertEquals("Fred", this.redirectAttributes.get("testBean"));
	}

	@Test
	public void addAllAttributesList() {
		this.redirectAttributes.addAllAttributes(Arrays.asList(new TestBean("Fred"), new Integer(5)));

		assertEquals("Fred", this.redirectAttributes.get("testBean"));
		assertEquals("5", this.redirectAttributes.get("integer"));
	}

	@Test
	public void addAttributesMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("person", new TestBean("Fred"));
		map.put("age", 33);
		this.redirectAttributes.addAllAttributes(map);

		assertEquals("Fred", this.redirectAttributes.get("person"));
		assertEquals("33", this.redirectAttributes.get("age"));
	}

	@Test
	public void mergeAttributes() {
		Map<String, Object> map = new HashMap<>();
		map.put("person", new TestBean("Fred"));
		map.put("age", 33);

		this.redirectAttributes.addAttribute("person", new TestBean("Ralph"));
		this.redirectAttributes.mergeAttributes(map);

		assertEquals("Ralph", this.redirectAttributes.get("person"));
		assertEquals("33", this.redirectAttributes.get("age"));
	}

	@Test
	public void put() {
		this.redirectAttributes.put("testBean", new TestBean("Fred"));

		assertEquals("Fred", this.redirectAttributes.get("testBean"));
	}

	@Test
	public void putAll() {
		Map<String, Object> map = new HashMap<>();
		map.put("person", new TestBean("Fred"));
		map.put("age", 33);
		this.redirectAttributes.putAll(map);

		assertEquals("Fred", this.redirectAttributes.get("person"));
		assertEquals("33", this.redirectAttributes.get("age"));
	}

	public static class TestBeanConverter implements Converter<TestBean, String> {

		@Override
		public String convert(TestBean source) {
			return "[" + source.getName() + "]";
		}
	}

}
