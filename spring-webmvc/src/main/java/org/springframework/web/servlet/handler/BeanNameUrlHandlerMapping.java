

package org.springframework.web.servlet.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * Implementation of the {@link org.springframework.web.servlet.HandlerMapping}
 * interface that map from URLs to beans with names that start with a slash ("/"),similar to how Struts maps URLs to action names.
 *
 * This is the default implementation used by the {@link org.springframework.web.servlet.DispatcherServlet},
 * along with {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping}.
 * Alternatively, {@link SimpleUrlHandlerMapping} allows for customizing a handler mapping declaratively.
 *
 * The mapping is from URL to bean name.
 * Thus an incoming URL "/foo" would map to a handler named "/foo", or to "/foo /foo2" in case of multiple mappings to a single handler.
 * Note: In XML definitions, you'll need to use an alias name="/foo" in the bean definition, as the XML id may not contain slashes.
 *
 * Supports direct matches (given "/test" -> registered "/test") and "*" matches (given "/test" -> registered "/t*").
 *  Note that the default is to map within the current servlet mapping if applicable; see the {@link #setAlwaysUseFullPath "alwaysUseFullPath"} property for details.
 * For details on the pattern options, see the {@link org.springframework.util.AntPathMatcher} javadoc.
 * @see SimpleUrlHandlerMapping
 *
 * 实现HandlerMapping接口，将url与handler bean进行映射，bean的name属性需以"/"开头
 */
public class BeanNameUrlHandlerMapping extends AbstractDetectingUrlHandlerMapping {

	/**
	 * Checks name and aliases of the given bean for URLs, starting with "/". SampleController
	 */
	@Override
	protected String[] determineUrlsForHandler(String beanName) {
		List<String> urls = new ArrayList<>();
		if (beanName.startsWith("/")) urls.add(beanName);
		String[] aliases = obtainApplicationContext().getAliases(beanName);
		for (String alias : aliases) {
			if (alias.startsWith("/")) urls.add(alias);
		}
		return StringUtils.toStringArray(urls);
	}

}
