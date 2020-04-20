
package org.springframework.core.annotation;
import javax.annotation.Priority;
import org.junit.Test;
import static org.junit.Assert.*;

public class OrderUtilsTests {

	@Test
	public void getSimpleOrder() {
		assertEquals(Integer.valueOf(50), OrderUtils.getOrder(SimpleOrder.class, null));
		assertEquals(Integer.valueOf(50), OrderUtils.getOrder(SimpleOrder.class, null));
	}

	@Test
	public void getPriorityOrder() {
		assertEquals(Integer.valueOf(55), OrderUtils.getOrder(SimplePriority.class, null));
		assertEquals(Integer.valueOf(55), OrderUtils.getOrder(SimplePriority.class, null));
	}

	@Test
	public void getOrderWithBoth() {
		assertEquals(Integer.valueOf(50), OrderUtils.getOrder(OrderAndPriority.class, null));
		assertEquals(Integer.valueOf(50), OrderUtils.getOrder(OrderAndPriority.class, null));
	}

	@Test
	public void getDefaultOrder() {
		assertEquals(33, OrderUtils.getOrder(NoOrder.class, 33));
		assertEquals(33, OrderUtils.getOrder(NoOrder.class, 33));
	}

	@Test
	public void getPriorityValueNoAnnotation() {
		assertNull(OrderUtils.getPriority(SimpleOrder.class));
		assertNull(OrderUtils.getPriority(SimpleOrder.class));
	}

	@Test
	public void getPriorityValue() {
		assertEquals(Integer.valueOf(55), OrderUtils.getPriority(OrderAndPriority.class));
		assertEquals(Integer.valueOf(55), OrderUtils.getPriority(OrderAndPriority.class));
	}


	@Order(50)
	private static class SimpleOrder {}

	@Priority(55)
	private static class SimplePriority {}

	@Order(50)
	@Priority(55)
	private static class OrderAndPriority {}

	private static class NoOrder {}

}
