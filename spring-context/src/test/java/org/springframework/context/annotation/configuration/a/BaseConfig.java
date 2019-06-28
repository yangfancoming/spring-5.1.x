

package org.springframework.context.annotation.configuration.a;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.configuration.PackagePrivateBeanMethodInheritanceTests.Bar;

public abstract class BaseConfig {

	// ---- reproduce ----
	@Bean
	Bar packagePrivateBar() {
		return new Bar();
	}

	public Bar reproBar() {
		return packagePrivateBar();
	}

	// ---- workaround ----
	@Bean
	protected Bar protectedBar() {
		return new Bar();
	}

	public Bar workaroundBar() {
		return protectedBar();
	}
}
