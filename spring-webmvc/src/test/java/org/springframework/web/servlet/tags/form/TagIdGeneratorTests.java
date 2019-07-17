

package org.springframework.web.servlet.tags.form;

import java.util.stream.IntStream;

import javax.servlet.jsp.PageContext;

import org.junit.Test;

import org.springframework.mock.web.test.MockPageContext;

import static org.junit.Assert.*;


public class TagIdGeneratorTests {

	@Test
	public void nextId() {
		// Repeat a few times just to be sure...
		IntStream.rangeClosed(1, 5).forEach(i -> assertNextId());
	}

	private void assertNextId() {
		PageContext pageContext = new MockPageContext();
		assertEquals("foo1", TagIdGenerator.nextId("foo", pageContext));
		assertEquals("foo2", TagIdGenerator.nextId("foo", pageContext));
		assertEquals("foo3", TagIdGenerator.nextId("foo", pageContext));
		assertEquals("foo4", TagIdGenerator.nextId("foo", pageContext));
		assertEquals("bar1", TagIdGenerator.nextId("bar", pageContext));
	}

}
