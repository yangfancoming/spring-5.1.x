

package org.springframework.util;

import java.util.LinkedList;

import org.junit.Test;

import org.springframework.tests.sample.objects.TestObject;

import static org.junit.Assert.*;


public class AutoPopulatingListTests {

	@Test
	public void withClass()  {
		doTestWithClass(new AutoPopulatingList<>(TestObject.class));
	}

	@Test
	public void withClassAndUserSuppliedBackingList()  {
		doTestWithClass(new AutoPopulatingList<>(new LinkedList<>(), TestObject.class));
	}

	@Test
	public void withElementFactory()  {
		doTestWithElementFactory(new AutoPopulatingList<>(new MockElementFactory()));
	}

	@Test
	public void withElementFactoryAndUserSuppliedBackingList()  {
		doTestWithElementFactory(new AutoPopulatingList<>(new LinkedList<>(), new MockElementFactory()));
	}

	private void doTestWithClass(AutoPopulatingList<Object> list) {
		Object lastElement = null;
		for (int x = 0; x < 10; x++) {
			Object element = list.get(x);
			assertNotNull("Element is null", list.get(x));
			assertTrue("Element is incorrect type", element instanceof TestObject);
			assertNotSame(lastElement, element);
			lastElement = element;
		}

		String helloWorld = "Hello World!";
		list.add(10, null);
		list.add(11, helloWorld);
		assertEquals(helloWorld, list.get(11));

		assertTrue(list.get(10) instanceof TestObject);
		assertTrue(list.get(12) instanceof TestObject);
		assertTrue(list.get(13) instanceof TestObject);
		assertTrue(list.get(20) instanceof TestObject);
	}

	private void doTestWithElementFactory(AutoPopulatingList<Object> list) {
		doTestWithClass(list);

		for (int x = 0; x < list.size(); x++) {
			Object element = list.get(x);
			if (element instanceof TestObject) {
				assertEquals(x, ((TestObject) element).getAge());
			}
		}
	}

	@Test
	public void serialization() throws Exception {
		AutoPopulatingList<?> list = new AutoPopulatingList<Object>(TestObject.class);
		assertEquals(list, SerializationTestUtils.serializeAndDeserialize(list));
	}


	private static class MockElementFactory implements AutoPopulatingList.ElementFactory<Object> {

		@Override
		public Object createElement(int index) {
			TestObject bean = new TestObject();
			bean.setAge(index);
			return bean;
		}
	}

}
