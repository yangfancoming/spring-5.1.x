

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.lang.Nullable;

/**
 * GenericBeanDefinition is a one-stop shop for standard bean definition purposes.
 * Like any bean definition, it allows for specifying a class plus optionally constructor argument values and property values. 
 * Additionally, deriving from a parent bean definition can be flexibly configured through the "parentName" property.
 * In general, use this {@code GenericBeanDefinition} class for the purpose of registering user-visible bean definitions
 * (which a post-processor might operate on,potentially even reconfiguring the parent name).
 * Use {@code RootBeanDefinition} / {@code ChildBeanDefinition} where parent/child relationships happen to be pre-determined.
 * @since 2.5
 * @see #setParentName
 * @see RootBeanDefinition
 * @see ChildBeanDefinition
 *
 * GenericBeanDefinition是标准bean定义的一站式服务。
 * 与任何bean定义一样，它允许指定类以及可选的构造函数参数值和属性值。
 * 此外，可以通过“parentName”属性灵活地配置从父bean定义派生的。
 * 一般来说，使用这个{@code GenericBeanDefinition}类来注册用户可见的bean定义（后处理器可以对其进行操作，甚至可能重新配置父名称）。
 * 使用{@code RootBeanDefinition}/{@code ChildBeanDefinition}，其中父/子关系恰好是预先确定的。
 */
@SuppressWarnings("serial")
public class GenericBeanDefinition extends AbstractBeanDefinition {

	@Nullable
	private String parentName;

	/**
	 * Create a new GenericBeanDefinition, to be configured through its bean properties and configuration methods.
	 * @see #setBeanClass
	 * @see #setScope
	 * @see #setConstructorArgumentValues
	 * @see #setPropertyValues
	 */
	public GenericBeanDefinition() {
		super();
	}

	/**
	 * Create a new GenericBeanDefinition as deep copy of the given  bean definition.
	 * @param original the original bean definition to copy from
	 */
	public GenericBeanDefinition(BeanDefinition original) {
		super(original);
	}

	//---------------------------------------------------------------------
	// Implementation of 【BeanDefinition】 interface
	//---------------------------------------------------------------------
	@Override
	public void setParentName(@Nullable String parentName) {
		this.parentName = parentName;
	}

	@Override
	@Nullable
	public String getParentName() {
		return this.parentName;
	}

	//---------------------------------------------------------------------
	// Implementation of 【AbstractBeanDefinition】 interface
	//---------------------------------------------------------------------
	@Override
	public AbstractBeanDefinition cloneBeanDefinition() {
		return new GenericBeanDefinition(this);
	}

	//---------------------------------------------------------------------
	// Implementation of 【JDK】 interface
	//---------------------------------------------------------------------

	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof GenericBeanDefinition && super.equals(other)));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Generic bean");
		if (this.parentName != null) {
			sb.append(" with parent '").append(this.parentName).append("'");
		}
		sb.append(": ").append(super.toString());
		return sb.toString();
	}
}
