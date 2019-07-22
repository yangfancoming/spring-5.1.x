

package org.springframework.transaction.interceptor;

import org.springframework.core.NestedRuntimeException;

/**
 * An example {@link RuntimeException} for use in testing rollback rules.

 */
@SuppressWarnings("serial")
class MyRuntimeException extends NestedRuntimeException {
	public MyRuntimeException(String msg) {
		super(msg);
	}
}
