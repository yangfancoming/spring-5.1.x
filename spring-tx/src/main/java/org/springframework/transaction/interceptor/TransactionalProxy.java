

package org.springframework.transaction.interceptor;

import org.springframework.aop.SpringProxy;

/**
 * A marker interface for manually created transactional proxies.
 *
 * {@link TransactionAttributeSourcePointcut} will ignore such existing
 * transactional proxies during AOP auto-proxying and therefore avoid
 * re-processing transaction metadata on them.
 *

 * @since 4.1.7
 */
public interface TransactionalProxy extends SpringProxy {

}
