

package org.springframework.remoting.support;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Rick Evans
 */
public class RemoteInvocationUtilsTests {

	@Test
	public void fillInClientStackTraceIfPossibleSunnyDay() throws Exception {
		try {
			throw new IllegalStateException("Mmm");
		}
		catch (Exception ex) {
			int originalStackTraceLngth = ex.getStackTrace().length;
			RemoteInvocationUtils.fillInClientStackTraceIfPossible(ex);
			assertTrue("Stack trace not being filled in",
					ex.getStackTrace().length > originalStackTraceLngth);
		}
	}

	@Test
	public void fillInClientStackTraceIfPossibleWithNullThrowable() throws Exception {
		// just want to ensure that it doesn't bomb
		RemoteInvocationUtils.fillInClientStackTraceIfPossible(null);
	}

}
