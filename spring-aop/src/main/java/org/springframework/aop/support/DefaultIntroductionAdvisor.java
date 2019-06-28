

package org.springframework.aop.support;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import org.aopalliance.aop.Advice;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionInfo;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Simple {@link org.springframework.aop.IntroductionAdvisor} implementation
 * that by default applies to any class.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 11.11.2003
 */
@SuppressWarnings({"serial" })
public class DefaultIntroductionAdvisor implements IntroductionAdvisor, ClassFilter, Ordered, Serializable {

	private final Advice advice;

	private final Set<Class<?>> interfaces = new LinkedHashSet<>();

	private int order = Ordered.LOWEST_PRECEDENCE;


	/**
	 * Create a DefaultIntroductionAdvisor for the given advice.
	 * @param advice the Advice to apply (may implement the
	 * {@link org.springframework.aop.IntroductionInfo} interface)
	 * @see #addInterface
	 */
	public DefaultIntroductionAdvisor(Advice advice) {
		this(advice, (advice instanceof IntroductionInfo ? (IntroductionInfo) advice : null));
	}

	/**
	 * Create a DefaultIntroductionAdvisor for the given advice.
	 * @param advice the Advice to apply
	 * @param introductionInfo the IntroductionInfo that describes
	 * the interface to introduce (may be {@code null})
	 */
	public DefaultIntroductionAdvisor(Advice advice, @Nullable IntroductionInfo introductionInfo) {
		Assert.notNull(advice, "Advice must not be null");
		this.advice = advice;
		if (introductionInfo != null) {
			Class<?>[] introducedInterfaces = introductionInfo.getInterfaces();
			if (introducedInterfaces.length == 0) {
				throw new IllegalArgumentException("IntroductionAdviceSupport implements no interfaces");
			}
			for (Class<?> ifc : introducedInterfaces) {
				addInterface(ifc);
			}
		}
	}

	/**
	 * Create a DefaultIntroductionAdvisor for the given advice.
	 * @param advice the Advice to apply
	 * @param intf the interface to introduce
	 */
	public DefaultIntroductionAdvisor(DynamicIntroductionAdvice advice, Class<?> intf) {
		Assert.notNull(advice, "Advice must not be null");
		this.advice = advice;
		addInterface(intf);
	}


	/**
	 * Add the specified interface to the list of interfaces to introduce.
	 * @param intf the interface to introduce
	 */
	public void addInterface(Class<?> intf) {
		Assert.notNull(intf, "Interface must not be null");
		if (!intf.isInterface()) {
			throw new IllegalArgumentException("Specified class [" + intf.getName() + "] must be an interface");
		}
		this.interfaces.add(intf);
	}

	@Override
	public Class<?>[] getInterfaces() {
		return ClassUtils.toClassArray(this.interfaces);
	}

	@Override
	public void validateInterfaces() throws IllegalArgumentException {
		for (Class<?> ifc : this.interfaces) {
			if (this.advice instanceof DynamicIntroductionAdvice &&
					!((DynamicIntroductionAdvice) this.advice).implementsInterface(ifc)) {
			throw new IllegalArgumentException("DynamicIntroductionAdvice [" + this.advice + "] " +
					"does not implement interface [" + ifc.getName() + "] specified for introduction");
			}
		}
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public Advice getAdvice() {
		return this.advice;
	}

	@Override
	public boolean isPerInstance() {
		return true;
	}

	@Override
	public ClassFilter getClassFilter() {
		return this;
	}

	@Override
	public boolean matches(Class<?> clazz) {
		return true;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DefaultIntroductionAdvisor)) {
			return false;
		}
		DefaultIntroductionAdvisor otherAdvisor = (DefaultIntroductionAdvisor) other;
		return (this.advice.equals(otherAdvisor.advice) && this.interfaces.equals(otherAdvisor.interfaces));
	}

	@Override
	public int hashCode() {
		return this.advice.hashCode() * 13 + this.interfaces.hashCode();
	}

	@Override
	public String toString() {
		return ClassUtils.getShortName(getClass()) + ": advice [" + this.advice + "]; interfaces " +
				ClassUtils.classNamesToString(this.interfaces);
	}

}
