

package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Immutable placeholder class used for a property value object when it's a reference to another bean in the factory, to be resolved at runtime.
 * @see BeanDefinition#getPropertyValues()
 * @see org.springframework.beans.factory.BeanFactory#getBean(String)
 * 当我们需要动态注入Bean，并给该Bean的属性注入其他Bean时，
 * 比如在Mybatis和Spring的整合中，我们需要动态注入Mapper到spring容器中，而该Mapper如果需要执行SQL语句，还需要持有SqlSessionFactory的引用。
 * 但是我们注入时，可能对应的Bean还没有准备好，这时，我们就可以使用RuntimeBeanReference，以保持对实际Bean的引用。
 * 在Spring处理依赖关系时，最终会将该引用替换成实际生成的Bean对象，例如：以下
 * @see org.mybatis.spring.mapper.ClassPathMapperScanner#processBeanDefinitions(java.util.Set) 中的
 * eg： definition.getPropertyValues().add("sqlSessionTemplate",new RuntimeBeanReference(this.sqlSessionTemplateBeanName));
 */
public class RuntimeBeanReference implements BeanReference {

	private final String beanName;

	private final boolean toParent;

	@Nullable
	private Object source;

	/**
	 * Create a new RuntimeBeanReference to the given bean name.
	 * @param beanName name of the target bean
	 */
	public RuntimeBeanReference(String beanName) {
		this(beanName, false);
	}

	/**
	 * Create a new RuntimeBeanReference to the given bean name, with the option to mark it as reference to a bean in the parent factory.
	 * @param beanName name of the target bean
	 * @param toParent whether this is an explicit reference to a bean in the
	 * parent factory
	 */
	public RuntimeBeanReference(String beanName, boolean toParent) {
		Assert.hasText(beanName, "'beanName' must not be empty");
		this.beanName = beanName;
		this.toParent = toParent;
	}

	@Override
	public String getBeanName() {
		return this.beanName;
	}

	/**
	 * Return whether this is an explicit reference to a bean in the parent factory.
	 */
	public boolean isToParent() {
		return this.toParent;
	}

	/**
	 * Set the configuration source {@code Object} for this metadata element.
	 * The exact type of the object will depend on the configuration mechanism used.
	 */
	public void setSource(@Nullable Object source) {
		this.source = source;
	}

	@Override
	@Nullable
	public Object getSource() {
		return this.source;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof RuntimeBeanReference)) {
			return false;
		}
		RuntimeBeanReference that = (RuntimeBeanReference) other;
		return (this.beanName.equals(that.beanName) && this.toParent == that.toParent);
	}

	@Override
	public int hashCode() {
		int result = this.beanName.hashCode();
		result = 29 * result + (this.toParent ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return '<' + getBeanName() + '>';
	}
}
