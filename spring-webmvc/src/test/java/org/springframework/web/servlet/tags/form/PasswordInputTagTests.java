

package org.springframework.web.servlet.tags.form;

import org.junit.Test;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import java.io.Writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Rob Harrop
 * @author Rick Evans
 * @author Jeremy Grelle
 */
public class PasswordInputTagTests extends InputTagTests {

	/**
	 * https://jira.spring.io/browse/SPR-2866
	 */
	@Test
	public void passwordValueIsNotRenderedByDefault() throws Exception {
		this.getTag().setPath("name");

		assertEquals(Tag.SKIP_BODY, this.getTag().doStartTag());

		String output = getOutput();
		assertTagOpened(output);
		assertTagClosed(output);

		assertContainsAttribute(output, "type", getType());
		assertValueAttribute(output, "");
	}

	/**
	 * https://jira.spring.io/browse/SPR-2866
	 */
	@Test
	public void passwordValueIsRenderedIfShowPasswordAttributeIsSetToTrue() throws Exception {
		this.getTag().setPath("name");
		this.getPasswordTag().setShowPassword(true);

		assertEquals(Tag.SKIP_BODY, this.getTag().doStartTag());

		String output = getOutput();
		assertTagOpened(output);
		assertTagClosed(output);

		assertContainsAttribute(output, "type", getType());
		assertValueAttribute(output, "Rob");
	}

	/**
	 * https://jira.spring.io/browse/SPR-2866
	 */
	@Test
	public void passwordValueIsNotRenderedIfShowPasswordAttributeIsSetToFalse() throws Exception {
		this.getTag().setPath("name");
		this.getPasswordTag().setShowPassword(false);

		assertEquals(Tag.SKIP_BODY, this.getTag().doStartTag());

		String output = getOutput();
		assertTagOpened(output);
		assertTagClosed(output);

		assertContainsAttribute(output, "type", getType());
		assertValueAttribute(output, "");
	}

	@Test
	@Override
	public void dynamicTypeAttribute() throws JspException {
		try {
			this.getTag().setDynamicAttribute(null, "type", "email");
			fail("Expected exception");
		}
		catch (IllegalArgumentException e) {
			assertEquals("Attribute type=\"email\" is not allowed", e.getMessage());
		}
	}

	@Override
	protected void assertValueAttribute(String output, String expectedValue) {
		if (this.getPasswordTag().isShowPassword()) {
			super.assertValueAttribute(output, expectedValue);
		}
		else {
			super.assertValueAttribute(output, "");
		}
	}

	@Override
	protected String getType() {
		return "password";
	}

	@Override
	@SuppressWarnings("serial")
	protected InputTag createTag(final Writer writer) {
		return new PasswordInputTag() {
			@Override
			protected TagWriter createTagWriter() {
				return new TagWriter(writer);
			}
		};
	}

	private PasswordInputTag getPasswordTag() {
		return (PasswordInputTag) this.getTag();
	}

}
