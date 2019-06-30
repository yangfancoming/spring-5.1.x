

package org.springframework.test.context.junit4.profile.annotation;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ActiveProfilesResolver;

/**
 * @author Michail Nikolaev
 * @since 4.0
 */
@ActiveProfiles(resolver = DevProfileResolverAnnotationConfigTests.class, inheritProfiles = false)
public class DevProfileResolverAnnotationConfigTests extends DevProfileAnnotationConfigTests implements
		ActiveProfilesResolver {

	@Override
	public String[] resolve(Class<?> testClass) {
		return new String[] { "dev" };
	}
}
