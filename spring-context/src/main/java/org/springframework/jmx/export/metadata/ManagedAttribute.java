

package org.springframework.jmx.export.metadata;

import org.springframework.lang.Nullable;

/**
 * Metadata that indicates to expose a given bean property as JMX attribute.
 * Only valid when used on a JavaBean getter or setter.
 *
 * @author Rob Harrop
 * @since 1.2
 * @see org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler
 * @see org.springframework.jmx.export.MBeanExporter
 */
public class ManagedAttribute extends AbstractJmxAttribute {

	/**
	 * Empty attributes.
	 */
	public static final ManagedAttribute EMPTY = new ManagedAttribute();


	@Nullable
	private Object defaultValue;

	@Nullable
	private String persistPolicy;

	private int persistPeriod = -1;


	/**
	 * Set the default value of this attribute.
	 */
	public void setDefaultValue(@Nullable Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Return the default value of this attribute.
	 */
	@Nullable
	public Object getDefaultValue() {
		return this.defaultValue;
	}

	public void setPersistPolicy(@Nullable String persistPolicy) {
		this.persistPolicy = persistPolicy;
	}

	@Nullable
	public String getPersistPolicy() {
		return this.persistPolicy;
	}

	public void setPersistPeriod(int persistPeriod) {
		this.persistPeriod = persistPeriod;
	}

	public int getPersistPeriod() {
		return this.persistPeriod;
	}

}
