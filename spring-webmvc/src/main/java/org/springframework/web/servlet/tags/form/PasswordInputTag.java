

package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;

/**
 * The {@code <password>} tag renders an HTML 'input' tag with type 'password'
 * using the bound value.
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
 * <td>accesskey</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>alt</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>autocomplete</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>Common Optional Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>cssClass</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>cssErrorClass</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute. Used when the bound field has
 * errors.</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>cssStyle</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>dir</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>disabled</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute. Setting the value of this attribute to 'true'
 * will disable the HTML element.</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>htmlEscape</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>Enable/disable HTML escaping of rendered values.</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>id</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>lang</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>maxlength</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>onblur</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>onchange</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
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
 * <td>onfocus</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>onkeydown</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>onkeypress</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>onkeyup</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>onmousedown</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>onmousemove</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>onmouseout</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>onmouseover</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>onmouseup</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>onselect</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>path</p></td>
 * <td>true</p></td>
 * <td>true</p></td>
 * <td>Path to property for data binding</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>readonly</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute. Setting the value of this attribute to 'true'
 * will make the HTML element readonly.</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>showPassword</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>Is the password value to be shown? Defaults to false.</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>size</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>tabindex</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>title</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @author Rob Harrop
 * @author Rick Evans
 * @author Rossen Stoyanchev
 * @since 2.0
 */
@SuppressWarnings("serial")
public class PasswordInputTag extends InputTag {

	private boolean showPassword = false;


	/**
	 * Is the password value to be rendered?
	 * @param showPassword {@code true} if the password value is to be rendered
	 */
	public void setShowPassword(boolean showPassword) {
		this.showPassword = showPassword;
	}

	/**
	 * Is the password value to be rendered?
	 * @return {@code true} if the password value to be rendered
	 */
	public boolean isShowPassword() {
		return this.showPassword;
	}


	/**
	 * Flags "type" as an illegal dynamic attribute.
	 */
	@Override
	protected boolean isValidDynamicAttribute(String localName, Object value) {
		return !"type".equals(localName);
	}

	/**
	 * Return '{@code password}' causing the rendered HTML '{@code input}'
	 * element to have a '{@code type}' of '{@code password}'.
	 */
	@Override
	protected String getType() {
		return "password";
	}

	/**
	 * The {@link PasswordInputTag} only writes it's value if the
	 * {@link #setShowPassword(boolean) 'showPassword'} property value is
	 * {@link Boolean#TRUE true}.
	 */
	@Override
	protected void writeValue(TagWriter tagWriter) throws JspException {
		if (this.showPassword) {
			super.writeValue(tagWriter);
		}
		else {
			tagWriter.writeAttribute("value", processFieldValue(getName(), "", getType()));
		}
	}

}
