
package org.springframework.test.web.servlet.result;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.StubMvcResult;

/**
 * Unit tests for {@link HeaderResultMatchers}.
 * @author Rossen Stoyanchev
 */
public class HeaderResultMatchersTests {

	private final HeaderResultMatchers matchers = new HeaderResultMatchers();

	private final MockHttpServletResponse response = new MockHttpServletResponse();

	private final MvcResult mvcResult =
			new StubMvcResult(new MockHttpServletRequest(), null, null, null, null, null, this.response);


	@Test // SPR-17330
	public void matchDateFormattedWithHttpHeaders() throws Exception {

		long epochMilli = ZonedDateTime.of(2018, 10, 5, 0, 0, 0, 0, ZoneId.of("GMT")).toInstant().toEpochMilli();
		HttpHeaders headers = new HttpHeaders();
		headers.setDate("myDate", epochMilli);
		this.response.setHeader("d", headers.getFirst("myDate"));

		this.matchers.dateValue("d", epochMilli).match(this.mvcResult);
	}

}
