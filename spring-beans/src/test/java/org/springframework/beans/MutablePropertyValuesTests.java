

package org.springframework.beans;

import java.util.Iterator;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Tests for {@link MutablePropertyValues}.
 */
public class MutablePropertyValuesTests extends AbstractPropertyValuesTests {

	// 测试 MutablePropertyValues 的普通功能有效性
	@Test
	public void testValid() {
		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(new PropertyValue("forname", "Tony"));
		pvs.addPropertyValue(new PropertyValue("surname", "Blair"));
		pvs.addPropertyValue(new PropertyValue("age", "50"));
		doTestTony(pvs);

		MutablePropertyValues deepCopy = new MutablePropertyValues(pvs);
		doTestTony(deepCopy);
		// deepCopy中 替换0号位置的对象，即 "forname", "Tony" 被替换为 "name", "Gordon"
		deepCopy.setPropertyValueAt(new PropertyValue("name", "Gordon"), 0);
		// deepCopy 修改后 不影响 pvs 中的值
		doTestTony(pvs);
		assertNotSame(pvs,deepCopy);
		assertEquals("Gordon", deepCopy.getPropertyValue("name").getValue());
	}

	// 测试 添加 和 覆盖 属性
	@Test
	public void testAddOrOverride() {
		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(new PropertyValue("forname", "Tony"));
		pvs.addPropertyValue(new PropertyValue("surname", "Blair"));
		pvs.addPropertyValue(new PropertyValue("age", "50"));
		doTestTony(pvs);
		// 测试 添加属性
		PropertyValue addedPv = new PropertyValue("rod", "Rod");
		pvs.addPropertyValue(addedPv);
		assertTrue(pvs.getPropertyValue("rod").equals(addedPv));

		// 测试 覆盖属性  将原来的 "Tony" 覆盖成 "Greg"
		PropertyValue changedPv = new PropertyValue("forname", "Greg");
		pvs.addPropertyValue(changedPv);
		assertTrue(pvs.getPropertyValue("forname").equals(changedPv));
	}

	// 测试 changesSince 方法   再未更改的情况下
	@Test
	public void testChangesOnEquals() {
		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(new PropertyValue("forname", "Tony"));
		pvs.addPropertyValue(new PropertyValue("surname", "Blair"));
		pvs.addPropertyValue(new PropertyValue("age", "50"));
		MutablePropertyValues pvs2 = pvs;
		PropertyValues changes = pvs2.changesSince(pvs);
		assertTrue("changes are empty", changes.getPropertyValues().length == 0);
	}

	//  测试 changesSince 方法   再有更改的情况下
	@Test
	public void testChangeOfOneField() {
		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(new PropertyValue("forname", "Tony"));
		pvs.addPropertyValue(new PropertyValue("surname", "Blair"));
		pvs.addPropertyValue(new PropertyValue("age", "50"));

		MutablePropertyValues pvs2 = new MutablePropertyValues(pvs);
		PropertyValues changes = pvs2.changesSince(pvs);
		assertTrue("changes are empty, not of length " + changes.getPropertyValues().length,changes.getPropertyValues().length == 0);

		// 发生变化  添加了1个属性
		pvs2.addPropertyValue(new PropertyValue("forname", "Gordon"));
		changes = pvs2.changesSince(pvs);
		assertEquals("1 change", 1, changes.getPropertyValues().length);
		PropertyValue fn = changes.getPropertyValue("forname");
		assertTrue("change is forname", fn != null);
		assertTrue("new value is gordon", fn.getValue().equals("Gordon"));

		MutablePropertyValues pvs3 = new MutablePropertyValues(pvs);
		changes = pvs3.changesSince(pvs);
		assertTrue("changes are empty, not of length " + changes.getPropertyValues().length,changes.getPropertyValues().length == 0);

		// add new 	// 发生变化  添加了2个属性
		pvs3.addPropertyValue(new PropertyValue("foo", "bar"));
		pvs3.addPropertyValue(new PropertyValue("fi", "fum"));
		changes = pvs3.changesSince(pvs);
		assertTrue("2 change", changes.getPropertyValues().length == 2);
		fn = changes.getPropertyValue("foo");
		assertTrue("change in foo", fn != null);
		assertTrue("new value is bar", fn.getValue().equals("bar"));
	}

	// 测试 迭代功能
	@Test
	public void iteratorContainsPropertyValue() {
		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.add("foo", "bar");

		Iterator<PropertyValue> it = pvs.iterator();
		assertTrue(it.hasNext());
		PropertyValue pv = it.next();
		assertEquals("foo", pv.getName());
		assertEquals("bar", pv.getValue());

		try {
			it.remove();
			fail("Should have thrown UnsupportedOperationException");
		}
		catch (UnsupportedOperationException ex) {
			// expected
			System.out.println(ex);
		}
		assertFalse(it.hasNext());
	}

	// 测试 数据为空时   迭代的情况
	@Test
	public void iteratorIsEmptyForEmptyValues() {
		MutablePropertyValues pvs = new MutablePropertyValues();
		Iterator<PropertyValue> it = pvs.iterator();
		assertFalse(it.hasNext());
	}

	// 测试 MutablePropertyValues 转 stream 后有数据情况下的功能操作
	@Test
	public void streamContainsPropertyValue() {
		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.add("foo", "bar");

		assertThat(pvs.stream(), notNullValue());
		assertThat(pvs.stream().count(), is(1L));
		assertThat(pvs.stream().anyMatch(pv -> "foo".equals(pv.getName()) && "bar".equals(pv.getValue())), is(true));
		assertThat(pvs.stream().anyMatch(pv -> "bar".equals(pv.getName()) && "foo".equals(pv.getValue())), is(false));
	}

	// 测试 MutablePropertyValues 转 stream 后数据为空时的功能操作
	@Test
	public void streamIsEmptyForEmptyValues() {
		MutablePropertyValues pvs = new MutablePropertyValues();
		assertThat(pvs.stream(), notNullValue());
		assertThat(pvs.stream().count(), is(0L));
	}
}
