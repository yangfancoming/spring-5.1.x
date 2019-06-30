

package org.springframework.test.context.configuration.interfaces;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.hierarchies.standard.SingleTestClassWithTwoLevelContextHierarchyTests;

/**
 * @author Sam Brannen
 * @since 4.3
 */
@ContextHierarchy({
	@ContextConfiguration(classes = SingleTestClassWithTwoLevelContextHierarchyTests.ParentConfig.class),
	@ContextConfiguration(classes = SingleTestClassWithTwoLevelContextHierarchyTests.ChildConfig.class) })
interface ContextHierarchyTestInterface {
}
