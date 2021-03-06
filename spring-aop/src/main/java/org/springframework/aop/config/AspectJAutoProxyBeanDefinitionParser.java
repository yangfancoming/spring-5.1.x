

package org.springframework.aop.config;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;

/**
 * {@link BeanDefinitionParser} for the {@code aspectj-autoproxy} tag,
 * enabling the automatic application of @AspectJ-style aspects found in the {@link org.springframework.beans.factory.BeanFactory}.
 * @since 2.0
 */
class AspectJAutoProxyBeanDefinitionParser implements BeanDefinitionParser {

	// 解析标签的时候将会执行的方法
	@Override
	@Nullable
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		// 注册AnnotationAwareAspectJAutoProxyCreator bean 和处理标签的两个属性
		// 注册一个类型为AnnotationAwareAspectJAutoProxyCreator的bean到Spring容器中
		AopNamespaceUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext, element);
		// 处理子标签  // 通过读取配置文件对扩展相关属性
		extendBeanDefinition(element, parserContext);
		return null;
	}

	private void extendBeanDefinition(Element element, ParserContext parserContext) {
		// 获取前面注册的AnnotationAwareAspectJAutoProxyCreator对应的BeanDefinition
		BeanDefinition beanDef = parserContext.getRegistry().getBeanDefinition(AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME);
		if (element.hasChildNodes()) {
			// 解析当前标签的子标签
			addIncludePatterns(element, parserContext, beanDef);
		}
	}
	// 解析子标签中的name属性，其可以有多个，这个name属性最终会被添加到
	// AnnotationAwareAspectJAutoProxyCreator的includePatterns属性中，
	// Spring在判断一个类是否需要进行代理的时候会判断当前bean的名称是否与includePatterns中的
	// 正则表达式相匹配，如果不匹配，则不进行代理
	private void addIncludePatterns(Element element, ParserContext parserContext, BeanDefinition beanDef) {
		ManagedList<TypedStringValue> includePatterns = new ManagedList<>();
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node instanceof Element) {
				Element includeElement = (Element) node;
				// 解析子标签中的name属性
				TypedStringValue valueHolder = new TypedStringValue(includeElement.getAttribute("name"));
				valueHolder.setSource(parserContext.extractSource(includeElement));
				includePatterns.add(valueHolder);
			}
		}
		// 将解析到的name属性设置到AnnotationAwareAspectJAutoProxyCreator的includePatterns属性中
		if (!includePatterns.isEmpty()) {
			includePatterns.setSource(parserContext.extractSource(element));
			beanDef.getPropertyValues().add("includePatterns", includePatterns);
		}
	}

}
