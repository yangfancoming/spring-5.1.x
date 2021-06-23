

package org.springframework.context.annotation;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Extension of the {@link org.springframework.beans.factory.support.GenericBeanDefinition} class, based on an ASM ClassReader,
 * with support for annotation metadata exposed  through the {@link AnnotatedBeanDefinition} interface.
 * This class does <i>not</i> load the bean {@code Class} early.
 * It rather retrieves all relevant metadata from the ".class" file itself,parsed with the ASM ClassReader.
 * It is functionally equivalent to  {@link AnnotatedGenericBeanDefinition#AnnotatedGenericBeanDefinition(AnnotationMetadata)}
 * but distinguishes by type beans that have been <em>scanned</em> vs those that have been otherwise registered or detected by other means.
 * @since 2.5
 * @see #getMetadata()
 * @see #getBeanClassName()
 * @see org.springframework.core.type.classreading.MetadataReaderFactory
 * @see AnnotatedGenericBeanDefinition
 * 存储@Component、@Service、@Controller等注解注释的类
 * 用于存储被包扫描到的bean定义
 */
@SuppressWarnings("serial")
public class ScannedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

	private final AnnotationMetadata metadata;

	/**
	 * Create a new ScannedGenericBeanDefinition for the class that the given MetadataReader describes.
	 * @param metadataReader the MetadataReader for the scanned target class
	 *  它只有一个构造函数：必须传入MetadataReader
	 */
	public ScannedGenericBeanDefinition(MetadataReader metadataReader) {
		Assert.notNull(metadataReader, "MetadataReader must not be null");
		metadata = metadataReader.getAnnotationMetadata();
		setBeanClassName(metadata.getClassName());
	}

	@Override
	public final AnnotationMetadata getMetadata() {
		return metadata;
	}

	@Override
	@Nullable
	public MethodMetadata getFactoryMethodMetadata() {
		return null;
	}
}
