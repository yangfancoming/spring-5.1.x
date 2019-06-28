

package org.springframework.beans.factory.support;

import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Rick Evans
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@SuppressWarnings("rawtypes")
public class ManagedPropertiesTests {

	@Test
	public void mergeSunnyDay() {
		ManagedProperties parent = new ManagedProperties();
		parent.setProperty("one", "one");
		parent.setProperty("two", "two");
		ManagedProperties child = new ManagedProperties();
		child.setProperty("three", "three");
		child.setMergeEnabled(true);
		Map mergedMap = (Map) child.merge(parent);
		assertEquals("merge() obviously did not work.", 3, mergedMap.size());
	}

	@Test
	public void mergeWithNullParent() {
		ManagedProperties child = new ManagedProperties();
		child.setMergeEnabled(true);
		assertSame(child, child.merge(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void mergeWithNonCompatibleParentType() {
		ManagedProperties map = new ManagedProperties();
		map.setMergeEnabled(true);
		map.merge("hello");
	}

	@Test(expected = IllegalStateException.class)
	public void mergeNotAllowedWhenMergeNotEnabled() {
		ManagedProperties map = new ManagedProperties();
		map.merge(null);
	}

	@Test
	public void mergeEmptyChild() {
		ManagedProperties parent = new ManagedProperties();
		parent.setProperty("one", "one");
		parent.setProperty("two", "two");
		ManagedProperties child = new ManagedProperties();
		child.setMergeEnabled(true);
		Map mergedMap = (Map) child.merge(parent);
		assertEquals("merge() obviously did not work.", 2, mergedMap.size());
	}

	@Test
	public void mergeChildValuesOverrideTheParents() {
		ManagedProperties parent = new ManagedProperties();
		parent.setProperty("one", "one");
		parent.setProperty("two", "two");
		ManagedProperties child = new ManagedProperties();
		child.setProperty("one", "fork");
		child.setMergeEnabled(true);
		Map mergedMap = (Map) child.merge(parent);
		// child value for 'one' must override parent value...
		assertEquals("merge() obviously did not work.", 2, mergedMap.size());
		assertEquals("Parent value not being overridden during merge().", "fork", mergedMap.get("one"));
	}

}
