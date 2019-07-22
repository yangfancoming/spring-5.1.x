

package org.springframework.transaction.interceptor;

import java.io.IOException;

import org.junit.Test;

import org.springframework.beans.FatalBeanException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link RollbackRuleAttribute} class.
 * @since 09.04.2003
 */
public class RollbackRuleTests {

	@Test
	public void foundImmediatelyWithString() {
		RollbackRuleAttribute rr = new RollbackRuleAttribute(java.lang.Exception.class.getName());
		assertEquals(0, rr.getDepth(new Exception()));
	}

	@Test
	public void foundImmediatelyWithClass() {
		RollbackRuleAttribute rr = new RollbackRuleAttribute(Exception.class);
		assertEquals(0, rr.getDepth(new Exception()));
	}

	@Test
	public void notFound() {
		RollbackRuleAttribute rr = new RollbackRuleAttribute(java.io.IOException.class.getName());
		assertEquals(-1, rr.getDepth(new MyRuntimeException("")));
	}

	@Test
	public void ancestry() {
		RollbackRuleAttribute rr = new RollbackRuleAttribute(java.lang.Exception.class.getName());
		// Exception -> Runtime -> NestedRuntime -> MyRuntimeException
		assertThat(rr.getDepth(new MyRuntimeException("")), equalTo(3));
	}

	@Test
	public void alwaysTrueForThrowable() {
		RollbackRuleAttribute rr = new RollbackRuleAttribute(java.lang.Throwable.class.getName());
		assertTrue(rr.getDepth(new MyRuntimeException("")) > 0);
		assertTrue(rr.getDepth(new IOException()) > 0);
		assertTrue(rr.getDepth(new FatalBeanException(null,null)) > 0);
		assertTrue(rr.getDepth(new RuntimeException()) > 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorArgMustBeAThrowableClassWithNonThrowableType() {
		new RollbackRuleAttribute(StringBuffer.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorArgMustBeAThrowableClassWithNullThrowableType() {
		new RollbackRuleAttribute((Class<?>) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorArgExceptionStringNameVersionWithNull() {
		new RollbackRuleAttribute((String) null);
	}

}
