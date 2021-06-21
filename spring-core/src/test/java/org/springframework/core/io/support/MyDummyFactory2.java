package org.springframework.core.io.support;
import org.springframework.core.annotation.Order;

/**
 * Used by {@link SpringFactoriesLoaderTests}
 */
@Order(2)
public class MyDummyFactory2 implements DummyFactory {

	@Override
	public String getString() {
		return "Bar";
	}
}
