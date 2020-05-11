

package org.springframework.beans;
import org.springframework.lang.Nullable;

/**
 * Interface to be implemented by bean metadata elements that carry a configuration source object.
 * @since 2.0
 * 接口提供了一个getResource()方法,用来传输一个可配置的源对象。
 */
public interface BeanMetadataElement {

	/** Return the configuration source {@code Object} for this metadata element (may be {@code null}). */
	@Nullable
	Object getSource();
}
