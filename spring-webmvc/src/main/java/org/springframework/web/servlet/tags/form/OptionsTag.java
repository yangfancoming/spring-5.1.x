

package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.util.TagUtils;

/**
 * The {@code <options>} tag renders a list of HTML 'option' tags.
 * Sets 'selected' as appropriate based on bound value.
 *
 * <i>Must</i> be used within a {@link SelectTag 'select' tag}.
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
 * <td>HTML Optional Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>cssErrorClass</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute. Used when the bound field has errors.</p></td>
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
 * <td>disabled</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute. Setting the value of this attribute
 * to 'true' will disable the HTML element.</p></td>
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
 * <td>itemLabel</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>Name of the property mapped to the inner text of the 'option' tag</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>items</p></td>
 * <td>true</p></td>
 * <td>true</p></td>
 * <td>The Collection, Map or array of objects used to generate the inner 'option' tags</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>itemValue</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>Name of the property mapped to 'value' attribute of the 'option' tag</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>lang</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>onclick</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>ondblclick</p></td>
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

 * @author Scott Andrews
 * @since 2.0
 */
@SuppressWarnings("serial")
public class OptionsTag extends AbstractHtmlElementTag {

	/**
	 * The {@link java.util.Collection}, {@link java.util.Map} or array of
	 * objects used to generate the inner '{@code option}' tags.
	 */
	@Nullable
	private Object items;

	/**
	 * The name of the property mapped to the '{@code value}' attribute
	 * of the '{@code option}' tag.
	 */
	@Nullable
	private String itemValue;

	/**
	 * The name of the property mapped to the inner text of the
	 * '{@code option}' tag.
	 */
	@Nullable
	private String itemLabel;

	private boolean disabled;


	/**
	 * Set the {@link java.util.Collection}, {@link java.util.Map} or array
	 * of objects used to generate the inner '{@code option}' tags.
	 * Required when wishing to render '{@code option}' tags from an
	 * array, {@link java.util.Collection} or {@link java.util.Map}.
	 * Typically a runtime expression.
	 */
	public void setItems(Object items) {
		this.items = items;
	}

	/**
	 * Get the {@link java.util.Collection}, {@link java.util.Map} or array
	 * of objects used to generate the inner '{@code option}' tags.
	 * Typically a runtime expression.
	 */
	@Nullable
	protected Object getItems() {
		return this.items;
	}

	/**
	 * Set the name of the property mapped to the '{@code value}'
	 * attribute of the '{@code option}' tag.
	 * Required when wishing to render '{@code option}' tags from
	 * an array or {@link java.util.Collection}.
	 */
	public void setItemValue(String itemValue) {
		Assert.hasText(itemValue, "'itemValue' must not be empty");
		this.itemValue = itemValue;
	}

	/**
	 * Return the name of the property mapped to the '{@code value}'
	 * attribute of the '{@code option}' tag.
	 */
	@Nullable
	protected String getItemValue() {
		return this.itemValue;
	}

	/**
	 * Set the name of the property mapped to the label (inner text) of the
	 * '{@code option}' tag.
	 */
	public void setItemLabel(String itemLabel) {
		Assert.hasText(itemLabel, "'itemLabel' must not be empty");
		this.itemLabel = itemLabel;
	}

	/**
	 * Get the name of the property mapped to the label (inner text) of the
	 * '{@code option}' tag.
	 */
	@Nullable
	protected String getItemLabel() {
		return this.itemLabel;
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
	protected boolean isDisabled() {
		return this.disabled;
	}


	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		SelectTag selectTag = getSelectTag();
		Object items = getItems();
		Object itemsObject = null;
		if (items != null) {
			itemsObject = (items instanceof String ? evaluate("items", items) : items);
		}
		else {
			Class<?> selectTagBoundType = selectTag.getBindStatus().getValueType();
			if (selectTagBoundType != null && selectTagBoundType.isEnum()) {
				itemsObject = selectTagBoundType.getEnumConstants();
			}
		}
		if (itemsObject != null) {
			String selectName = selectTag.getName();
			String itemValue = getItemValue();
			String itemLabel = getItemLabel();
			String valueProperty =
					(itemValue != null ? ObjectUtils.getDisplayString(evaluate("itemValue", itemValue)) : null);
			String labelProperty =
					(itemLabel != null ? ObjectUtils.getDisplayString(evaluate("itemLabel", itemLabel)) : null);
			OptionsWriter optionWriter = new OptionsWriter(selectName, itemsObject, valueProperty, labelProperty);
			optionWriter.writeOptions(tagWriter);
		}
		return SKIP_BODY;
	}

	/**
	 * Appends a counter to a specified id,
	 * since we're dealing with multiple HTML elements.
	 */
	@Override
	protected String resolveId() throws JspException {
		Object id = evaluate("id", getId());
		if (id != null) {
			String idString = id.toString();
			return (StringUtils.hasText(idString) ? TagIdGenerator.nextId(idString, this.pageContext) : null);
		}
		return null;
	}

	private SelectTag getSelectTag() {
		TagUtils.assertHasAncestorOfType(this, SelectTag.class, "options", "select");
		return (SelectTag) findAncestorWithClass(this, SelectTag.class);
	}

	@Override
	protected BindStatus getBindStatus() {
		return (BindStatus) this.pageContext.getAttribute(SelectTag.LIST_VALUE_PAGE_ATTRIBUTE);
	}


	/**
	 * Inner class that adapts OptionWriter for multiple options to be rendered.
	 */
	private class OptionsWriter extends OptionWriter {

		@Nullable
		private final String selectName;

		public OptionsWriter(@Nullable String selectName, Object optionSource,
				@Nullable String valueProperty, @Nullable String labelProperty) {

			super(optionSource, getBindStatus(), valueProperty, labelProperty, isHtmlEscape());
			this.selectName = selectName;
		}

		@Override
		protected boolean isOptionDisabled() throws JspException {
			return isDisabled();
		}

		@Override
		protected void writeCommonAttributes(TagWriter tagWriter) throws JspException {
			writeOptionalAttribute(tagWriter, "id", resolveId());
			writeOptionalAttributes(tagWriter);
		}

		@Override
		protected String processOptionValue(String value) {
			return processFieldValue(this.selectName, value, "option");
		}
	}

}
