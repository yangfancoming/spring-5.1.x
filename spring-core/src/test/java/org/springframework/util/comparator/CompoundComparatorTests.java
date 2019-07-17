

package org.springframework.util.comparator;

import java.util.Comparator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test for {@link CompoundComparator}.
 *
 * @author Keith Donald

 * @author Phillip Webb
 */
@Deprecated
public class CompoundComparatorTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void shouldNeedAtLeastOneComparator() {
		Comparator<String> c = new CompoundComparator<>();
		thrown.expect(IllegalStateException.class);
		c.compare("foo", "bar");
	}

}
