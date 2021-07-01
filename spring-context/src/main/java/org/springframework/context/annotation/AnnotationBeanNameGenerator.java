

package org.springframework.context.annotation;

import java.beans.Introspector;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * {@link org.springframework.beans.factory.support.BeanNameGenerator} implementation for bean classes annotated with the
 * {@link org.springframework.stereotype.Component @Component} annotation or with another annotation that is itself annotated with
 * {@link org.springframework.stereotype.Component @Component} as a meta-annotation. For example, Spring's stereotype annotations (such as
 * {@link org.springframework.stereotype.Repository @Repository}) are themselves annotated with
 * {@link org.springframework.stereotype.Component @Component}.
 * Also supports Java EE 6's {@link javax.annotation.ManagedBean} and  JSR-330's {@link javax.inject.Named} annotations,if available.
 * Note that Spring component annotations always override such standard annotations.
 * If the annotation's value doesn't indicate a bean name,an appropriate name will be built based on the short name of the class (with the first letter lower-cased).
 * For example:  <pre class="code">com.xyz.FooServiceImpl -&gt; fooServiceImpl</pre>
 * @since 2.5
 * @see org.springframework.stereotype.Component#value()
 * @see org.springframework.stereotype.Repository#value()
 * @see org.springframework.stereotype.Service#value()
 * @see org.springframework.stereotype.Controller#value()
 * @see javax.inject.Named#value()
 */
public class AnnotationBeanNameGenerator implements BeanNameGenerator {

	private static final String COMPONENT_ANNOTATION_CLASSNAME = "org.springframework.stereotype.Component";

	//---------------------------------------------------------------------------------------------------------------------
	// Implementation of 【BeanNameGenerator】 interface 唯一核心方法 本类中的其他方式都是为该方法服务的！
	//---------------------------------------------------------------------------------------------------------------------
	@Override
	public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
		// 若是 AnnotatedBeanDefinition 子类，则使用注释模式生成策略
		if (definition instanceof AnnotatedBeanDefinition) {
			String beanName = determineBeanNameFromAnnotation((AnnotatedBeanDefinition) definition);
			if (StringUtils.hasText(beanName)) {
				// Explicit bean name found.
				return beanName;
			}
		}
		// 如果不是注解模式则使用xml模式生成名称策略。  Fallback: generate a unique default bean name.
		return buildDefaultBeanName(definition, registry);
	}

	/**
	 * 根据类的注解中的value属性生成一个bean name
	 * Derive a bean name from one of the annotations on the class.
	 * @param annotatedDef the annotation-aware bean definition
	 * @return the bean name, or {@code null} if none is found
	 */
	@Nullable
	protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {
		// AnnotatedGenericBeanDefinition 构造时初始化的参数 （StandardAnnotationMetadata）
		AnnotationMetadata amd = annotatedDef.getMetadata();
		String beanName = null;
		// 获取当前类上所有的注解的全类名
		Set<String> types = amd.getAnnotationTypes();
		for (String type : types) {
			// 获取该注解对应的属性
			AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(amd, type);
			// 判断注解类型是否包含value属性
			if (attributes != null && isStereotypeWithNameValue(type, amd.getMetaAnnotationTypes(type), attributes)) {
				Object value = attributes.get("value");
				// 只有注解中的value属性 是字符串时，才会解析 eg： @Service("goat") 。   若是数字则直接跳过 eg： @Service(123)
				if (value instanceof String) {
					String strVal = (String) value;
					if (StringUtils.hasLength(strVal)) {
						// 不多于1个注解配置了value属性且非空，比如无法在一个类上面同时使用Component和Sevice注解同时配置beanName值
						if (beanName != null && !strVal.equals(beanName)) {
							throw new IllegalStateException("Stereotype annotations suggest inconsistent component names: '" + beanName + "' versus '" + strVal + "'");
						}
						beanName = strVal;
					}
				}
			}
		}
		return beanName;
	}

	/**
	 * Check whether the given annotation is a stereotype that is allowed to suggest a component name through its annotation {@code value()}.
	 * @param annotationType the name of the annotation class to check
	 * @param metaAnnotationTypes the names of meta-annotations on the given annotation
	 * @param attributes the map of attributes for the given annotation
	 * @return whether the annotation qualifies as a stereotype with component name
	 */
	protected boolean isStereotypeWithNameValue(String annotationType,Set<String> metaAnnotationTypes, @Nullable Map<String, Object> attributes) {
		boolean isStereotype = annotationType.equals(COMPONENT_ANNOTATION_CLASSNAME) || metaAnnotationTypes.contains(COMPONENT_ANNOTATION_CLASSNAME) ||
				annotationType.equals("javax.annotation.ManagedBean") || annotationType.equals("javax.inject.Named");
		return (isStereotype && attributes != null && attributes.containsKey("value"));
	}

	/**
	 * Derive a default bean name from the given bean definition.
	 * The default implementation delegates to {@link #buildDefaultBeanName(BeanDefinition)}.
	 * @param definition the bean definition to build a bean name for
	 * @param registry the registry that the given bean definition is being registered with
	 * @return the default bean name (never {@code null})
	 */
	protected String buildDefaultBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
		return buildDefaultBeanName(definition);
	}

	/**
	 * Derive a default bean name from the given bean definition.
	 * The default implementation simply builds a decapitalized version of the short class name: e.g. "mypackage.MyJdbcDao" -> "myJdbcDao".
	 * Note that inner classes will thus have names of the form "outerClassName.InnerClassName",
	 * which because of the period in the name may be an issue if you are autowiring by name.
	 * @param definition the bean definition to build a bean name for
	 * @return the default bean name (never {@code null})
	 */
	protected String buildDefaultBeanName(BeanDefinition definition) {
		String beanClassName = definition.getBeanClassName();
		Assert.state(beanClassName != null, "No bean class name set");
		String shortClassName = ClassUtils.getShortName(beanClassName);
		return Introspector.decapitalize(shortClassName);
	}
}
