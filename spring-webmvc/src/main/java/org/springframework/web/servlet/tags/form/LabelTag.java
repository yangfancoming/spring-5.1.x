

package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The {@code <label>} tag renders a form field label in an HTML 'label' tag.
 *
 * See the "formTags" showcase application that ships with the
 * full Spring distribution for an example of this class in action.
 *
 *
 * <table>
 * <caption>Attribute Summary</caption>
 * <thead>
 * <tr>
 * <th class="colFirst">Attribute</th>
 * <th class="colOne">Required?</th>
 * <th class="colOne">Runtime Expression?</th>
 * <th class="colLast">Description</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr class="altColor">
 * <td>cssClass</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute.</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>cssErrorClass</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute. Used only when errors are present.</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>cssStyle</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>dir</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>for</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>htmlEscape</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>Enable/disable HTML escaping of rendered values.</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>id</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>lang</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>onclick</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>ondblclick</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>onkeydown</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>onkeypress</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>onkeyup</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>onmousedown</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>onmousemove</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>onmouseout</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>onmouseover</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>onmouseup</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>path</p></td>
 * <td>true</p></td>
 * <td>true</p></td>
 * <td>Path to errors object for data binding</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>tabindex</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>title</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @author Rob Harrop

 * @since 2.0
 */
@SuppressWarnings("serial")
public class LabelTag extends AbstractHtmlElementTag {

	/**
	 * The HTML '{@code label}' tag.
	 */
	private static final String LABEL_TAG = "label";

	/**
	 * The name of the '{@code for}' attribute.
	 */
	private static final String FOR_ATTRIBUTE = "for";


	/**
	 * The {@link TagWriter} instance being used.
	 * Stored so we can close the tag on {@link #doEndTag()}.
	 */
	@Nullable
	private TagWriter tagWriter;

	/**
	 * The value of the '{@code for}' attribute.
	 */
	@Nullable
	private String forId;


	/**
	 * Set the value of the '{@code for}' attribute.
	 * Defaults to the value of {@link #getPath}; may be a runtime expression.
	 */
	public void setFor(String forId) {
		this.forId = forId;
	}

	/**
	 * Get the value of the '{@code id}' attribute.
	 * May be a runtime expression.
	 */
	@Nullable
	protected String getFor() {
		return this.forId;
	}


	/**
	 * Writes the opening '{@code label}' tag and forces a block tag so
	 * that body content is written correctly.
	 * @return {@link javax.servlet.jsp.tagext.Tag#EVAL_BODY_INCLUDE}
	 */
	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		tagWriter.startTag(LABEL_TAG);
		tagWriter.writeAttribute(FOR_ATTRIBUTE, resolveFor());
		writeDefaultAttributes(tagWriter);
		tagWriter.forceBlock();
		this.tagWriter = tagWriter;
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * Overrides {@code #getName()} to always return {@code null},
	 * because the '{@code name}' attribute is not supported by the
	 * '{@code label}' tag.
	 * @return the value for the HTML '{@code name}' attribute
	 */
	@Override
	@Nullable
	protected String getName() throws JspException {
		// This also suppresses the 'id' attribute (which is okay for a <label/>)
		return null;
	}

	/**
	 * Determine the '{@code for}' attribute value for this tag,
	 * autogenerating one if none specified.
	 * @see #getFor()
	 * @see #autogenerateFor()
	 */
	protected String resolveFor() throws JspException {
		if (StringUtils.hasText(this.forId)) {
			return getDisplayString(evaluate(FOR_ATTRIBUTE, this.forId));
		}
		else {
			return autogenerateFor();
		}
	}

	/**
	 * Autogenerate the '{@code for}' attribute value for this tag.
	 * The default implementation delegates to {@link #getPropertyPath()},
	 * deleting invalid characters (such as "[" or "]").
	 */
	protected String autogenerateFor() throws JspException {
		return StringUtils.deleteAny(getPropertyPath(), "[]");
	}

	/**
	 * Close the '{@code label}' tag.
	 */
	@Override
	public int doEndTag() throws JspException {
		Assert.state(this.tagWriter != null, "No TagWriter set");
		this.tagWriter.endTag();
		return EVAL_PAGE;
	}

	/**
	 * Disposes of the {@link TagWriter} instance.
	 */
	@Override
	public void doFinally() {
		super.doFinally();
		this.tagWriter = null;
	}

}
