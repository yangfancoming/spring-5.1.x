

package org.springframework.beans.factory.config;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for {@link CustomScopeConfigurer}.
 *
 * @author Rick Evans
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class CustomScopeConfigurerTests {

	private static final String FOO_SCOPE = "fooScope";

	private final ConfigurableListableBeanFactory factory = new DefaultListableBeanFactory();


	@Test
	public void testWithNoScopes() {
		CustomScopeConfigurer figurer = new CustomScopeConfigurer();
		figurer.postProcessBeanFactory(factory);
	}

	@Test
	public void testSunnyDayWithBonaFideScopeInstance() {
		Scope scope = mock(Scope.class);
		factory.registerScope(FOO_SCOPE, scope);
		Map<String, Object> scopes = new HashMap<>();
		scopes.put(FOO_SCOPE, scope);
		CustomScopeConfigurer figurer = new CustomScopeConfigurer();
		figurer.setScopes(scopes);
		figurer.postProcessBeanFactory(factory);
	}

	@Test
	public void testSunnyDayWithBonaFideScopeClass() {
		Map<String, Object> scopes = new HashMap<>();
		scopes.put(FOO_SCOPE, NoOpScope.class);
		CustomScopeConfigurer figurer = new CustomScopeConfigurer();
		figurer.setScopes(scopes);
		figurer.postProcessBeanFactory(factory);
		assertTrue(factory.getRegisteredScope(FOO_SCOPE) instanceof NoOpScope);
	}

	@Test
	public void testSunnyDayWithBonaFideScopeClassName() {
		Map<String, Object> scopes = new HashMap<>();
		scopes.put(FOO_SCOPE, NoOpScope.class.getName());
		CustomScopeConfigurer figurer = new CustomScopeConfigurer();
		figurer.setScopes(scopes);
		figurer.postProcessBeanFactory(factory);
		assertTrue(factory.getRegisteredScope(FOO_SCOPE) instanceof NoOpScope);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWhereScopeMapHasNullScopeValueInEntrySet() {
		Map<String, Object> scopes = new HashMap<>();
		scopes.put(FOO_SCOPE, null);
		CustomScopeConfigurer figurer = new CustomScopeConfigurer();
		figurer.setScopes(scopes);
		figurer.postProcessBeanFactory(factory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWhereScopeMapHasNonScopeInstanceInEntrySet() {
		Map<String, Object> scopes = new HashMap<>();
		scopes.put(FOO_SCOPE, this);  // <-- not a valid value...
		CustomScopeConfigurer figurer = new CustomScopeConfigurer();
		figurer.setScopes(scopes);
		figurer.postProcessBeanFactory(factory);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ClassCastException.class)
	public void testWhereScopeMapHasNonStringTypedScopeNameInKeySet() {
		Map scopes = new HashMap();
		scopes.put(this, new NoOpScope());  // <-- not a valid value (the key)...
		CustomScopeConfigurer figurer = new CustomScopeConfigurer();
		figurer.setScopes(scopes);
		figurer.postProcessBeanFactory(factory);
	}

}
