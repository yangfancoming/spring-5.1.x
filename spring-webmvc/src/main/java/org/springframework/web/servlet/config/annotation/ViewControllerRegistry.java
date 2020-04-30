

package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

/**
 * Assists with the registration of simple automated controllers pre-configured with status code and/or a view.
 * @since 3.1
 */
public class ViewControllerRegistry {

	@Nullable
	private ApplicationContext applicationContext;

	private final List<ViewControllerRegistration> registrations = new ArrayList<>(4);

	private final List<RedirectViewControllerRegistration> redirectRegistrations = new ArrayList<>(10);

	private int order = 1;

	/**
	 * Class constructor with {@link ApplicationContext}.
	 * @since 4.3.12
	 */
	public ViewControllerRegistry(@Nullable ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Map a view controller to the given URL path (or pattern) in order to render
	 * a response with a pre-configured status code and view.
	 * Patterns like {@code "/admin/**"} or {@code "/articles/{articlename:\\w+}"}
	 * are allowed. See {@link org.springframework.util.AntPathMatcher} for more details on the
	 * syntax.
	 */
	public ViewControllerRegistration addViewController(String urlPath) {
		ViewControllerRegistration registration = new ViewControllerRegistration(urlPath);
		registration.setApplicationContext(applicationContext);
		registrations.add(registration);
		return registration;
	}

	/**
	 * Map a view controller to the given URL path (or pattern) in order to redirect to another URL.
	 * By default the redirect URL is expected to be relative to the current ServletContext, i.e. as relative to the web application root.
	 * @since 4.1
	 */
	public RedirectViewControllerRegistration addRedirectViewController(String urlPath, String redirectUrl) {
		RedirectViewControllerRegistration registration = new RedirectViewControllerRegistration(urlPath, redirectUrl);
		registration.setApplicationContext(applicationContext);
		redirectRegistrations.add(registration);
		return registration;
	}

	/**
	 * Map a simple controller to the given URL path (or pattern) in order to set the response status to the given code without rendering a body.
	 * @since 4.1
	 */
	public void addStatusController(String urlPath, HttpStatus statusCode) {
		ViewControllerRegistration registration = new ViewControllerRegistration(urlPath);
		registration.setApplicationContext(applicationContext);
		registration.setStatusCode(statusCode);
		registration.getViewController().setStatusOnly(true);
		registrations.add(registration);
	}

	/**
	 * Specify the order to use for the {@code HandlerMapping} used to map view
	 * controllers relative to other handler mappings configured in Spring MVC.
	 * By default this is set to 1, i.e. right after annotated controllers, which are ordered at 0.
	 */
	public void setOrder(int order) {
		this.order = order;
	}


	/**
	 * Return the {@code HandlerMapping} that contains the registered view controller mappings, or {@code null} for no registrations.
	 * @since 4.3.12
	 */
	@Nullable
	protected SimpleUrlHandlerMapping buildHandlerMapping() {
		if (registrations.isEmpty() && redirectRegistrations.isEmpty()) return null;

		Map<String, Object> urlMap = new LinkedHashMap<>();
		for (ViewControllerRegistration registration : registrations) {
			urlMap.put(registration.getUrlPath(), registration.getViewController());
		}
		for (RedirectViewControllerRegistration registration : redirectRegistrations) {
			urlMap.put(registration.getUrlPath(), registration.getViewController());
		}

		SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
		handlerMapping.setUrlMap(urlMap);
		handlerMapping.setOrder(order);
		return handlerMapping;
	}

}
