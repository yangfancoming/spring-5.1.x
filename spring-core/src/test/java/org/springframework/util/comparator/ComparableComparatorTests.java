

package org.springframework.util.comparator;

import java.util.Comparator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Tests for {@link ComparableComparator}.
 *
 * @author Keith Donald

 * @author Phillip Webb
 */
public class ComparableComparatorTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testComparableComparator() {
		Comparator<String> c = new ComparableComparator<>();
		String s1 = "abc";
		String s2 = "cde";
		assertTrue(c.compare(s1, s2) < 0);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void shouldNeedComparable() {
		Comparator c = new ComparableComparator();
		Object o1 = new Object();
		Object o2 = new Object();
		thrown.expect(ClassCastException.class);
		c.compare(o1, o2);
	}

}
