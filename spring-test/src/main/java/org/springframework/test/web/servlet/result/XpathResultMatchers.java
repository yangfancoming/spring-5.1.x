

package org.springframework.test.web.servlet.result;

import java.util.Map;
import javax.xml.xpath.XPathExpressionException;

import org.hamcrest.Matcher;
import org.w3c.dom.Node;

import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.XpathExpectationsHelper;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * Factory for assertions on the response content using XPath expressions.
 *
 * An instance of this class is typically accessed via
 * {@link MockMvcResultMatchers#xpath}.
 *
 *
 * @since 3.2
 */
public class XpathResultMatchers {

	private final XpathExpectationsHelper xpathHelper;


	/**
	 * Protected constructor, not for direct instantiation. Use
	 * {@link MockMvcResultMatchers#xpath(String, Object...)} or
	 * {@link MockMvcResultMatchers#xpath(String, Map, Object...)}.
	 * @param expression the XPath expression
	 * @param namespaces the XML namespaces referenced in the XPath expression, or {@code null}
	 * @param args arguments to parameterize the XPath expression with using the
	 * formatting specifiers defined in {@link String#format(String, Object...)}
	 */
	protected XpathResultMatchers(String expression, @Nullable Map<String, String> namespaces, Object ... args)
			throws XPathExpressionException {

		this.xpathHelper = new XpathExpectationsHelper(expression, namespaces, args);
	}


	/**
	 * Evaluate the XPath and assert the {@link Node} content found with the
	 * given Hamcrest {@link Matcher}.
	 */
	public ResultMatcher node(final Matcher<? super Node> matcher) {
		return result -> {
			MockHttpServletResponse response = result.getResponse();
			this.xpathHelper.assertNode(response.getContentAsByteArray(), getDefinedEncoding(response), matcher);
		};
	}

	/**
	 * Get the response encoding if explicitly defined in the response, {code null} otherwise.
	 */
	@Nullable
	private String getDefinedEncoding(MockHttpServletResponse response) {
		return (response.isCharset() ? response.getCharacterEncoding() : null);
	}

	/**
	 * Evaluate the XPath and assert that content exists.
	 */
	public ResultMatcher exists() {
		return result -> {
			MockHttpServletResponse response = result.getResponse();
			this.xpathHelper.exists(response.getContentAsByteArray(), getDefinedEncoding(response));
		};
	}

	/**
	 * Evaluate the XPath and assert that content doesn't exist.
	 */
	public ResultMatcher doesNotExist() {
		return result -> {
			MockHttpServletResponse response = result.getResponse();
			this.xpathHelper.doesNotExist(response.getContentAsByteArray(), getDefinedEncoding(response));
		};
	}

	/**
	 * Evaluate the XPath and assert the number of nodes found with the given
	 * Hamcrest {@link Matcher}.
	 */
	public ResultMatcher nodeCount(final Matcher<Integer> matcher) {
		return result -> {
			MockHttpServletResponse response = result.getResponse();
			this.xpathHelper.assertNodeCount(response.getContentAsByteArray(), getDefinedEncoding(response), matcher);
		};
	}

	/**
	 * Evaluate the XPath and assert the number of nodes found.
	 */
	public ResultMatcher nodeCount(final int expectedCount) {
		return result -> {
			MockHttpServletResponse response = result.getResponse();
			this.xpathHelper.assertNodeCount(response.getContentAsByteArray(), getDefinedEncoding(response), expectedCount);
		};
	}

	/**
	 * Apply the XPath and assert the {@link String} value found with the given
	 * Hamcrest {@link Matcher}.
	 */
	public ResultMatcher string(final Matcher<? super String> matcher) {
		return result -> {
			MockHttpServletResponse response = result.getResponse();
			this.xpathHelper.assertString(response.getContentAsByteArray(), getDefinedEncoding(response), matcher);
		};
	}

	/**
	 * Apply the XPath and assert the {@link String} value found.
	 */
	public ResultMatcher string(final String expectedValue) {
		return result -> {
			MockHttpServletResponse response = result.getResponse();
			this.xpathHelper.assertString(response.getContentAsByteArray(), getDefinedEncoding(response), expectedValue);
		};
	}

	/**
	 * Evaluate the XPath and assert the {@link Double} value found with the
	 * given Hamcrest {@link Matcher}.
	 */
	public ResultMatcher number(final Matcher<? super Double> matcher) {
		return result -> {
			MockHttpServletResponse response = result.getResponse();
			this.xpathHelper.assertNumber(response.getContentAsByteArray(), getDefinedEncoding(response), matcher);
		};
	}

	/**
	 * Evaluate the XPath and assert the {@link Double} value found.
	 */
	public ResultMatcher number(final Double expectedValue) {
		return result -> {
			MockHttpServletResponse response = result.getResponse();
			this.xpathHelper.assertNumber(response.getContentAsByteArray(), getDefinedEncoding(response), expectedValue);
		};
	}

	/**
	 * Evaluate the XPath and assert the {@link Boolean} value found.
	 */
	public ResultMatcher booleanValue(final Boolean value) {
		return result -> {
			MockHttpServletResponse response = result.getResponse();
			this.xpathHelper.assertBoolean(response.getContentAsByteArray(), getDefinedEncoding(response), value);
		};
	}

}
