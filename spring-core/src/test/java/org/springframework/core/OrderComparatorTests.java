

package org.springframework.core;

import java.util.Comparator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link OrderComparator} class.
 */
public class OrderComparatorTests {

	private final OrderComparator comparator = new OrderComparator();


	@Test
	public void compareOrderedInstancesBefore() {
		assertEquals(-1, this.comparator.compare(new StubOrdered(100), new StubOrdered(2000)));
	}

	@Test
	public void compareOrderedInstancesSame() {
		assertEquals(0, this.comparator.compare(new StubOrdered(100), new StubOrdered(100)));
	}

	@Test
	public void compareOrderedInstancesAfter() {
		assertEquals(1, this.comparator.compare(new StubOrdered(982300), new StubOrdered(100)));
	}

	@Test
	public void compareOrderedInstancesNullFirst() {
		assertEquals(1, this.comparator.compare(null, new StubOrdered(100)));
	}

	@Test
	public void compareOrderedInstancesNullLast() {
		assertEquals(-1, this.comparator.compare(new StubOrdered(100), null));
	}

	@Test
	public void compareOrderedInstancesDoubleNull() {
		assertEquals(0, this.comparator.compare(null, null));
	}

	@Test
	public void compareTwoNonOrderedInstancesEndsUpAsSame() {
		assertEquals(0, this.comparator.compare(new Object(), new Object()));
	}

	@Test
	public void compareWithSimpleSourceProvider() {
		Comparator<Object> customComparator = this.comparator.withSourceProvider(
				new TestSourceProvider(5L, new StubOrdered(25)));
		assertEquals(-1, customComparator.compare(new StubOrdered(10), 5L));
	}

	@Test
	public void compareWithSourceProviderArray() {
		Comparator<Object> customComparator = this.comparator.withSourceProvider(
				new TestSourceProvider(5L, new Object[] {new StubOrdered(10), new StubOrdered(-25)}));
		assertEquals(-1, customComparator.compare(5L, new Object()));
	}

	@Test
	public void compareWithSourceProviderArrayNoMatch() {
		Comparator<Object> customComparator = this.comparator.withSourceProvider(
				new TestSourceProvider(5L, new Object[]{new Object(), new Object()}));
		assertEquals(0, customComparator.compare(new Object(), 5L));
	}

	@Test
	public void compareWithSourceProviderEmpty() {
		Comparator<Object> customComparator = this.comparator.withSourceProvider(
				new TestSourceProvider(50L, new Object()));
		assertEquals(0, customComparator.compare(new Object(), 5L));
	}


	private static final class TestSourceProvider implements OrderComparator.OrderSourceProvider {

		private final Object target;

		private final Object orderSource;

		public TestSourceProvider(Object target, Object orderSource) {
			this.target = target;
			this.orderSource = orderSource;
		}

		@Override
		public Object getOrderSource(Object obj) {
			if (target.equals(obj)) {
				return orderSource;
			}
			return null;
		}
	}


	private static final class StubOrdered implements Ordered {

		private final int order;


		public StubOrdered(int order) {
			this.order = order;
		}

		@Override
		public int getOrder() {
			return this.order;
		}
	}

}
