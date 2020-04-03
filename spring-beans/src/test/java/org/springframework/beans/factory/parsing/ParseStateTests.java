

package org.springframework.beans.factory.parsing;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 2.0
 */
public class ParseStateTests {

	@Test
	public void testSimple()  {
		MockEntry entry = new MockEntry();

		ParseState parseState = new ParseState();
		parseState.push(entry);
		assertEquals("Incorrect peek value.", entry, parseState.peek());
		parseState.pop();
		assertNull("Should get null on peek()", parseState.peek());
	}

	@Test
	public void testNesting()  {
		MockEntry one = new MockEntry();
		MockEntry two = new MockEntry();
		MockEntry three = new MockEntry();

		ParseState parseState = new ParseState();
		parseState.push(one);
		assertEquals(one, parseState.peek());
		parseState.push(two);
		assertEquals(two, parseState.peek());
		parseState.push(three);
		assertEquals(three, parseState.peek());

		parseState.pop();
		assertEquals(two, parseState.peek());
		parseState.pop();
		assertEquals(one, parseState.peek());
	}

	@Test
	public void testSnapshot()  {
		MockEntry entry = new MockEntry();
		ParseState original = new ParseState();
		original.push(entry);
		ParseState snapshot = original.snapshot();
		original.push(new MockEntry());
		assertEquals("Snapshot should not have been modified.", entry, snapshot.peek());
	}

	private static class MockEntry implements ParseState.Entry {}

}
