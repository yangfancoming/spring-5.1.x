

package org.springframework.web.servlet.tags;

import org.springframework.lang.Nullable;

/**
 * Bean used to pass name-value pair parameters from a {@link ParamTag} to a
 * {@link ParamAware} tag.
 *
 * Attributes are the raw values passed to the spring:param tag and have not
 * been encoded or escaped.
 *
 * @author Scott Andrews
 * @since 3.0
 * @see ParamTag
 */
public class Param {

	@Nullable
	private String name;

	@Nullable
	private String value;


	/**
	 * Set the raw name of the parameter.
	 */
	public void setName(@Nullable String name) {
		this.name = name;
	}

	/**
	 * Return the raw parameter name.
	 */
	@Nullable
	public String getName() {
		return this.name;
	}

	/**
	 * Set the raw value of the parameter.
	 */
	public void setValue(@Nullable String value) {
		this.value = value;
	}

	/**
	 * Return the raw parameter value.
	 */
	@Nullable
	public String getValue() {
		return this.value;
	}


	@Override
	public String toString() {
		return "JSP Tag Param: name '" + this.name + "', value '" + this.value + "'";
	}

}
