

package org.springframework.web.servlet.tags.form;

import org.junit.Test;
import org.springframework.tests.sample.beans.TestBean;

import javax.servlet.jsp.tagext.Tag;
import java.io.Writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ButtonTagTests extends AbstractFormTagTests {

	private ButtonTag tag;

	@Override
	protected void onSetUp() {
		this.tag = createTag(getWriter());
		this.tag.setParent(getFormTag());
		this.tag.setPageContext(getPageContext());
		this.tag.setId("My Id");
		this.tag.setName("My Name");
		this.tag.setValue("My Button");
	}

	@Test
	public void buttonTag() throws Exception {
		assertEquals(Tag.EVAL_BODY_INCLUDE, this.tag.doStartTag());
		assertEquals(Tag.EVAL_PAGE, this.tag.doEndTag());

		String output = getOutput();
		assertTagOpened(output);
		assertTagClosed(output);

		assertContainsAttribute(output, "id", "My Id");
		assertContainsAttribute(output, "name", "My Name");
		assertContainsAttribute(output, "type", "submit");
		assertContainsAttribute(output, "value", "My Button");
		assertAttributeNotPresent(output, "disabled");
	}

	@Test
	public void disabled() throws Exception {
		this.tag.setDisabled(true);

		this.tag.doStartTag();
		this.tag.doEndTag();

		String output = getOutput();
		assertTagOpened(output);
		assertTagClosed(output);

		assertContainsAttribute(output, "disabled", "disabled");
	}

	@Override
	protected TestBean createTestBean() {
		return new TestBean();
	}

	protected final void assertTagClosed(String output) {
		assertTrue("Tag not closed properly", output.endsWith("</button>"));
	}

	protected final void assertTagOpened(String output) {
		assertTrue("Tag not opened properly", output.startsWith("<button "));
	}

	@SuppressWarnings("serial")
	protected ButtonTag createTag(final Writer writer) {
		return new ButtonTag() {
			@Override
			protected TagWriter createTagWriter() {
				return new TagWriter(writer);
			}
		};
	}

}
