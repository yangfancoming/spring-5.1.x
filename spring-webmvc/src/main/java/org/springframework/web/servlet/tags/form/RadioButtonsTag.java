

package org.springframework.web.servlet.tags.form;

/**
 * The {@code <radiobuttons>} tag renders multiple HTML 'input' tags with type 'radio'.
 *
 * Rendered elements are marked as 'checked' if the configured
 * {@link #setItems(Object) value} matches the bound value.
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
 * <td>delimiter</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>Delimiter to use between each 'input' tag with type 'radio'.
 * There is no delimiter by default.</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>dir</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>disabled</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute. Setting the value of this attribute to
 * 'true' will disable the HTML element.</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>element</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>Specifies the HTML element that is used to enclose each 'input' tag
 * with type 'radio'. Defaults to 'span'.</p></td>
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
 * <td>itemLabel</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>Value to be displayed as part of the 'input' tags with type
 * 'radio'</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>items</p></td>
 * <td>true</p></td>
 * <td>true</p></td>
 * <td>The Collection, Map or array of objects used to generate the 'input'
 * tags with type 'radio'</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>itemValue</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>Name of the property mapped to 'value' attribute of the 'input' tags
 * with type 'radio'</p></td>
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
 * </tbody>
 * </table>
 *
 * @author Thomas Risberg

 * @since 2.5
 */
@SuppressWarnings("serial")
public class RadioButtonsTag extends AbstractMultiCheckedElementTag {

	@Override
	protected String getInputType() {
		return "radio";
	}

}
