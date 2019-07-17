

package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;

import org.junit.Test;
import org.springframework.tests.sample.beans.CustomEnum;
import org.springframework.tests.sample.beans.GenericBean;
import org.springframework.web.servlet.support.BindStatus;

import static org.junit.Assert.*;


public class OptionTagEnumTests extends AbstractHtmlElementTagTests {

	private OptionTag tag;

	private SelectTag parentTag;

	@Override
	@SuppressWarnings("serial")
	protected void onSetUp() {
		this.tag = new OptionTag() {
			@Override
			protected TagWriter createTagWriter() {
				return new TagWriter(getWriter());
			}
		};
		this.parentTag = new SelectTag() {
			@Override
			public String getName() {
				// Should not be used other than to delegate to
				// RequestDataValueDataProcessor
				return "testName";
			}
		};
		this.tag.setParent(this.parentTag);
		this.tag.setPageContext(getPageContext());
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void withJavaEnum() throws Exception {
		GenericBean testBean = new GenericBean();
		testBean.setCustomEnum(CustomEnum.VALUE_1);
		getPageContext().getRequest().setAttribute("testBean", testBean);
		String selectName = "testBean.customEnum";
		getPageContext().setAttribute(SelectTag.LIST_VALUE_PAGE_ATTRIBUTE,
				new BindStatus(getRequestContext(), selectName, false));

		this.tag.setValue("VALUE_1");

		int result = this.tag.doStartTag();
		assertEquals(BodyTag.EVAL_BODY_BUFFERED, result);
		result = this.tag.doEndTag();
		assertEquals(Tag.EVAL_PAGE, result);

		String output = getWriter().toString();

		assertOptionTagOpened(output);
		assertOptionTagClosed(output);
		assertContainsAttribute(output, "value", "VALUE_1");
		assertContainsAttribute(output, "selected", "selected");
	}

	private void assertOptionTagOpened(String output) {
		assertTrue(output.startsWith("<option"));
	}

	private void assertOptionTagClosed(String output) {
		assertTrue(output.endsWith("</option>"));
	}

}
