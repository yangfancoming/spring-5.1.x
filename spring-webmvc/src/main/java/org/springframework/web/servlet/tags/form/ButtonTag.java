

package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * The {@code <button>} tag renders a form field label in an HTML 'button' tag.
 * It is provided for completeness if the application relies on a
 * {@link RequestDataValueProcessor}.
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
 * <td>disabled</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute. Setting the value of this attribute to 'true'
 * will disable the HTML element.</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>id</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>name</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>The name attribute for the HTML button tag</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>value</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>The name attribute for the HTML button tag</p></td>
 * </tr>
 * </tbody>
 * </table>
 *
 *
 * @since 3.1
 */
@SuppressWarnings("serial")
public class ButtonTag extends AbstractHtmlElementTag {

	/**
	 * The name of the '{@code disabled}' attribute.
	 */
	public static final String DISABLED_ATTRIBUTE = "disabled";


	@Nullable
	private TagWriter tagWriter;

	@Nullable
	private String name;

	@Nullable
	private String value;

	private boolean disabled;


	/**
	 * Get the value of the '{@code name}' attribute.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the value of the '{@code name}' attribute.
	 */
	@Override
	@Nullable
	public String getName() {
		return this.name;
	}

	/**
	 * Set the value of the '{@code value}' attribute.
	 */
	public void setValue(@Nullable String value) {
		this.value = value;
	}

	/**
	 * Get the value of the '{@code value}' attribute.
	 */
	@Nullable
	public String getValue() {
		return this.value;
	}

	/**
	 * Set the value of the '{@code disabled}' attribute.
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * Get the value of the '{@code disabled}' attribute.
	 */
	public boolean isDisabled() {
		return this.disabled;
	}


	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		tagWriter.startTag("button");
		writeDefaultAttributes(tagWriter);
		tagWriter.writeAttribute("type", getType());
		writeValue(tagWriter);
		if (isDisabled()) {
			tagWriter.writeAttribute(DISABLED_ATTRIBUTE, "disabled");
		}
		tagWriter.forceBlock();
		this.tagWriter = tagWriter;
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * Writes the '{@code value}' attribute to the supplied {@link TagWriter}.
	 * Subclasses may choose to override this implementation to control exactly
	 * when the value is written.
	 */
	protected void writeValue(TagWriter tagWriter) throws JspException {
		String valueToUse = (getValue() != null ? getValue() : getDefaultValue());
		tagWriter.writeAttribute("value", processFieldValue(getName(), valueToUse, getType()));
	}

	/**
	 * Return the default value.
	 * @return the default value if none supplied
	 */
	protected String getDefaultValue() {
		return "Submit";
	}

	/**
	 * Get the value of the '{@code type}' attribute. Subclasses
	 * can override this to change the type of '{@code input}' element
	 * rendered. Default value is '{@code submit}'.
	 */
	protected String getType() {
		return "submit";
	}

	/**
	 * Closes the '{@code button}' block tag.
	 */
	@Override
	public int doEndTag() throws JspException {
		Assert.state(this.tagWriter != null, "No TagWriter set");
		this.tagWriter.endTag();
		return EVAL_PAGE;
	}

}
