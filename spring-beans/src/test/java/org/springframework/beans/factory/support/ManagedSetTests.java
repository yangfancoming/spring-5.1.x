

package org.springframework.beans.factory.support;

import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.*;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class ManagedSetTests {

	@Test
	public void mergeSunnyDay() {
		ManagedSet parent = new ManagedSet();
		parent.add("one");
		parent.add("two");
		ManagedSet child = new ManagedSet();
		child.add("three");
		child.setMergeEnabled(true);
		Set mergedSet = child.merge(parent);
		assertEquals("merge() obviously did not work.", 3, mergedSet.size());
	}

	@Test
	public void mergeWithNullParent() {
		ManagedSet child = new ManagedSet();
		child.add("one");
		child.setMergeEnabled(true);
		assertSame(child, child.merge(null));
	}

	@Test(expected = IllegalStateException.class)
	public void mergeNotAllowedWhenMergeNotEnabled() {
		new ManagedSet().merge(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void mergeWithNonCompatibleParentType() {
		ManagedSet child = new ManagedSet();
		child.add("one");
		child.setMergeEnabled(true);
		child.merge("hello");
	}

	@Test
	public void mergeEmptyChild() {
		ManagedSet parent = new ManagedSet();
		parent.add("one");
		parent.add("two");
		ManagedSet child = new ManagedSet();
		child.setMergeEnabled(true);
		Set mergedSet = child.merge(parent);
		assertEquals("merge() obviously did not work.", 2, mergedSet.size());
	}

	@Test
	public void mergeChildValuesOverrideTheParents() {
		// asserts that the set contract is not violated during a merge() operation...
		ManagedSet parent = new ManagedSet();
		parent.add("one");
		parent.add("two");
		ManagedSet child = new ManagedSet();
		child.add("one");
		child.setMergeEnabled(true);
		Set mergedSet = child.merge(parent);
		assertEquals("merge() obviously did not work.", 2, mergedSet.size());
	}

}
