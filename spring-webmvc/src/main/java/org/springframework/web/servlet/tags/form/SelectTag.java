

package org.springframework.web.servlet.tags.form;

import java.util.Collection;
import java.util.Map;
import javax.servlet.jsp.JspException;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.support.BindStatus;

/**
 * The {@code <select>} tag renders an HTML 'select' element.
 * Supports data binding to the selected option.
 *
 * Inner '{@code option}' tags can be rendered using one of the
 * approaches supported by the OptionWriter class.
 *
 * Also supports the use of nested {@link OptionTag OptionTags} or
 * (typically one) nested {@link OptionsTag}.
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
 * <td>itemLabel</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>Name of the property mapped to the inner text of the 'option' tag</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>items</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>The Collection, Map or array of objects used to generate the inner
 * 'option' tags</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>itemValue</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>Name of the property mapped to 'value' attribute of the 'option'
 * tag</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>lang</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Standard Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>multiple</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Optional Attribute</p></td>
 * </tr>
 * <tr class="rowColor">
 * <td>onblur</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
 * </tr>
 * <tr class="altColor">
 * <td>onchange</p></td>
 * <td>false</p></td>
 * <td>true</p></td>
 * <td>HTML Event Attribute</p></td>
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
 * <td>onfocus</p></td>
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
 * <td>Path to property for data binding</p></td>
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

 * @since 2.0
 * @see OptionTag
 */
@SuppressWarnings("serial")
public class SelectTag extends AbstractHtmlInputElementTag {

	/**
	 * The {@link javax.servlet.jsp.PageContext} attribute under
	 * which the bound value is exposed to inner {@link OptionTag OptionTags}.
	 */
	public static final String LIST_VALUE_PAGE_ATTRIBUTE =
			"org.springframework.web.servlet.tags.form.SelectTag.listValue";

	/**
	 * Marker object for items that have been specified but resolve to null.
	 * Allows to differentiate between 'set but null' and 'not set at all'.
	 */
	private static final Object EMPTY = new Object();


	/**
	 * The {@link Collection}, {@link Map} or array of objects used to generate
	 * the inner '{@code option}' tags.
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

	/**
	 * The value of the HTML '{@code size}' attribute rendered
	 * on the final '{@code select}' element.
	 */
	@Nullable
	private String size;

	/**
	 * Indicates whether or not the '{@code select}' tag allows
	 * multiple-selections.
	 */
	@Nullable
	private Object multiple;

	/**
	 * The {@link TagWriter} instance that the output is being written.
	 * Only used in conjunction with nested {@link OptionTag OptionTags}.
	 */
	@Nullable
	private TagWriter tagWriter;


	/**
	 * Set the {@link Collection}, {@link Map} or array of objects used to
	 * generate the inner '{@code option}' tags.
	 * Required when wishing to render '{@code option}' tags from
	 * an array, {@link Collection} or {@link Map}.
	 * Typically a runtime expression.
	 * @param items the items that comprise the options of this selection
	 */
	public void setItems(@Nullable Object items) {
		this.items = (items != null ? items : EMPTY);
	}

	/**
	 * Get the value of the '{@code items}' attribute.
	 * May be a runtime expression.
	 */
	@Nullable
	protected Object getItems() {
		return this.items;
	}

	/**
	 * Set the name of the property mapped to the '{@code value}'
	 * attribute of the '{@code option}' tag.
	 * Required when wishing to render '{@code option}' tags from
	 * an array or {@link Collection}.
	 * May be a runtime expression.
	 */
	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}

	/**
	 * Get the value of the '{@code itemValue}' attribute.
	 * May be a runtime expression.
	 */
	@Nullable
	protected String getItemValue() {
		return this.itemValue;
	}

	/**
	 * Set the name of the property mapped to the label (inner text) of the
	 * '{@code option}' tag.
	 * May be a runtime expression.
	 */
	public void setItemLabel(String itemLabel) {
		this.itemLabel = itemLabel;
	}

	/**
	 * Get the value of the '{@code itemLabel}' attribute.
	 * May be a runtime expression.
	 */
	@Nullable
	protected String getItemLabel() {
		return this.itemLabel;
	}

	/**
	 * Set the value of the HTML '{@code size}' attribute rendered
	 * on the final '{@code select}' element.
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * Get the value of the '{@code size}' attribute.
	 */
	@Nullable
	protected String getSize() {
		return this.size;
	}

	/**
	 * Set the value of the HTML '{@code multiple}' attribute rendered
	 * on the final '{@code select}' element.
	 */
	public void setMultiple(Object multiple) {
		this.multiple = multiple;
	}

	/**
	 * Get the value of the HTML '{@code multiple}' attribute rendered
	 * on the final '{@code select}' element.
	 */
	@Nullable
	protected Object getMultiple() {
		return this.multiple;
	}


	/**
	 * Renders the HTML '{@code select}' tag to the supplied
	 * {@link TagWriter}.
	 * Renders nested '{@code option}' tags if the
	 * {@link #setItems items} property is set, otherwise exposes the
	 * bound value for the nested {@link OptionTag OptionTags}.
	 */
	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		tagWriter.startTag("select");
		writeDefaultAttributes(tagWriter);
		if (isMultiple()) {
			tagWriter.writeAttribute("multiple", "multiple");
		}
		tagWriter.writeOptionalAttributeValue("size", getDisplayString(evaluate("size", getSize())));

		Object items = getItems();
		if (items != null) {
			// Items specified, but might still be empty...
			if (items != EMPTY) {
				Object itemsObject = evaluate("items", items);
				if (itemsObject != null) {
					final String selectName = getName();
					String valueProperty = (getItemValue() != null ?
							ObjectUtils.getDisplayString(evaluate("itemValue", getItemValue())) : null);
					String labelProperty = (getItemLabel() != null ?
							ObjectUtils.getDisplayString(evaluate("itemLabel", getItemLabel())) : null);
					OptionWriter optionWriter =
							new OptionWriter(itemsObject, getBindStatus(), valueProperty, labelProperty, isHtmlEscape()) {
								@Override
								protected String processOptionValue(String resolvedValue) {
									return processFieldValue(selectName, resolvedValue, "option");
								}
							};
					optionWriter.writeOptions(tagWriter);
				}
			}
			tagWriter.endTag(true);
			writeHiddenTagIfNecessary(tagWriter);
			return SKIP_BODY;
		}
		else {
			// Using nested <form:option/> tags, so just expose the value in the PageContext...
			tagWriter.forceBlock();
			this.tagWriter = tagWriter;
			this.pageContext.setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, getBindStatus());
			return EVAL_BODY_INCLUDE;
		}
	}

	/**
	 * If using a multi-select, a hidden element is needed to make sure all
	 * items are correctly unselected on the server-side in response to a
	 * {@code null} post.
	 */
	private void writeHiddenTagIfNecessary(TagWriter tagWriter) throws JspException {
		if (isMultiple()) {
			tagWriter.startTag("input");
			tagWriter.writeAttribute("type", "hidden");
			String name = WebDataBinder.DEFAULT_FIELD_MARKER_PREFIX + getName();
			tagWriter.writeAttribute("name", name);
			tagWriter.writeAttribute("value", processFieldValue(name, "1", "hidden"));
			tagWriter.endTag();
		}
	}

	private boolean isMultiple() throws JspException {
		Object multiple = getMultiple();
		if (multiple != null) {
			String stringValue = multiple.toString();
			return ("multiple".equalsIgnoreCase(stringValue) || Boolean.parseBoolean(stringValue));
		}
		return forceMultiple();
	}

	/**
	 * Returns '{@code true}' if the bound value requires the
	 * resultant '{@code select}' tag to be multi-select.
	 */
	private boolean forceMultiple() throws JspException {
		BindStatus bindStatus = getBindStatus();
		Class<?> valueType = bindStatus.getValueType();
		if (valueType != null && typeRequiresMultiple(valueType)) {
			return true;
		}
		else if (bindStatus.getEditor() != null) {
			Object editorValue = bindStatus.getEditor().getValue();
			if (editorValue != null && typeRequiresMultiple(editorValue.getClass())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns '{@code true}' for arrays, {@link Collection Collections}
	 * and {@link Map Maps}.
	 */
	private static boolean typeRequiresMultiple(Class<?> type) {
		return (type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type));
	}

	/**
	 * Closes any block tag that might have been opened when using
	 * nested {@link OptionTag options}.
	 */
	@Override
	public int doEndTag() throws JspException {
		if (this.tagWriter != null) {
			this.tagWriter.endTag();
			writeHiddenTagIfNecessary(this.tagWriter);
		}
		return EVAL_PAGE;
	}

	/**
	 * Clears the {@link TagWriter} that might have been left over when using
	 * nested {@link OptionTag options}.
	 */
	@Override
	public void doFinally() {
		super.doFinally();
		this.tagWriter = null;
		this.pageContext.removeAttribute(LIST_VALUE_PAGE_ATTRIBUTE);
	}

}
