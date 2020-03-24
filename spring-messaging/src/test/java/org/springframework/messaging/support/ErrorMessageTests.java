

package org.springframework.messaging.support;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author Gary Russell
 * @since 5.0
 */
public class ErrorMessageTests {

	@Test
	public void testToString() {
		ErrorMessage em = new ErrorMessage(new RuntimeException("foo"));
		String emString = em.toString();
		assertThat(emString, not(containsString("original")));

		em = new ErrorMessage(new RuntimeException("foo"), new GenericMessage<>("bar"));
		emString = em.toString();
		assertThat(emString, containsString("original"));
		assertThat(emString, containsString(em.getOriginalMessage().toString()));
	}

}
