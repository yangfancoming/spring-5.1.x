

package org.springframework.web.servlet.view;


public class ResourceBundleViewResolverNoCacheTests extends ResourceBundleViewResolverTests {

	@Override
	protected boolean getCache() {
		return false;
	}

}
