

package org.springframework.web.servlet.mvc.method.annotation;

import java.util.Map;
import javax.servlet.ServletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Subclass of {@link ServletRequestDataBinder} that adds URI template variables to the values used for data binding.
 * @since 3.1
 */
public class ExtendedServletRequestDataBinder extends ServletRequestDataBinder {

	/**
	 * Create a new instance, with default object name.
	 * @param target the target object to bind onto (or {@code null}
	 * if the binder is just used to convert a plain parameter value)
	 * @see #DEFAULT_OBJECT_NAME
	 */
	public ExtendedServletRequestDataBinder(@Nullable Object target) {
		super(target);
	}

	/**
	 * Create a new instance.
	 * @param target the target object to bind onto (or {@code null}
	 * if the binder is just used to convert a plain parameter value)
	 * @param objectName the name of the target object
	 * @see #DEFAULT_OBJECT_NAME
	 */
	public ExtendedServletRequestDataBinder(@Nullable Object target, String objectName) {
		super(target, objectName);
	}


	/**
	 * Merge URI variables into the property values to use for data binding.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
		String attr = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
		Map<String, String> uriVars = (Map<String, String>) request.getAttribute(attr);
		if (uriVars != null) {
			uriVars.forEach((name, value) -> {
				if (mpvs.contains(name)) {
					if (logger.isWarnEnabled()) logger.warn("Skipping URI variable '" + name + "' because request contains bind value with same name.");
				}else {
					mpvs.addPropertyValue(name, value);
				}
			});
		}
	}

}
