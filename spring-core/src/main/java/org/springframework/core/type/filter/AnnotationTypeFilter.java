

package org.springframework.core.type.filter;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/**
 * A simple filter which matches classes with a given annotation, checking inherited annotations as well.
 * The matching logic mirrors that of {@link java.lang.Class#isAnnotationPresent(Class)}.
 * @since 2.5
 */
public class AnnotationTypeFilter extends AbstractTypeHierarchyTraversingFilter {
	// 指定 Annotation 类型
	private final Class<? extends Annotation> annotationType;
	// 是否考虑元注解，默认考虑
	private final boolean considerMetaAnnotations;

	/**
	 * Create a new AnnotationTypeFilter for the given annotation type.
	 * This filter will also match meta-annotations.
	 * To disable the meta-annotation matching, use the constructor that accepts a '{@code considerMetaAnnotations}' argument.
	 * The filter will not match interfaces.
	 * @param annotationType the annotation type to match
	 */
	public AnnotationTypeFilter(Class<? extends Annotation> annotationType) {
		this(annotationType, true, false);
	}

	/**
	 * Create a new AnnotationTypeFilter for the given annotation type.
	 * The filter will not match interfaces.
	 * @param annotationType the annotation type to match
	 * @param considerMetaAnnotations whether to also match on meta-annotations
	 */
	public AnnotationTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations) {
		this(annotationType, considerMetaAnnotations, false);
	}

	/**
	 * Create a new {@link AnnotationTypeFilter} for the given annotation type.
	 * @param annotationType the annotation type to match
	 * @param considerMetaAnnotations whether to also match on meta-annotations
	 * @param considerInterfaces whether to also match interfaces
	 */
	public AnnotationTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations, boolean considerInterfaces) {
		// 是否考虑父类取决于给定注解是否标注 Inherited
		super(annotationType.isAnnotationPresent(Inherited.class), considerInterfaces);
		this.annotationType = annotationType;
		this.considerMetaAnnotations = considerMetaAnnotations;
	}

	/**
	 * Return the {@link Annotation} that this instance is using to filter candidates.
	 * @since 5.0
	 */
	public final Class<? extends Annotation> getAnnotationType() {
		return this.annotationType;
	}

	@Override
	protected boolean matchSelf(MetadataReader metadataReader) {
		// 获取当前类注解信息(当前类指componentScan指定扫描的类)  实现类为 AnnotationMetadataReadingVisitor
		// 标注有注定注解 或者 被注定元注解标注（前提是 considerMetaAnnotations = true）
		AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
		//check 注解及其派生注解中是否包含@Component
		// 获取当前类的注解 metadata.hasAnnotation    @Controller
		// 获取当前类的注解及其派生注解 metadata.hasAnnotation   @Controller包含的@Component\@Documented等等
		return metadata.hasAnnotation(this.annotationType.getName()) || (this.considerMetaAnnotations && metadata.hasMetaAnnotation(this.annotationType.getName()));
	}

	@Override
	@Nullable
	protected Boolean matchSuperClass(String superClassName) {
		return hasAnnotation(superClassName);
	}

	@Override
	@Nullable
	protected Boolean matchInterface(String interfaceName) {
		return hasAnnotation(interfaceName);
	}

	@Nullable
	protected Boolean hasAnnotation(String typeName) {
		// 目标类是 Object 则不匹配，相当于子类都没匹配
		if (Object.class.getName().equals(typeName)) {
			return false;
		}else if (typeName.startsWith("java")) { // 目标类以 java 开头
			// annotationType 不以 java 开头则不匹配
			if (!this.annotationType.getName().startsWith("java")) {
				// Standard Java types do not have non-standard annotations on them ->
				// skip any load attempt, in particular for Java language interfaces.
				return false;
			}
			try {
				// 加载目标类，根据是否考虑元注解进行匹配
				Class<?> clazz = ClassUtils.forName(typeName, getClass().getClassLoader());
				return ((this.considerMetaAnnotations ? AnnotationUtils.getAnnotation(clazz, this.annotationType) : clazz.getAnnotation(this.annotationType)) != null);
			}catch (Throwable ex) {
				// Class not regularly loadable - can't determine a match that way.
			}
		}
		return null;
	}
}
