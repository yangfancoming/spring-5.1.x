

package org.springframework.beans.support;

import java.beans.PropertyEditor;
import java.lang.reflect.Method;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;

/**
 * Subclass of {@link MethodInvoker} that tries to convert the given
 * arguments for the actual target method via a {@link TypeConverter}.
 *
 * Supports flexible argument conversions, in particular for
 * invoking a specific overloaded method.
 *

 * @since 1.1
 * @see org.springframework.beans.BeanWrapperImpl#convertIfNecessary
 */
public class ArgumentConvertingMethodInvoker extends MethodInvoker {

	@Nullable
	private TypeConverter typeConverter;

	private boolean useDefaultConverter = true;


	/**
	 * Set a TypeConverter to use for argument type conversion.
	 * Default is a {@link org.springframework.beans.SimpleTypeConverter}.
	 * Can be overridden with any TypeConverter implementation, typically
	 * a pre-configured SimpleTypeConverter or a BeanWrapperImpl instance.
	 * @see org.springframework.beans.SimpleTypeConverter
	 * @see org.springframework.beans.BeanWrapperImpl
	 */
	public void setTypeConverter(@Nullable TypeConverter typeConverter) {
		this.typeConverter = typeConverter;
		this.useDefaultConverter = (typeConverter == null);
	}

	/**
	 * Return the TypeConverter used for argument type conversion.
	 * Can be cast to {@link org.springframework.beans.PropertyEditorRegistry}
	 * if direct access to the underlying PropertyEditors is desired
	 * (provided that the present TypeConverter actually implements the
	 * PropertyEditorRegistry interface).
	 */
	@Nullable
	public TypeConverter getTypeConverter() {
		if (this.typeConverter == null && this.useDefaultConverter) {
			this.typeConverter = getDefaultTypeConverter();
		}
		return this.typeConverter;
	}

	/**
	 * Obtain the default TypeConverter for this method invoker.
	 * Called if no explicit TypeConverter has been specified.
	 * The default implementation builds a
	 * {@link org.springframework.beans.SimpleTypeConverter}.
	 * Can be overridden in subclasses.
	 */
	protected TypeConverter getDefaultTypeConverter() {
		return new SimpleTypeConverter();
	}

	/**
	 * Register the given custom property editor for all properties of the given type.
	 * Typically used in conjunction with the default
	 * {@link org.springframework.beans.SimpleTypeConverter}; will work with any
	 * TypeConverter that implements the PropertyEditorRegistry interface as well.
	 * @param requiredType type of the property
	 * @param propertyEditor editor to register
	 * @see #setTypeConverter
	 * @see org.springframework.beans.PropertyEditorRegistry#registerCustomEditor
	 */
	public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
		TypeConverter converter = getTypeConverter();
		if (!(converter instanceof PropertyEditorRegistry)) {
			throw new IllegalStateException(
					"TypeConverter does not implement PropertyEditorRegistry interface: " + converter);
		}
		((PropertyEditorRegistry) converter).registerCustomEditor(requiredType, propertyEditor);
	}


	/**
	 * This implementation looks for a method with matching parameter types.
	 * @see #doFindMatchingMethod
	 */
	@Override
	protected Method findMatchingMethod() {
		Method matchingMethod = super.findMatchingMethod();
		// Second pass: look for method where arguments can be converted to parameter types.
		if (matchingMethod == null) {
			// Interpret argument array as individual method arguments.
			matchingMethod = doFindMatchingMethod(getArguments());
		}
		if (matchingMethod == null) {
			// Interpret argument array as single method argument of array type.
			matchingMethod = doFindMatchingMethod(new Object[] {getArguments()});
		}
		return matchingMethod;
	}

	/**
	 * Actually find a method with matching parameter type, i.e. where each
	 * argument value is assignable to the corresponding parameter type.
	 * @param arguments the argument values to match against method parameters
	 * @return a matching method, or {@code null} if none
	 */
	@Nullable
	protected Method doFindMatchingMethod(Object[] arguments) {
		TypeConverter converter = getTypeConverter();
		if (converter != null) {
			String targetMethod = getTargetMethod();
			Method matchingMethod = null;
			int argCount = arguments.length;
			Class<?> targetClass = getTargetClass();
			Assert.state(targetClass != null, "No target class set");
			Method[] candidates = ReflectionUtils.getAllDeclaredMethods(targetClass);
			int minTypeDiffWeight = Integer.MAX_VALUE;
			Object[] argumentsToUse = null;
			for (Method candidate : candidates) {
				if (candidate.getName().equals(targetMethod)) {
					// Check if the inspected method has the correct number of parameters.
					Class<?>[] paramTypes = candidate.getParameterTypes();
					if (paramTypes.length == argCount) {
						Object[] convertedArguments = new Object[argCount];
						boolean match = true;
						for (int j = 0; j < argCount && match; j++) {
							// Verify that the supplied argument is assignable to the method parameter.
							try {
								convertedArguments[j] = converter.convertIfNecessary(arguments[j], paramTypes[j]);
							}
							catch (TypeMismatchException ex) {
								// Ignore -> simply doesn't match.
								match = false;
							}
						}
						if (match) {
							int typeDiffWeight = getTypeDifferenceWeight(paramTypes, convertedArguments);
							if (typeDiffWeight < minTypeDiffWeight) {
								minTypeDiffWeight = typeDiffWeight;
								matchingMethod = candidate;
								argumentsToUse = convertedArguments;
							}
						}
					}
				}
			}
			if (matchingMethod != null) {
				setArguments(argumentsToUse);
				return matchingMethod;
			}
		}
		return null;
	}

}
