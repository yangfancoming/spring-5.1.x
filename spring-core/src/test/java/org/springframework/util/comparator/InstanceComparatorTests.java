

package org.springframework.util.comparator;

import java.util.Comparator;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Tests for {@link InstanceComparator}.
 *
 * @author Phillip Webb
 */
public class InstanceComparatorTests {

	private C1 c1 = new C1();

	private C2 c2 = new C2();

	private C3 c3 = new C3();

	private C4 c4 = new C4();

	@Test
	public void shouldCompareClasses() throws Exception {
		Comparator<Object> comparator = new InstanceComparator<>(C1.class, C2.class);
		assertThat(comparator.compare(c1, c1), is(0));
		assertThat(comparator.compare(c1, c2), is(-1));
		assertThat(comparator.compare(c2, c1), is(1));
		assertThat(comparator.compare(c2, c3), is(-1));
		assertThat(comparator.compare(c2, c4), is(-1));
		assertThat(comparator.compare(c3, c4), is(0));
	}

	@Test
	public void shouldCompareInterfaces() throws Exception {
		Comparator<Object> comparator = new InstanceComparator<>(I1.class, I2.class);
		assertThat(comparator.compare(c1, c1), is(0));
		assertThat(comparator.compare(c1, c2), is(0));
		assertThat(comparator.compare(c2, c1), is(0));
		assertThat(comparator.compare(c1, c3), is(-1));
		assertThat(comparator.compare(c3, c1), is(1));
		assertThat(comparator.compare(c3, c4), is(0));
	}

	@Test
	public void shouldCompareMix() throws Exception {
		Comparator<Object> comparator = new InstanceComparator<>(I1.class, C3.class);
		assertThat(comparator.compare(c1, c1), is(0));
		assertThat(comparator.compare(c3, c4), is(-1));
		assertThat(comparator.compare(c3, null), is(-1));
		assertThat(comparator.compare(c4, null), is(0));
	}

	private static interface I1 {

	}

	private static interface I2 {

	}

	private static class C1 implements I1 {
	}

	private static class C2 implements I1 {
	}

	private static class C3 implements I2 {
	}

	private static class C4 implements I2 {
	}

}
