

package org.springframework.core;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;


@SuppressWarnings("unchecked")
public class ExceptionDepthComparatorTests {

	@Test
	public void targetBeforeSameDepth() throws Exception {
		Class<? extends Throwable> foundClass = findClosestMatch(TargetException.class, SameDepthException.class);
		assertEquals(TargetException.class, foundClass);
	}

	@Test
	public void sameDepthBeforeTarget() throws Exception {
		Class<? extends Throwable> foundClass = findClosestMatch(SameDepthException.class, TargetException.class);
		assertEquals(TargetException.class, foundClass);
	}

	@Test
	public void lowestDepthBeforeTarget() throws Exception {
		Class<? extends Throwable> foundClass = findClosestMatch(LowestDepthException.class, TargetException.class);
		assertEquals(TargetException.class, foundClass);
	}

	@Test
	public void targetBeforeLowestDepth() throws Exception {
		Class<? extends Throwable> foundClass = findClosestMatch(TargetException.class, LowestDepthException.class);
		assertEquals(TargetException.class, foundClass);
	}

	@Test
	public void noDepthBeforeTarget() throws Exception {
		Class<? extends Throwable> foundClass = findClosestMatch(NoDepthException.class, TargetException.class);
		assertEquals(TargetException.class, foundClass);
	}

	@Test
	public void noDepthBeforeHighestDepth() throws Exception {
		Class<? extends Throwable> foundClass = findClosestMatch(NoDepthException.class, HighestDepthException.class);
		assertEquals(HighestDepthException.class, foundClass);
	}

	@Test
	public void highestDepthBeforeNoDepth() throws Exception {
		Class<? extends Throwable> foundClass = findClosestMatch(HighestDepthException.class, NoDepthException.class);
		assertEquals(HighestDepthException.class, foundClass);
	}

	@Test
	public void highestDepthBeforeLowestDepth() throws Exception {
		Class<? extends Throwable> foundClass = findClosestMatch(HighestDepthException.class, LowestDepthException.class);
		assertEquals(LowestDepthException.class, foundClass);
	}

	@Test
	public void lowestDepthBeforeHighestDepth() throws Exception {
		Class<? extends Throwable> foundClass = findClosestMatch(LowestDepthException.class, HighestDepthException.class);
		assertEquals(LowestDepthException.class, foundClass);
	}

	private Class<? extends Throwable> findClosestMatch(
			Class<? extends Throwable>... classes) {
		return ExceptionDepthComparator.findClosestMatch(Arrays.asList(classes), new TargetException());
	}

	@SuppressWarnings("serial")
	public class HighestDepthException extends Throwable {
	}

	@SuppressWarnings("serial")
	public class LowestDepthException extends HighestDepthException {
	}

	@SuppressWarnings("serial")
	public class TargetException extends LowestDepthException {
	}

	@SuppressWarnings("serial")
	public class SameDepthException extends LowestDepthException {
	}

	@SuppressWarnings("serial")
	public class NoDepthException extends TargetException {
	}

}
