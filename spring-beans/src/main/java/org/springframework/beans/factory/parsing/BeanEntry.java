

package org.springframework.beans.factory.parsing;

/**
 * {@link ParseState} entry representing a bean definition.
 * @since 2.0
 */
public class BeanEntry implements ParseState.Entry {

	private String beanDefinitionName;

	/**
	 * Creates a new instance of {@link BeanEntry} class.
	 * @param beanDefinitionName the name of the associated bean definition
	 */
	public BeanEntry(String beanDefinitionName) {
		this.beanDefinitionName = beanDefinitionName;
	}


	@Override
	public String toString() {
		return "Bean '" + this.beanDefinitionName + "'";
	}

}
