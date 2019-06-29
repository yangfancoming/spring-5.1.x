

package org.springframework.util.comparator;

import java.util.Comparator;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Compares objects based on an arbitrary class order. Allows objects to be sorted based
 * on the types of class that they inherit, for example: this comparator can be used to
 * sort a list {@code Number}s such that {@code Long}s occur before {@code Integer}s.
 *
 * <p>Only the specified {@code instanceOrder} classes are considered during comparison.
 * If two objects are both instances of the ordered type this comparator will return a
 * {@code 0}. Consider combining with {@link Comparator#thenComparing(Comparator)}
 * if additional sorting is required.
 *
 * @author Phillip Webb
 * @since 3.2
 * @param <T> the type of objects that may be compared by this comparator
 * @see Comparator#thenComparing(Comparator)
 */
public class InstanceComparator<T> implements Comparator<T> {

	private final Class<?>[] instanceOrder;


	/**
	 * Create a new {@link InstanceComparator} instance.
	 * @param instanceOrder the ordered list of classes that should be used when comparing
	 * objects. Classes earlier in the list will be given a higher priority.
	 */
	public InstanceComparator(Class<?>... instanceOrder) {
		Assert.notNull(instanceOrder, "'instanceOrder' array must not be null");
		this.instanceOrder = instanceOrder;
	}


	@Override
	public int compare(T o1, T o2) {
		int i1 = getOrder(o1);
		int i2 = getOrder(o2);
		return (i1 < i2 ? -1 : (i1 == i2 ? 0 : 1));
	}

	private int getOrder(@Nullable T object) {
		if (object != null) {
			for (int i = 0; i < this.instanceOrder.length; i++) {
				if (this.instanceOrder[i].isInstance(object)) {
					return i;
				}
			}
		}
		return this.instanceOrder.length;
	}

}
