

package org.springframework.test.context.junit4.statements;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.model.Statement;
import org.mockito.stubbing.Answer;

import org.springframework.test.context.junit4.statements.SpringFailOnTimeout;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SpringFailOnTimeout}.
 *
 * @author Igor Suhorukov
 * @author Sam Brannen
 * @since 4.3.17
 */
public class SpringFailOnTimeoutTests {

	private Statement statement = mock(Statement.class);

	@Rule
	public final ExpectedException exception = ExpectedException.none();


	@Test
	public void nullNextStatement() throws Throwable {
		exception.expect(IllegalArgumentException.class);
		new SpringFailOnTimeout(null, 1);
	}

	@Test
	public void negativeTimeout() throws Throwable {
		exception.expect(IllegalArgumentException.class);
		new SpringFailOnTimeout(statement, -1);
	}

	@Test
	public void userExceptionPropagates() throws Throwable {
		doThrow(new Boom()).when(statement).evaluate();

		exception.expect(Boom.class);
		new SpringFailOnTimeout(statement, 1).evaluate();
	}

	@Test
	public void timeoutExceptionThrownIfNoUserException() throws Throwable {
		doAnswer((Answer<Void>) invocation -> {
			TimeUnit.MILLISECONDS.sleep(50);
			return null;
		}).when(statement).evaluate();

		exception.expect(TimeoutException.class);
		new SpringFailOnTimeout(statement, 1).evaluate();
	}

	@Test
	public void noExceptionThrownIfNoUserExceptionAndTimeoutDoesNotOccur() throws Throwable {
		doAnswer((Answer<Void>) invocation -> {
			return null;
		}).when(statement).evaluate();

		new SpringFailOnTimeout(statement, 100).evaluate();
	}

	@SuppressWarnings("serial")
	private static class Boom extends RuntimeException {
	}

}
