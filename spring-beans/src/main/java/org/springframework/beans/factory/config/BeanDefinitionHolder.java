

package org.springframework.beans.factory.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Holder for a BeanDefinition with name and aliases.Can be registered as a placeholder for an inner bean.
 * Can also be used for programmatic registration of inner bean definitions.
 * If you don't care about BeanNameAware and the like,registering RootBeanDefinition or ChildBeanDefinition is good enough.
 * @since 1.0.2
 * @see org.springframework.beans.factory.BeanNameAware
 * @see org.springframework.beans.factory.support.RootBeanDefinition
 * @see org.springframework.beans.factory.support.ChildBeanDefinition
 *
 * bean定义的持有者，同时持有 bean正名和bean别名
 * 使用示例1   解析<bean> 标签
 * @see org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#processBeanDefinition
 * 使用示例2   注解配置类方式 启动，注解bean定义
 * @see org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean
 */
public class BeanDefinitionHolder implements BeanMetadataElement {

	protected final Log logger = LogFactory.getLog(getClass());

	// 持有bean定义
	private final BeanDefinition beanDefinition;

	// 持有bean正名
	private final String beanName;

	//  @Nullable注解 表示该属性可以为空， 持有bean别名
	@Nullable
	private final String[] aliases;

	/**
	 * Create a new BeanDefinitionHolder.
	 * @param beanDefinition the BeanDefinition to wrap
	 * @param beanName the name of the bean, as specified for the bean definition
	 */
	public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
		this(beanDefinition, beanName, null);
	}

	/**
	 * Create a new BeanDefinitionHolder.
	 * @param beanDefinition the BeanDefinition to wrap
	 * @param beanName the name of the bean, as specified for the bean definition
	 * @param aliases alias names for the bean, or {@code null} if none
	 *  根据bean正名、beanDefinition、bean别名，来初始化BeanDefinitionHolder，其中别名aliases可以为空
	 */
	public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName, @Nullable String[] aliases) {
		logger.warn("进入 【BeanDefinitionHolder】 构造函数 {}");
		Assert.notNull(beanDefinition, "BeanDefinition must not be null");
		Assert.notNull(beanName, "Bean name must not be null");
		this.beanDefinition = beanDefinition;
		this.beanName = beanName;
		this.aliases = aliases;
	}

	/**
	 * Copy constructor: Create a new BeanDefinitionHolder with the same contents as the given BeanDefinitionHolder instance.
	 * Note: The wrapped BeanDefinition reference is taken as-is; it is {@code not} deeply copied.
	 * @param beanDefinitionHolder the BeanDefinitionHolder to copy
	 * 根据指定的BeanDefinitionHolder 复制一个新的BeanDefinitionHolder ，此处不是深克隆
	 */
	public BeanDefinitionHolder(BeanDefinitionHolder beanDefinitionHolder) {
		logger.warn("进入 【BeanDefinitionHolder】 构造函数 {}");
		Assert.notNull(beanDefinitionHolder, "BeanDefinitionHolder must not be null");
		this.beanDefinition = beanDefinitionHolder.getBeanDefinition();
		this.beanName = beanDefinitionHolder.getBeanName();
		this.aliases = beanDefinitionHolder.getAliases();
	}

	//  Return the wrapped BeanDefinition.
	public BeanDefinition getBeanDefinition() {
		return beanDefinition;
	}

	//  Return the primary name of the bean, as specified for the bean definition.
	public String getBeanName() {
		return beanName;
	}

	/**
	 * Return the alias names for the bean, as specified directly for the bean definition.
	 * @return the array of alias names, or {@code null} if none
	 */
	@Nullable
	public String[] getAliases() {
		return aliases;
	}

	// 判断指定的名称与beanName或者别名是否匹配
	//  Determine whether the given candidate name matches the bean name or the aliases stored in this bean definition.
	public boolean matchesName(@Nullable String candidateName) {
		return (candidateName != null && (candidateName.equals(beanName) || candidateName.equals(BeanFactoryUtils.transformedBeanName(beanName)) || ObjectUtils.containsElement(aliases, candidateName)));
	}

	/**
	 * Return a friendly, short description for the bean, stating name and aliases.
	 * @see #getBeanName()
	 * @see #getAliases()
	 */
	public String getShortDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Bean definition with name '").append(beanName).append("'");
		if (aliases != null) {
			sb.append(" and aliases [").append(StringUtils.arrayToCommaDelimitedString(aliases)).append("]");
		}
		return sb.toString();
	}

	/**
	 * Return a long description for the bean, including name and aliases as well as a description of the contained {@link BeanDefinition}.
	 * @see #getShortDescription()
	 * @see #getBeanDefinition()
	 */
	public String getLongDescription() {
		StringBuilder sb = new StringBuilder(getShortDescription());
		sb.append(": ").append(beanDefinition);
		return sb.toString();
	}

	//---------------------------------------------------------------------
	// Implementation of 【BeanMetadataElement】 interface
	//---------------------------------------------------------------------
	/**
	 * Expose the bean definition's source object.
	 * @see BeanDefinition#getSource()
	 * 获取beanDefinition的源对象，实现了BeanMetadataElement
	 */
	@Override
	@Nullable
	public Object getSource() {
		return beanDefinition.getSource();
	}

	//---------------------------------------------------------------------
	// Implementation of 【Object】 class
	//---------------------------------------------------------------------
	/**
	 * This implementation returns the long description. Can be overridden to return the short description or any kind of custom description instead.
	 * @see #getLongDescription()
	 * @see #getShortDescription()
	 */
	@Override
	public String toString() {
		return getLongDescription();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof BeanDefinitionHolder)) {
			return false;
		}
		BeanDefinitionHolder otherHolder = (BeanDefinitionHolder) other;
		return beanDefinition.equals(otherHolder.beanDefinition) && beanName.equals(otherHolder.beanName) && ObjectUtils.nullSafeEquals(aliases, otherHolder.aliases);
	}

	@Override
	public int hashCode() {
		int hashCode = beanDefinition.hashCode();
		hashCode = 29 * hashCode + beanName.hashCode();
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(aliases);
		return hashCode;
	}
}
