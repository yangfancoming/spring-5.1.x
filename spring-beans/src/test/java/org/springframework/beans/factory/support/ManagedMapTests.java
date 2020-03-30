

package org.springframework.beans.factory.support;

import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class ManagedMapTests {

	@Test
	public void mergeSunnyDay() {
		ManagedMap parent = new ManagedMap();
		parent.put("one", "one");
		parent.put("two", "two");
		ManagedMap child = new ManagedMap();
		child.put("three", "three");
		child.setMergeEnabled(true);
		Map mergedMap = (Map) child.merge(parent);
		assertEquals("merge() obviously did not work.", 3, mergedMap.size());
	}

	@Test
	public void mergeWithNullParent() {
		ManagedMap child = new ManagedMap();
		child.setMergeEnabled(true);
		assertSame(child, child.merge(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void mergeWithNonCompatibleParentType() {
		ManagedMap map = new ManagedMap();
		map.setMergeEnabled(true);
		map.merge("hello");
	}

	@Test(expected = IllegalStateException.class)
	public void mergeNotAllowedWhenMergeNotEnabled() {
		new ManagedMap().merge(null);
	}

	@Test
	public void mergeEmptyChild() {
		ManagedMap parent = new ManagedMap();
		parent.put("one", "one");
		parent.put("two", "two");
		ManagedMap child = new ManagedMap();
		child.setMergeEnabled(true);
		Map mergedMap = (Map) child.merge(parent);
		assertEquals("merge() obviously did not work.", 2, mergedMap.size());
	}

	@Test
	public void mergeChildValuesOverrideTheParents() {
		ManagedMap parent = new ManagedMap();
		parent.put("one", "one");
		parent.put("two", "two");
		ManagedMap child = new ManagedMap();
		child.put("one", "fork");
		child.setMergeEnabled(true);
		Map mergedMap = (Map) child.merge(parent);
		// child value for 'one' must override parent value...
		assertEquals("merge() obviously did not work.", 2, mergedMap.size());
		assertEquals("Parent value not being overridden during merge().", "fork", mergedMap.get("one"));
	}

}
