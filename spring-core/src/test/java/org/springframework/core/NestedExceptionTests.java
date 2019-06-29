

package org.springframework.core;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
@SuppressWarnings("serial")
public class NestedExceptionTests {

	@Test
	public void nestedRuntimeExceptionWithNoRootCause() {
		String mesg = "mesg of mine";
		// Making a class abstract doesn't _really_ prevent instantiation :-)
		NestedRuntimeException nex = new NestedRuntimeException(mesg) {};
		assertNull(nex.getCause());
		assertEquals(nex.getMessage(), mesg);

		// Check printStackTrace
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(baos);
		nex.printStackTrace(pw);
		pw.flush();
		String stackTrace = new String(baos.toByteArray());
		assertTrue(stackTrace.contains(mesg));
	}

	@Test
	public void nestedRuntimeExceptionWithRootCause() {
		String myMessage = "mesg for this exception";
		String rootCauseMsg = "this is the obscure message of the root cause";
		Exception rootCause = new Exception(rootCauseMsg);
		// Making a class abstract doesn't _really_ prevent instantiation :-)
		NestedRuntimeException nex = new NestedRuntimeException(myMessage, rootCause) {};
		assertEquals(nex.getCause(), rootCause);
		assertTrue(nex.getMessage().contains(myMessage));
		assertTrue(nex.getMessage().endsWith(rootCauseMsg));

		// check PrintStackTrace
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(baos);
		nex.printStackTrace(pw);
		pw.flush();
		String stackTrace = new String(baos.toByteArray());
		assertTrue(stackTrace.contains(rootCause.getClass().getName()));
		assertTrue(stackTrace.contains(rootCauseMsg));
	}

	@Test
	public void nestedCheckedExceptionWithNoRootCause() {
		String mesg = "mesg of mine";
		// Making a class abstract doesn't _really_ prevent instantiation :-)
		NestedCheckedException nex = new NestedCheckedException(mesg) {};
		assertNull(nex.getCause());
		assertEquals(nex.getMessage(), mesg);

		// Check printStackTrace
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(baos);
		nex.printStackTrace(pw);
		pw.flush();
		String stackTrace = new String(baos.toByteArray());
		assertTrue(stackTrace.contains(mesg));
	}

	@Test
	public void nestedCheckedExceptionWithRootCause() {
		String myMessage = "mesg for this exception";
		String rootCauseMsg = "this is the obscure message of the root cause";
		Exception rootCause = new Exception(rootCauseMsg);
		// Making a class abstract doesn't _really_ prevent instantiation :-)
		NestedCheckedException nex = new NestedCheckedException(myMessage, rootCause) {};
		assertEquals(nex.getCause(), rootCause);
		assertTrue(nex.getMessage().contains(myMessage));
		assertTrue(nex.getMessage().endsWith(rootCauseMsg));

		// check PrintStackTrace
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(baos);
		nex.printStackTrace(pw);
		pw.flush();
		String stackTrace = new String(baos.toByteArray());
		assertTrue(stackTrace.contains(rootCause.getClass().getName()));
		assertTrue(stackTrace.contains(rootCauseMsg));
	}

}
