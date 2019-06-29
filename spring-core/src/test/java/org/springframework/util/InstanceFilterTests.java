

package org.springframework.util;

import org.junit.Test;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

/**
 * @author Stephane Nicoll
 */
public class InstanceFilterTests {

	@Test
	public void emptyFilterApplyMatchIfEmpty() {
		InstanceFilter<String> filter = new InstanceFilter<>(null, null, true);
		match(filter, "foo");
		match(filter, "bar");
	}

	@Test
	public void includesFilter() {
		InstanceFilter<String> filter = new InstanceFilter<>(
				asList("First", "Second"), null, true);
		match(filter, "Second");
		doNotMatch(filter, "foo");
	}

	@Test
	public void excludesFilter() {
		InstanceFilter<String> filter = new InstanceFilter<>(
				null, asList("First", "Second"), true);
		doNotMatch(filter, "Second");
		match(filter, "foo");
	}

	@Test
	public void includesAndExcludesFilters() {
		InstanceFilter<String> filter = new InstanceFilter<>(
				asList("foo", "Bar"), asList("First", "Second"), true);
		doNotMatch(filter, "Second");
		match(filter, "foo");
	}

	@Test
	public void includesAndExcludesFiltersConflict() {
		InstanceFilter<String> filter = new InstanceFilter<>(
				asList("First"), asList("First"), true);
		doNotMatch(filter, "First");
	}

	private <T> void match(InstanceFilter<T> filter, T candidate) {
		assertTrue("filter '" + filter + "' should match " + candidate, filter.match(candidate));
	}

	private <T> void doNotMatch(InstanceFilter<T> filter, T candidate) {
		assertFalse("filter '" + filter + "' should not match " + candidate, filter.match(candidate));
	}

}
