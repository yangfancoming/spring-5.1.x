

package org.springframework.core.type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

/**
 * {@link MethodMetadata} implementation that uses standard reflection to introspect a given {@code Method}.
 * @since 3.0
 */
public class StandardMethodMetadata implements MethodMetadata {

	private final Method introspectedMethod;

	private final boolean nestedAnnotationsAsMap;

	/**
	 * Create a new StandardMethodMetadata wrapper for the given Method.
	 * @param introspectedMethod the Method to introspect
	 */
	public StandardMethodMetadata(Method introspectedMethod) {
		this(introspectedMethod, false);
	}

	/**
	 * Create a new StandardMethodMetadata wrapper for the given Method,providing the option to return any nested annotations or annotation arrays in the
	 * form of {@link org.springframework.core.annotation.AnnotationAttributes} instead of actual {@link java.lang.annotation.Annotation} instances.
	 * @param introspectedMethod the Method to introspect
	 * @param nestedAnnotationsAsMap return nested annotations and annotation arrays as
	 * {@link org.springframework.core.annotation.AnnotationAttributes} for compatibility with ASM-based {@link AnnotationMetadata} implementations
	 * @since 3.1.1
	 */
	public StandardMethodMetadata(Method introspectedMethod, boolean nestedAnnotationsAsMap) {
		Assert.notNull(introspectedMethod, "Method must not be null");
		this.introspectedMethod = introspectedMethod;
		this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
	}

	//  Return the underlying Method.
	public final Method getIntrospectedMethod() {
		return introspectedMethod;
	}

	//---------------------------------------------------------------------
	// Implementation of 【MethodMetadata】 interface
	//---------------------------------------------------------------------
	@Override
	public String getMethodName() {
		return introspectedMethod.getName();
	}

	@Override
	public String getDeclaringClassName() {
		return introspectedMethod.getDeclaringClass().getName();
	}

	@Override
	public String getReturnTypeName() {
		return introspectedMethod.getReturnType().getName();
	}

	@Override
	public boolean isAbstract() {
		return Modifier.isAbstract(introspectedMethod.getModifiers());
	}

	@Override
	public boolean isStatic() {
		return Modifier.isStatic(introspectedMethod.getModifiers());
	}

	@Override
	public boolean isFinal() {
		return Modifier.isFinal(introspectedMethod.getModifiers());
	}

	@Override
	public boolean isOverridable() {
		return (!isStatic() && !isFinal() && !Modifier.isPrivate(introspectedMethod.getModifiers()));
	}


	//---------------------------------------------------------------------
	// Implementation of 【AnnotatedTypeMetadata】 interface
	//---------------------------------------------------------------------
	@Override
	public boolean isAnnotated(String annotationName) {
		return AnnotatedElementUtils.isAnnotated(introspectedMethod, annotationName);
	}

	@Override
	@Nullable
	public Map<String, Object> getAnnotationAttributes(String annotationName) {
		return getAnnotationAttributes(annotationName, false);
	}

	@Override
	@Nullable
	public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		return AnnotatedElementUtils.getMergedAnnotationAttributes(introspectedMethod,annotationName, classValuesAsString, nestedAnnotationsAsMap);
	}

	@Override
	@Nullable
	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
		return getAllAnnotationAttributes(annotationName, false);
	}

	@Override
	@Nullable
	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		return AnnotatedElementUtils.getAllAnnotationAttributes(introspectedMethod,annotationName, classValuesAsString, nestedAnnotationsAsMap);
	}
}
