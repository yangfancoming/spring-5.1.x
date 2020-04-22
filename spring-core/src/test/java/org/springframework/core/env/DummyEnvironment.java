

package org.springframework.core.env;

public class DummyEnvironment implements Environment {

	//---------------------------------------------------------------------
	// Implementation of 【PropertyResolver】 interface
	//---------------------------------------------------------------------
	@Override
	public boolean containsProperty(String key) {
		return false;
	}

	@Override
	public String getProperty(String key) {
		return null;
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return null;
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType) {
		return null;
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
		return null;
	}

	@Override
	public String getRequiredProperty(String key) throws IllegalStateException {
		return null;
	}

	@Override
	public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
		return null;
	}


	//---------------------------------------------------------------------
	// Implementation of 【Environment】 interface
	//---------------------------------------------------------------------
	@Override
	public String resolvePlaceholders(String text) {
		return null;
	}

	@Override
	public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
		return null;
	}

	@Override
	public String[] getActiveProfiles() {
		return null;
	}

	@Override
	public String[] getDefaultProfiles() {
		return null;
	}

	@Deprecated
	@Override
	public boolean acceptsProfiles(String... profiles) {
		return false;
	}

	@Override
	public boolean acceptsProfiles(Profiles profiles) {
		return false;
	}

}
