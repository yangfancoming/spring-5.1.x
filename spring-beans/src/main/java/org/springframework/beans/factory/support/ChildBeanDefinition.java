

package org.springframework.beans.factory.support;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/**
 * Bean definition for beans which inherit settings from their parent.
 * Child bean definitions have a fixed dependency on a parent bean definition.
 * A child bean definition will inherit constructor argument values, property values and method overrides from the parent,with the option to add new values.
 * If init method, destroy method and/or static factory  method are specified, they will override the corresponding parent settings.
 * The remaining settings will <i>always</i> be taken from the child definition: depends on, autowire mode, dependency check, singleton, lazy init.
 * <b>NOTE:</b> Since Spring 2.5, the preferred way to register bean definitions programmatically is the {@link GenericBeanDefinition} class,
 * which allows to dynamically define parent dependencies through the {@link GenericBeanDefinition#setParentName} method.
 * This effectively supersedes the ChildBeanDefinition class for most use cases.
 * @see GenericBeanDefinition
 * @see RootBeanDefinition
 */
@SuppressWarnings("serial")
public class ChildBeanDefinition extends AbstractBeanDefinition {

	@Nullable
	private String parentName;

	/**
	 * Create a new ChildBeanDefinition for the given parent, to be configured through its bean properties and configuration methods.
	 * @param parentName the name of the parent bean
	 * @see #setBeanClass
	 * @see #setScope
	 * @see #setConstructorArgumentValues
	 * @see #setPropertyValues
	 */
	public ChildBeanDefinition(String parentName) {
		super();
		this.parentName = parentName;
	}

	/**
	 * Create a new ChildBeanDefinition for the given parent.
	 * @param parentName the name of the parent bean
	 * @param pvs the additional property values of the child
	 */
	public ChildBeanDefinition(String parentName, MutablePropertyValues pvs) {
		super(null, pvs);
		this.parentName = parentName;
	}

	/**
	 * Create a new ChildBeanDefinition for the given parent.
	 * @param parentName the name of the parent bean
	 * @param cargs the constructor argument values to apply
	 * @param pvs the additional property values of the child
	 */
	public ChildBeanDefinition(String parentName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		super(cargs, pvs);
		this.parentName = parentName;
	}

	/**
	 * Create a new ChildBeanDefinition for the given parent, providing constructor arguments and property values.
	 * @param parentName the name of the parent bean
	 * @param beanClass the class of the bean to instantiate
	 * @param cargs the constructor argument values to apply
	 * @param pvs the property values to apply
	 */
	public ChildBeanDefinition(String parentName, Class<?> beanClass, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		super(cargs, pvs);
		this.parentName = parentName;
		setBeanClass(beanClass);
	}

	/**
	 * Create a new ChildBeanDefinition for the given parent,
	 * providing constructor arguments and property values.
	 * Takes a bean class name to avoid eager loading of the bean class.
	 * @param parentName the name of the parent bean
	 * @param beanClassName the name of the class to instantiate
	 * @param cargs the constructor argument values to apply
	 * @param pvs the property values to apply
	 */
	public ChildBeanDefinition(String parentName, String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		super(cargs, pvs);
		this.parentName = parentName;
		setBeanClassName(beanClassName);
	}

	/**
	 * Create a new ChildBeanDefinition as deep copy of the given bean definition.
	 * @param original the original bean definition to copy from
	 */
	public ChildBeanDefinition(ChildBeanDefinition original) {
		super(original);
	}

	@Override
	public void setParentName(@Nullable String parentName) {
		this.parentName = parentName;
	}

	@Override
	@Nullable
	public String getParentName() {
		return this.parentName;
	}

	@Override
	public void validate() throws BeanDefinitionValidationException {
		super.validate();
		if (parentName == null) throw new BeanDefinitionValidationException("'parentName' must be set in ChildBeanDefinition");
	}

	@Override
	public AbstractBeanDefinition cloneBeanDefinition() {
		return new ChildBeanDefinition(this);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof ChildBeanDefinition)) {
			return false;
		}
		ChildBeanDefinition that = (ChildBeanDefinition) other;
		return (ObjectUtils.nullSafeEquals(parentName, that.parentName) && super.equals(other));
	}

	@Override
	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(parentName) * 29 + super.hashCode();
	}

	@Override
	public String toString() {
		return "Child bean with parent '" + parentName + "': " + super.toString();
	}
}
