

package org.springframework.cache.jcache.interceptor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.lang.Nullable;

/**
 * Advisor driven by a {@link JCacheOperationSource}, used to include a
 * cache advice bean for methods that are cacheable.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
@SuppressWarnings("serial")
public class BeanFactoryJCacheOperationSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {

	@Nullable
	private JCacheOperationSource cacheOperationSource;

	private final JCacheOperationSourcePointcut pointcut = new JCacheOperationSourcePointcut() {
		@Override
		protected JCacheOperationSource getCacheOperationSource() {
			return cacheOperationSource;
		}
	};


	/**
	 * Set the cache operation attribute source which is used to find cache
	 * attributes. This should usually be identical to the source reference
	 * set on the cache interceptor itself.
	 */
	public void setCacheOperationSource(JCacheOperationSource cacheOperationSource) {
		this.cacheOperationSource = cacheOperationSource;
	}

	/**
	 * Set the {@link org.springframework.aop.ClassFilter} to use for this pointcut.
	 * Default is {@link org.springframework.aop.ClassFilter#TRUE}.
	 */
	public void setClassFilter(ClassFilter classFilter) {
		this.pointcut.setClassFilter(classFilter);
	}

	@Override
	public Pointcut getPointcut() {
		return this.pointcut;
	}

}
