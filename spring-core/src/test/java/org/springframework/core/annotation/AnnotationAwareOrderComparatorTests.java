

package org.springframework.core.annotation;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Priority;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author Juergen Hoeller
 * @author Oliver Gierke
 */
public class AnnotationAwareOrderComparatorTests {

	@Test
	public void instanceVariableIsAnAnnotationAwareOrderComparator() {
		assertThat(AnnotationAwareOrderComparator.INSTANCE, is(instanceOf(AnnotationAwareOrderComparator.class)));
	}

	@Test
	public void sortInstances() {
		List<Object> list = new ArrayList<>();
		list.add(new B());
		list.add(new A());
		AnnotationAwareOrderComparator.sort(list);
		assertTrue(list.get(0) instanceof A);
		assertTrue(list.get(1) instanceof B);
	}

	@Test
	public void sortInstancesWithPriority() {
		List<Object> list = new ArrayList<>();
		list.add(new B2());
		list.add(new A2());
		AnnotationAwareOrderComparator.sort(list);
		assertTrue(list.get(0) instanceof A2);
		assertTrue(list.get(1) instanceof B2);
	}

	@Test
	public void sortInstancesWithOrderAndPriority() {
		List<Object> list = new ArrayList<>();
		list.add(new B());
		list.add(new A2());
		AnnotationAwareOrderComparator.sort(list);
		assertTrue(list.get(0) instanceof A2);
		assertTrue(list.get(1) instanceof B);
	}

	@Test
	public void sortInstancesWithSubclass() {
		List<Object> list = new ArrayList<>();
		list.add(new B());
		list.add(new C());
		AnnotationAwareOrderComparator.sort(list);
		assertTrue(list.get(0) instanceof C);
		assertTrue(list.get(1) instanceof B);
	}

	@Test
	public void sortClasses() {
		List<Object> list = new ArrayList<>();
		list.add(B.class);
		list.add(A.class);
		AnnotationAwareOrderComparator.sort(list);
		assertEquals(A.class, list.get(0));
		assertEquals(B.class, list.get(1));
	}

	@Test
	public void sortClassesWithSubclass() {
		List<Object> list = new ArrayList<>();
		list.add(B.class);
		list.add(C.class);
		AnnotationAwareOrderComparator.sort(list);
		assertEquals(C.class, list.get(0));
		assertEquals(B.class, list.get(1));
	}

	@Test
	public void sortWithNulls() {
		List<Object> list = new ArrayList<>();
		list.add(null);
		list.add(B.class);
		list.add(null);
		list.add(A.class);
		AnnotationAwareOrderComparator.sort(list);
		assertEquals(A.class, list.get(0));
		assertEquals(B.class, list.get(1));
		assertNull(list.get(2));
		assertNull(list.get(3));
	}


	@Order(1)
	private static class A {
	}

	@Order(2)
	private static class B {
	}

	private static class C extends A {
	}

	@Priority(1)
	private static class A2 {
	}

	@Priority(2)
	private static class B2 {
	}

}
