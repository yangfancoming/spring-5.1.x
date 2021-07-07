package org.springframework.core.annotation;
import javax.annotation.Priority;
import org.junit.Test;
import static org.junit.Assert.*;

public class OrderUtilsTests {

	/**
	 * 获取指定类上的@Order注解的值，若没有@Order注解，则获取@Priority注解的值。（有缓存功能）
	*/

	@Test
	public void testGetOrder() {
		// 情况1，优选获取@Order注解的值
		assertEquals(Integer.valueOf(50), OrderUtils.getOrder(SimpleOrder.class, null));
		// 情况2，没有@Order注解，则获取@Priority注解的值
		assertEquals(Integer.valueOf(55), OrderUtils.getOrder(SimplePriority.class, null));
	}

	// 情况3，没有@Order和@Priority注解，则使用默认值
	@Test
	public void getDefaultOrder() {
		assertEquals(23, OrderUtils.getOrder(NoOrder.class, 23));
	}

	// 情况4，既有@Order又有@Priority注解，则使用@Order的值
	@Test
	public void getOrderWithBoth() {
		assertEquals(Integer.valueOf(50), OrderUtils.getOrder(OrderAndPriority.class, null));
	}

	/**
	 * 获取指定类上的@Priority注解的value属性值。
	 */


	// 若指定类上无@Priority注解，则返回null
	@Test
	public void getPriorityValueNoAnnotation() {
		assertNull(OrderUtils.getPriority(SimpleOrder.class));
		assertNull(OrderUtils.getPriority(NoOrder.class));
	}

	// 测试  只获取指定类上的@Priority注解的value属性值。
	@Test
	public void getPriorityValue() {
		assertEquals(Integer.valueOf(55), OrderUtils.getPriority(SimplePriority.class));
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
