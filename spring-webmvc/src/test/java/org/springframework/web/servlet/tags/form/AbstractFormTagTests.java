

package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;

import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockPageContext;
import org.springframework.tests.sample.beans.TestBean;

/**
 * @author Rob Harrop
 * @author Juergen Hoeller
 */
public abstract class AbstractFormTagTests extends AbstractHtmlElementTagTests {

	private FormTag formTag = new FormTag();


	@Override
	protected void extendRequest(MockHttpServletRequest request) {
		request.setAttribute(COMMAND_NAME, createTestBean());
	}

	protected abstract TestBean createTestBean();

	@Override
	protected void extendPageContext(MockPageContext pageContext) throws JspException {
		this.formTag.setModelAttribute(COMMAND_NAME);
		this.formTag.setAction("myAction");
		this.formTag.setPageContext(pageContext);
		this.formTag.doStartTag();
	}

	protected final FormTag getFormTag() {
		return this.formTag;
	}

}
