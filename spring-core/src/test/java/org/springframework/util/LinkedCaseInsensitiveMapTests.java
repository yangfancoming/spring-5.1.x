

package org.springframework.util;

import org.junit.Test;

import static org.junit.Assert.*;


public class LinkedCaseInsensitiveMapTests {

	private final LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap<>();


	@Test
	public void putAndGet() {
		assertNull(map.put("key", "value1"));
		assertEquals("value1", map.put("key", "value2"));
		assertEquals("value2", map.put("key", "value3"));
		assertEquals(1, map.size());
		assertEquals("value3", map.get("key"));
		assertEquals("value3", map.get("KEY"));
		assertEquals("value3", map.get("Key"));
		assertTrue(map.containsKey("key"));
		assertTrue(map.containsKey("KEY"));
		assertTrue(map.containsKey("Key"));
		assertTrue(map.keySet().contains("key"));
		assertTrue(map.keySet().contains("KEY"));
		assertTrue(map.keySet().contains("Key"));
	}

	@Test
	public void putWithOverlappingKeys() {
		assertNull(map.put("key", "value1"));
		assertEquals("value1", map.put("KEY", "value2"));
		assertEquals("value2", map.put("Key", "value3"));
		assertEquals(1, map.size());
		assertEquals("value3", map.get("key"));
		assertEquals("value3", map.get("KEY"));
		assertEquals("value3", map.get("Key"));
		assertTrue(map.containsKey("key"));
		assertTrue(map.containsKey("KEY"));
		assertTrue(map.containsKey("Key"));
		assertTrue(map.keySet().contains("key"));
		assertTrue(map.keySet().contains("KEY"));
		assertTrue(map.keySet().contains("Key"));
	}

	@Test
	public void getOrDefault() {
		assertNull(map.put("key", "value1"));
		assertEquals("value1", map.put("KEY", "value2"));
		assertEquals("value2", map.put("Key", "value3"));
		assertEquals("value3", map.getOrDefault("key", "N"));
		assertEquals("value3", map.getOrDefault("KEY", "N"));
		assertEquals("value3", map.getOrDefault("Key", "N"));
		assertEquals("N", map.getOrDefault("keeeey", "N"));
		assertEquals("N", map.getOrDefault(new Object(), "N"));
	}

	@Test
	public void getOrDefaultWithNullValue() {
		assertNull(map.put("key", null));
		assertNull(map.put("KEY", null));
		assertNull(map.put("Key", null));
		assertNull(map.getOrDefault("key", "N"));
		assertNull(map.getOrDefault("KEY", "N"));
		assertNull(map.getOrDefault("Key", "N"));
		assertEquals("N", map.getOrDefault("keeeey", "N"));
		assertEquals("N", map.getOrDefault(new Object(), "N"));
	}

	@Test
	public void computeIfAbsentWithExistingValue() {
		assertNull(map.putIfAbsent("key", "value1"));
		assertEquals("value1", map.putIfAbsent("KEY", "value2"));
		assertEquals("value1", map.put("Key", "value3"));
		assertEquals("value3", map.computeIfAbsent("key", key -> "value1"));
		assertEquals("value3", map.computeIfAbsent("KEY", key -> "value2"));
		assertEquals("value3", map.computeIfAbsent("Key", key -> "value3"));
	}

	@Test
	public void computeIfAbsentWithComputedValue() {
		assertEquals("value1", map.computeIfAbsent("key", key -> "value1"));
		assertEquals("value1", map.computeIfAbsent("KEY", key -> "value2"));
		assertEquals("value1", map.computeIfAbsent("Key", key -> "value3"));
	}

	@Test
	public void mapClone() {
		assertNull(map.put("key", "value1"));
		LinkedCaseInsensitiveMap<String> copy = map.clone();

		assertEquals(map.getLocale(), copy.getLocale());
		assertEquals("value1", map.get("key"));
		assertEquals("value1", map.get("KEY"));
		assertEquals("value1", map.get("Key"));
		assertEquals("value1", copy.get("key"));
		assertEquals("value1", copy.get("KEY"));
		assertEquals("value1", copy.get("Key"));

		copy.put("Key", "value2");
		assertEquals(1, map.size());
		assertEquals(1, copy.size());
		assertEquals("value1", map.get("key"));
		assertEquals("value1", map.get("KEY"));
		assertEquals("value1", map.get("Key"));
		assertEquals("value2", copy.get("key"));
		assertEquals("value2", copy.get("KEY"));
		assertEquals("value2", copy.get("Key"));
	}

}
