

package org.springframework.web.servlet.config;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.servlet.handler.MappedInterceptor;

/**
 * {@link org.springframework.beans.factory.xml.BeanDefinitionParser} that parses a
 * {@code interceptors} element to register a set of {@link MappedInterceptor} definitions.
 * @since 3.0
 * 通过org.springframework.beans.factory.xml.BeanDefinitionParser
 * 	解析 <interceptors><interceptors/> 标签，注册MappedInterceptor.
 */
class InterceptorsBeanDefinitionParser implements BeanDefinitionParser {

	private final Log logger = LogFactory.getLog(getClass());

	@Override
	@Nullable
	public BeanDefinition parse(Element element, ParserContext context) {
		logger.warn("【spring-mvc 开始解析 <interceptors> 标签  --- 】  ");
		context.pushContainingComponent(new CompositeComponentDefinition(element.getTagName(), context.extractSource(element)));
		RuntimeBeanReference pathMatcherRef = null;
		// interceptors元素的path-matcher属性.
		if (element.hasAttribute("path-matcher")) {
			pathMatcherRef = new RuntimeBeanReference(element.getAttribute("path-matcher"));
		}
		// interceptors的子元素：bean、ref、interceptor.
		List<Element> interceptors = DomUtils.getChildElementsByTagName(element, "bean", "ref", "interceptor");
		// 遍历interceptors的子元素
		for (Element interceptor : interceptors) {
			// 创建MappedInterceptor的Bean定义.
			RootBeanDefinition mappedInterceptorDef = new RootBeanDefinition(MappedInterceptor.class);
			mappedInterceptorDef.setSource(context.extractSource(interceptor));
			mappedInterceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

			ManagedList<String> includePatterns = null;
			ManagedList<String> excludePatterns = null;
			Object interceptorBean;
			// 处理interceptor子元素.
			if ("interceptor".equals(interceptor.getLocalName())) {
				// 根据interceptor元素的mapping子元素的path属性，获取包含模式.
				includePatterns = getIncludePatterns(interceptor, "mapping");
				// 根据interceptor元素的mapping子元素的path属性，获取排除模式.
				excludePatterns = getIncludePatterns(interceptor, "exclude-mapping");
				// 获取interceptor元素的子元素：bean、ref.
				Element beanElem = DomUtils.getChildElementsByTagName(interceptor, "bean", "ref").get(0);
				// 解析通用的bean、ref元素，其实际为HandlerInterceptor实例.
				interceptorBean = context.getDelegate().parsePropertySubElement(beanElem, null);
			}else {
				// 解析通用的bean、ref元素，其实际为HandlerInterceptor实例.
				interceptorBean = context.getDelegate().parsePropertySubElement(interceptor, null);
			}
			// 构造函数-includePatterns.
			mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(0, includePatterns);
			// 构造函数-excludePatterns.
			mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(1, excludePatterns);
			// 构造函数-interceptorBean.
			mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(2, interceptorBean);
			// 若存在pathMatcherRef，则设置mappedInterceptorDef的pathMatcher属性.
			if (pathMatcherRef != null) {
				mappedInterceptorDef.getPropertyValues().add("pathMatcher", pathMatcherRef);
			}
			// 注册并获取Bean名称.
			String beanName = context.getReaderContext().registerWithGeneratedName(mappedInterceptorDef);
			// 注册Bean.
			context.registerComponent(new BeanComponentDefinition(mappedInterceptorDef, beanName));
		}
		context.popAndRegisterContainingComponent();
		return null;
	}

	/**
	 * 获取匹配模式.
	 */
	private ManagedList<String> getIncludePatterns(Element interceptor, String elementName) {
		// 获取interceptor元素的子元素.
		List<Element> paths = DomUtils.getChildElementsByTagName(interceptor, elementName);
		ManagedList<String> patterns = new ManagedList<>(paths.size());
		for (Element path : paths) {
			// 获取interceptor元素的子元素的path属性.
			patterns.add(path.getAttribute("path"));
		}
		return patterns;
	}
}
