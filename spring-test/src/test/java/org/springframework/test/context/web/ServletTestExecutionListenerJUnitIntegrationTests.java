

package org.springframework.test.context.web;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.Assert.*;

/**
 * JUnit-based integration tests for {@link ServletTestExecutionListener}.
 *
 * @author Sam Brannen
 * @since 3.2.9
 * @see org.springframework.test.context.testng.web.ServletTestExecutionListenerTestNGIntegrationTests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class ServletTestExecutionListenerJUnitIntegrationTests {

	@Configuration
	static class Config {
		/* no beans required for this test */
	}


	@Autowired
	private MockHttpServletRequest servletRequest;


	/**
	 * Verifies bug fix for <a href="https://jira.spring.io/browse/SPR-11626">SPR-11626</a>.
	 *
	 * @see #ensureMocksAreReinjectedBetweenTests_2
	 */
	@Test
	public void ensureMocksAreReinjectedBetweenTests_1() {
		assertInjectedServletRequestEqualsRequestInRequestContextHolder();
	}

	/**
	 * Verifies bug fix for <a href="https://jira.spring.io/browse/SPR-11626">SPR-11626</a>.
	 *
	 * @see #ensureMocksAreReinjectedBetweenTests_1
	 */
	@Test
	public void ensureMocksAreReinjectedBetweenTests_2() {
		assertInjectedServletRequestEqualsRequestInRequestContextHolder();
	}

	private void assertInjectedServletRequestEqualsRequestInRequestContextHolder() {
		assertEquals("Injected ServletRequest must be stored in the RequestContextHolder", servletRequest,
			((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
	}

}
