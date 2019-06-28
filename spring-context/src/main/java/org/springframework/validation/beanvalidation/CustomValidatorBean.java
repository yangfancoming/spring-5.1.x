

package org.springframework.validation.beanvalidation;

import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

/**
 * Configurable bean class that exposes a specific JSR-303 Validator
 * through its original interface as well as through the Spring
 * {@link org.springframework.validation.Validator} interface.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
public class CustomValidatorBean extends SpringValidatorAdapter implements Validator, InitializingBean {

	@Nullable
	private ValidatorFactory validatorFactory;

	@Nullable
	private MessageInterpolator messageInterpolator;

	@Nullable
	private TraversableResolver traversableResolver;


	/**
	 * Set the ValidatorFactory to obtain the target Validator from.
	 * <p>Default is {@link javax.validation.Validation#buildDefaultValidatorFactory()}.
	 */
	public void setValidatorFactory(ValidatorFactory validatorFactory) {
		this.validatorFactory = validatorFactory;
	}

	/**
	 * Specify a custom MessageInterpolator to use for this Validator.
	 */
	public void setMessageInterpolator(MessageInterpolator messageInterpolator) {
		this.messageInterpolator = messageInterpolator;
	}

	/**
	 * Specify a custom TraversableResolver to use for this Validator.
	 */
	public void setTraversableResolver(TraversableResolver traversableResolver) {
		this.traversableResolver = traversableResolver;
	}


	@Override
	public void afterPropertiesSet() {
		if (this.validatorFactory == null) {
			this.validatorFactory = Validation.buildDefaultValidatorFactory();
		}

		ValidatorContext validatorContext = this.validatorFactory.usingContext();
		MessageInterpolator targetInterpolator = this.messageInterpolator;
		if (targetInterpolator == null) {
			targetInterpolator = this.validatorFactory.getMessageInterpolator();
		}
		validatorContext.messageInterpolator(new LocaleContextMessageInterpolator(targetInterpolator));
		if (this.traversableResolver != null) {
			validatorContext.traversableResolver(this.traversableResolver);
		}

		setTargetValidator(validatorContext.getValidator());
	}

}
