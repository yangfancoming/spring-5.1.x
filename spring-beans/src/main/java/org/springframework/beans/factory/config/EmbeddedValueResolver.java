

package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * {@link StringValueResolver} adapter for resolving placeholders and
 * expressions against a {@link ConfigurableBeanFactory}.
 *
 * <p>Note that this adapter resolves expressions as well, in contrast
 * to the {@link ConfigurableBeanFactory#resolveEmbeddedValue} method.
 * The {@link BeanExpressionContext} used is for the plain bean factory,
 * with no scope specified for any contextual objects to access.
 *
 * @author Juergen Hoeller
 * @since 4.3
 * @see ConfigurableBeanFactory#resolveEmbeddedValue(String)
 * @see ConfigurableBeanFactory#getBeanExpressionResolver()
 * @see BeanExpressionContext
 */
public class EmbeddedValueResolver implements StringValueResolver {

	private final BeanExpressionContext exprContext;

	@Nullable
	private final BeanExpressionResolver exprResolver;


	public EmbeddedValueResolver(ConfigurableBeanFactory beanFactory) {
		this.exprContext = new BeanExpressionContext(beanFactory, null);
		this.exprResolver = beanFactory.getBeanExpressionResolver();
	}


	@Override
	@Nullable
	public String resolveStringValue(String strVal) {
		String value = this.exprContext.getBeanFactory().resolveEmbeddedValue(strVal);
		if (this.exprResolver != null && value != null) {
			Object evaluated = this.exprResolver.evaluate(value, this.exprContext);
			value = (evaluated != null ? evaluated.toString() : null);
		}
		return value;
	}

}
