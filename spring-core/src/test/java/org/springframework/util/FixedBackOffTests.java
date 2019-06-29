

package org.springframework.util;

import org.junit.Test;

import org.springframework.util.backoff.BackOffExecution;
import org.springframework.util.backoff.FixedBackOff;

import static org.junit.Assert.*;

/**
 * @author Stephane Nicoll
 */
public class FixedBackOffTests {

	@Test
	public void defaultInstance() {
		FixedBackOff backOff = new FixedBackOff();
		BackOffExecution execution = backOff.start();
		for (int i = 0; i < 100; i++) {
			assertEquals(FixedBackOff.DEFAULT_INTERVAL, execution.nextBackOff());
		}
	}

	@Test
	public void noAttemptAtAll() {
		FixedBackOff backOff = new FixedBackOff(100L, 0L);
		BackOffExecution execution = backOff.start();
		assertEquals(BackOffExecution.STOP, execution.nextBackOff());
	}

	@Test
	public void maxAttemptsReached() {
		FixedBackOff backOff = new FixedBackOff(200L, 2);
		BackOffExecution execution = backOff.start();
		assertEquals(200L, execution.nextBackOff());
		assertEquals(200L, execution.nextBackOff());
		assertEquals(BackOffExecution.STOP, execution.nextBackOff());
	}

	@Test
	public void startReturnDifferentInstances() {
		FixedBackOff backOff = new FixedBackOff(100L, 1);
		BackOffExecution execution = backOff.start();
		BackOffExecution execution2 = backOff.start();

		assertEquals(100L, execution.nextBackOff());
		assertEquals(100L, execution2.nextBackOff());
		assertEquals(BackOffExecution.STOP, execution.nextBackOff());
		assertEquals(BackOffExecution.STOP, execution2.nextBackOff());
	}

	@Test
	public void liveUpdate() {
		FixedBackOff backOff = new FixedBackOff(100L, 1);
		BackOffExecution execution = backOff.start();
		assertEquals(100L, execution.nextBackOff());

		backOff.setInterval(200L);
		backOff.setMaxAttempts(2);

		assertEquals(200L, execution.nextBackOff());
		assertEquals(BackOffExecution.STOP, execution.nextBackOff());
	}

	@Test
	public void toStringContent() {
		FixedBackOff backOff = new FixedBackOff(200L, 10);
		BackOffExecution execution = backOff.start();
		assertEquals("FixedBackOff{interval=200, currentAttempts=0, maxAttempts=10}", execution.toString());
		execution.nextBackOff();
		assertEquals("FixedBackOff{interval=200, currentAttempts=1, maxAttempts=10}", execution.toString());
		execution.nextBackOff();
		assertEquals("FixedBackOff{interval=200, currentAttempts=2, maxAttempts=10}", execution.toString());
	}

}
