

package org.springframework.util.comparator;

import java.util.Comparator;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Tests for {@link InvertibleComparator}.
 *
 * @author Keith Donald

 * @author Phillip Webb
 */
@Deprecated
public class InvertibleComparatorTests {

	private final Comparator<Integer> comparator = new ComparableComparator<>();


	@Test(expected = IllegalArgumentException.class)
	public void shouldNeedComparator() throws Exception {
		new InvertibleComparator<>(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNeedComparatorWithAscending() throws Exception {
		new InvertibleComparator<>(null, true);
	}

	@Test
	public void shouldDefaultToAscending() throws Exception {
		InvertibleComparator<Integer> invertibleComparator = new InvertibleComparator<>(comparator);
		assertThat(invertibleComparator.isAscending(), is(true));
		assertThat(invertibleComparator.compare(1, 2), is(-1));
	}

	@Test
	public void shouldInvert() throws Exception {
		InvertibleComparator<Integer> invertibleComparator = new InvertibleComparator<>(comparator);
		assertThat(invertibleComparator.isAscending(), is(true));
		assertThat(invertibleComparator.compare(1, 2), is(-1));
		invertibleComparator.invertOrder();
		assertThat(invertibleComparator.isAscending(), is(false));
		assertThat(invertibleComparator.compare(1, 2), is(1));
	}

	@Test
	public void shouldCompareAscending() throws Exception {
		InvertibleComparator<Integer> invertibleComparator = new InvertibleComparator<>(comparator, true);
		assertThat(invertibleComparator.compare(1, 2), is(-1));
	}

	@Test
	public void shouldCompareDescending() throws Exception {
		InvertibleComparator<Integer> invertibleComparator = new InvertibleComparator<>(comparator, false);
		assertThat(invertibleComparator.compare(1, 2), is(1));
	}

}
