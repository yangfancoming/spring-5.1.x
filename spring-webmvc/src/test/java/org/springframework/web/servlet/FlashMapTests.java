

package org.springframework.web.servlet;

import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.Assert.*;

/**
 * Test fixture for {@link FlashMap} tests.
 */
public class FlashMapTests {

	@Test
	public void isExpired() throws InterruptedException {
		assertFalse(new FlashMap().isExpired());
		FlashMap flashMap = new FlashMap();
		flashMap.startExpirationPeriod(0);
		Thread.sleep(100);

		assertTrue(flashMap.isExpired());
	}

	@Test
	public void notExpired() throws InterruptedException {
		FlashMap flashMap = new FlashMap();
		flashMap.startExpirationPeriod(10);
		Thread.sleep(100);

		assertFalse(flashMap.isExpired());
	}

	@Test
	public void compareTo() {
		FlashMap flashMap1 = new FlashMap();
		FlashMap flashMap2 = new FlashMap();
		assertEquals(0, flashMap1.compareTo(flashMap2));

		flashMap1.setTargetRequestPath("/path1");
		assertEquals(-1, flashMap1.compareTo(flashMap2));
		assertEquals(1, flashMap2.compareTo(flashMap1));

		flashMap2.setTargetRequestPath("/path2");
		assertEquals(0, flashMap1.compareTo(flashMap2));

		flashMap1.addTargetRequestParam("id", "1");
		assertEquals(-1, flashMap1.compareTo(flashMap2));
		assertEquals(1, flashMap2.compareTo(flashMap1));

		flashMap2.addTargetRequestParam("id", "2");
		assertEquals(0, flashMap1.compareTo(flashMap2));
	}

	@Test
	public void addTargetRequestParamNullValue() {
		FlashMap flashMap = new FlashMap();
		flashMap.addTargetRequestParam("text", "abc");
		flashMap.addTargetRequestParam("empty", " ");
		flashMap.addTargetRequestParam("null", null);

		assertEquals(1, flashMap.getTargetRequestParams().size());
		assertEquals("abc", flashMap.getTargetRequestParams().getFirst("text"));
	}

	@Test
	public void addTargetRequestParamsNullValue() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("key", "abc");
		params.add("key", " ");
		params.add("key", null);

		FlashMap flashMap = new FlashMap();
		flashMap.addTargetRequestParams(params);

		assertEquals(1, flashMap.getTargetRequestParams().size());
		assertEquals(1, flashMap.getTargetRequestParams().get("key").size());
		assertEquals("abc", flashMap.getTargetRequestParams().getFirst("key"));
	}

	@Test
	public void addTargetRequestParamNullKey() {
		FlashMap flashMap = new FlashMap();
		flashMap.addTargetRequestParam(" ", "abc");
		flashMap.addTargetRequestParam(null, "abc");

		assertTrue(flashMap.getTargetRequestParams().isEmpty());
	}

	@Test
	public void addTargetRequestParamsNullKey() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(" ", "abc");
		params.add(null, " ");

		FlashMap flashMap = new FlashMap();
		flashMap.addTargetRequestParams(params);

		assertTrue(flashMap.getTargetRequestParams().isEmpty());
	}

}
