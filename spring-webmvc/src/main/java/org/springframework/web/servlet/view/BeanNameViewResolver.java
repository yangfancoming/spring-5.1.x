

package org.springframework.web.servlet.view;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * A simple implementation of {@link org.springframework.web.servlet.ViewResolver}
 * that interprets a view name as a bean name in the current application context,
 * i.e. typically in the XML file of the executing {@code DispatcherServlet}.
 *
 * This resolver can be handy for small applications, keeping all definitions
 * ranging from controllers to views in the same place. For larger applications,
 * {@link XmlViewResolver} will be the better choice, as it separates the XML
 * view bean definitions into a dedicated views file.
 *
 * Note: Neither this {@code ViewResolver} nor {@link XmlViewResolver} supports
 * internationalization. Consider {@link ResourceBundleViewResolver} if you need
 * to apply different view resources per locale.
 *
 * Note: This {@code ViewResolver} implements the {@link Ordered} interface
 * in order to allow for flexible participation in {@code ViewResolver} chaining.
 * For example, some special views could be defined via this {@code ViewResolver}
 * (giving it 0 as "order" value), while all remaining views could be resolved by a {@link UrlBasedViewResolver}.
 * @since 18.06.2003
 * @see XmlViewResolver
 * @see ResourceBundleViewResolver
 * @see UrlBasedViewResolver
 */
public class BeanNameViewResolver extends WebApplicationObjectSupport implements ViewResolver, Ordered {

	private int order = Ordered.LOWEST_PRECEDENCE;  // default: same as non-Ordered

	/**
	 * Specify the order value for this ViewResolver bean.
	 * The default value is {@code Ordered.LOWEST_PRECEDENCE}, meaning non-ordered.
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	@Nullable
	public View resolveViewName(String viewName, Locale locale) throws BeansException {
		ApplicationContext context = obtainApplicationContext();
		if (!context.containsBean(viewName)) {
			// Allow for ViewResolver chaining...
			return null;
		}
		if (!context.isTypeMatch(viewName, View.class)) {
			if (logger.isDebugEnabled()) logger.debug("Found bean named '" + viewName + "' but it does not implement View");
			// Since we're looking into the general ApplicationContext here,
			// let's accept this as a non-match and allow for chaining as well...
			return null;
		}
		return context.getBean(viewName, View.class);
	}

}
