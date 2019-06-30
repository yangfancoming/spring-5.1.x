

package org.springframework.jndi;

import java.lang.reflect.Field;
import javax.naming.spi.NamingManager;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests for {@link JndiLocatorDelegate}.
 *
 * @author Phillip Webb
 * @author Juergen Hoeller
 */
public class JndiLocatorDelegateTests {

	@Test
	public void isDefaultJndiEnvironmentAvailableFalse() throws Exception {
		Field builderField = NamingManager.class.getDeclaredField("initctx_factory_builder");
		builderField.setAccessible(true);
		Object oldBuilder = builderField.get(null);
		builderField.set(null, null);

		try {
			assertThat(JndiLocatorDelegate.isDefaultJndiEnvironmentAvailable(), equalTo(false));
		}
		finally {
			builderField.set(null, oldBuilder);
		}
	}

}
