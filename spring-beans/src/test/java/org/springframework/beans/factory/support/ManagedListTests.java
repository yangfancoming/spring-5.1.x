

package org.springframework.beans.factory.support;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Rick Evans
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ManagedListTests {

	@Test
	public void mergeSunnyDay() {
		ManagedList parent = new ManagedList();
		parent.add("one");
		parent.add("two");
		ManagedList child = new ManagedList();
		child.add("three");
		child.setMergeEnabled(true);
		List mergedList = child.merge(parent);
		assertEquals("merge() obviously did not work.", 3, mergedList.size());
	}

	@Test
	public void mergeWithNullParent() {
		ManagedList child = new ManagedList();
		child.add("one");
		child.setMergeEnabled(true);
		assertSame(child, child.merge(null));
	}

	@Test(expected = IllegalStateException.class)
	public void mergeNotAllowedWhenMergeNotEnabled() {
		ManagedList child = new ManagedList();
		child.merge(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void mergeWithNonCompatibleParentType() {
		ManagedList child = new ManagedList();
		child.add("one");
		child.setMergeEnabled(true);
		child.merge("hello");
	}

	@Test
	public void mergeEmptyChild() {
		ManagedList parent = new ManagedList();
		parent.add("one");
		parent.add("two");
		ManagedList child = new ManagedList();
		child.setMergeEnabled(true);
		List mergedList = child.merge(parent);
		assertEquals("merge() obviously did not work.", 2, mergedList.size());
	}

	@Test
	public void mergeChildValuesOverrideTheParents() {
		// doesn't make much sense in the context of a list...
		ManagedList parent = new ManagedList();
		parent.add("one");
		parent.add("two");
		ManagedList child = new ManagedList();
		child.add("one");
		child.setMergeEnabled(true);
		List mergedList = child.merge(parent);
		assertEquals("merge() obviously did not work.", 3, mergedList.size());
	}

}
