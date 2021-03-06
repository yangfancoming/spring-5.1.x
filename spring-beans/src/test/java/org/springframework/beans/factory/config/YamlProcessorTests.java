

package org.springframework.beans.factory.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.scanner.ScannerException;

import org.springframework.core.io.ByteArrayResource;

import static org.junit.Assert.*;

/**
 * Tests for {@link YamlProcessor}.
 */
public class YamlProcessorTests {

	private final YamlProcessor processor = new YamlProcessor() {};

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public Resource getResource(String string){
		return new ByteArrayResource(string.getBytes());
	}

	@Test
	public void arrayConvertedToIndexedBeanReference() {
		this.processor.setResources(getResource("foo: bar\nbar: [1,2,3]"));
		this.processor.process((properties, map) -> {
			assertEquals(4, properties.size());
			assertEquals("bar", properties.get("foo"));
			assertEquals("bar", properties.getProperty("foo"));
			assertEquals(1, properties.get("bar[0]"));
			assertEquals("1", properties.getProperty("bar[0]"));
			assertEquals(2, properties.get("bar[1]"));
			assertEquals("2", properties.getProperty("bar[1]"));
			assertEquals(3, properties.get("bar[2]"));
			assertEquals("3", properties.getProperty("bar[2]"));
		});
	}

	@Test
	public void testStringResource() {
		this.processor.setResources(getResource("foo # a document that is a literal"));
		this.processor.process((properties, map) -> assertEquals("foo", map.get("document")));
	}

	@Test
	public void testBadDocumentStart() {
		this.processor.setResources(getResource("foo # a document\nbar: baz"));
		this.exception.expect(ParserException.class);
		this.exception.expectMessage("line 2, column 1");
		this.processor.process((properties, map) -> {});
	}

	@Test
	public void testBadResource() {
		this.processor.setResources(getResource("foo: bar\ncd\nspam:\n  foo: baz"));
		this.exception.expect(ScannerException.class);
		this.exception.expectMessage("line 3, column 1");
		this.processor.process((properties, map) -> {});
	}

	@Test
	public void mapConvertedToIndexedBeanReference() {
		this.processor.setResources(getResource("foo: bar\nbar:\n spam: bucket"));
		this.processor.process((properties, map) -> {
			assertEquals("bucket", properties.get("bar.spam"));
			assertEquals(2, properties.size());
		});
	}

	@Test
	public void integerKeyBehaves() {
		this.processor.setResources(getResource("foo: bar\n1: bar"));
		this.processor.process((properties, map) -> {
			assertEquals("bar", properties.get("[1]"));
			assertEquals(2, properties.size());
		});
	}

	@Test
	public void integerDeepKeyBehaves() {
		this.processor.setResources(getResource("foo:\n  1: bar"));
		this.processor.process((properties, map) -> {
			assertEquals("bar", properties.get("foo[1]"));
			assertEquals(1, properties.size());
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void flattenedMapIsSameAsPropertiesButOrdered() {
		this.processor.setResources(getResource("foo: bar\nbar:\n spam: bucket"));
		this.processor.process((properties, map) -> {
			assertEquals("bucket", properties.get("bar.spam"));
			assertEquals(2, properties.size());
			Map<String, Object> flattenedMap = processor.getFlattenedMap(map);
			assertEquals("bucket", flattenedMap.get("bar.spam"));
			assertEquals(2, flattenedMap.size());
			assertTrue(flattenedMap instanceof LinkedHashMap);
			Map<String, Object> bar = (Map<String, Object>) map.get("bar");
			assertEquals("bucket", bar.get("spam"));
		});
	}

}
