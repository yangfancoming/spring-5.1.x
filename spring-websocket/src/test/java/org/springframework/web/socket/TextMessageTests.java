

package org.springframework.web.socket;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test fixture for {@link TextMessage}.
 */
public class TextMessageTests {

	@Test
	public void toStringWithAscii() {
		String expected = "foo,bar";
		TextMessage actual = new TextMessage(expected);
		assertThat(actual.getPayload(), Matchers.is(expected));
		assertThat(actual.toString(), Matchers.containsString(expected));
	}

	@Test
	public void toStringWithMultibyteString() {
		String expected = "\u3042\u3044\u3046\u3048\u304a";
		TextMessage actual = new TextMessage(expected);
		assertThat(actual.getPayload(), Matchers.is(expected));
		assertThat(actual.toString(), Matchers.containsString(expected));
	}

}
