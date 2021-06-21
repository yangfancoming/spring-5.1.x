package org.springframework.core.io.support;
import org.springframework.core.annotation.Order;

/**
 * Used by {@link SpringFactoriesLoaderTests}
 */
@Order(1)
public class MyDummyFactory1 implements DummyFactory {

	@Override
	public String getString() {
		return "Foo";
	}
}
