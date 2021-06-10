
package org.springframework.web.servlet.support;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import java.util.*;

class MockFilterRegistration implements Dynamic {

	private boolean asyncSupported = false;

	private Map<String, EnumSet<DispatcherType>> mappings = new HashMap<>();


	public Map<String, EnumSet<DispatcherType>> getMappings() {
		return this.mappings;
	}

	public boolean isAsyncSupported() {
		return this.asyncSupported;
	}

	@Override
	public void setAsyncSupported(boolean isAsyncSupported) {
		this.asyncSupported = isAsyncSupported;
	}

	@Override
	public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes,
			boolean isMatchAfter, String... servletNames) {

		for (String servletName : servletNames) {
			this.mappings.put(servletName, dispatcherTypes);
		}
	}

	// Not implemented

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Collection<String> getServletNameMappings() {
		return null;
	}

	@Override
	public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes,
			boolean isMatchAfter, String... urlPatterns) {
	}

	@Override
	public Collection<String> getUrlPatternMappings() {
		return null;
	}

	@Override
	public String getClassName() {
		return null;
	}

	@Override
	public boolean setInitParameter(String name, String value) {
		return false;
	}

	@Override
	public String getInitParameter(String name) {
		return null;
	}

	@Override
	public Set<String> setInitParameters(Map<String, String> initParameters) {
		return null;
	}

	@Override
	public Map<String, String> getInitParameters() {
		return null;
	}

}
