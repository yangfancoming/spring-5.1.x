

package org.springframework.test.web.servlet.htmlunit.webdriver;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.WebDriverException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link WebConnectionHtmlUnitDriver}.
 *
 * @author Rob Winch
 * @author Sam Brannen
 * @since 4.2
 */
@RunWith(MockitoJUnitRunner.class)
public class WebConnectionHtmlUnitDriverTests {

	private final WebConnectionHtmlUnitDriver driver = new WebConnectionHtmlUnitDriver();

	@Mock
	private WebConnection connection;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup() throws Exception {
		when(this.connection.getResponse(any(WebRequest.class))).thenThrow(new IOException(""));
	}


	@Test
	public void getWebConnectionDefaultNotNull() {
		assertThat(this.driver.getWebConnection(), notNullValue());
	}

	@Test
	public void setWebConnectionToNull() {
		this.exception.expect(IllegalArgumentException.class);
		this.driver.setWebConnection(null);
	}

	@Test
	public void setWebConnection() {
		this.driver.setWebConnection(this.connection);
		assertThat(this.driver.getWebConnection(), equalTo(this.connection));

		this.exception.expect(WebDriverException.class);
		this.driver.get("https://example.com");
	}

}
