

package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;

/**
 * The {@code <radiobutton>} tag renders an HTML 'input' tag with type 'radio'.
 *
 * Rendered elements are marked as 'checked' if the configured
 * {@link #setValue(Object) value} matches the {@link #getValue bound value}.
 *
 * A typical usage pattern will involved multiple tag instances bound
 * to the same property but with different values.
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
 * <td>cssClass</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>cssErrorClass</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute. Used when the bound field has errors.</p></td>
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
 * <td>label</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>Value to be displayed as part of the tag</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>lang</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
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
 * <td>path</p></td>
 * <td>true</p></td>
 * <td>true</p></td>
 * <td>Path to property for data binding</p></td>
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
 * <tr class="altColor">
 * <td>value</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute</p></td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @author Rob Harrop

 * @since 2.0
 */
@SuppressWarnings("serial")
public class RadioButtonTag extends AbstractSingleCheckedElementTag {

	@Override
	protected void writeTagDetails(TagWriter tagWriter) throws JspException {
		tagWriter.writeAttribute("type", getInputType());
		Object resolvedValue = evaluate("value", getValue());
		renderFromValue(resolvedValue, tagWriter);
	}

	@Override
	protected String getInputType() {
		return "radio";
	}

}
