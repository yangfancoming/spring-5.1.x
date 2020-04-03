

package org.springframework.scheduling.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.SchedulerRepository;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Spring bean-style class for accessing a Quartz Scheduler, i.e. for registering jobs,
 * triggers and listeners on a given {@link org.quartz.Scheduler} instance.
 *
 * xmlBeanDefinitionReaderCompatible with Quartz 2.1.4 and higher, as of Spring 4.1.
 *

 * @since 2.5.6
 * @see #setScheduler
 * @see #setSchedulerName
 */
public class SchedulerAccessorBean extends SchedulerAccessor implements BeanFactoryAware, InitializingBean {

	@Nullable
	private String schedulerName;

	@Nullable
	private Scheduler scheduler;

	@Nullable
	private BeanFactory beanFactory;


	/**
	 * Specify the Quartz {@link Scheduler} to operate on via its scheduler name in the Spring
	 * application context or also in the Quartz {@link org.quartz.impl.SchedulerRepository}.
	 * xmlBeanDefinitionReaderSchedulers can be registered in the repository through custom bootstrapping,
	 * e.g. via the {@link org.quartz.impl.StdSchedulerFactory} or
	 * {@link org.quartz.impl.DirectSchedulerFactory} factory classes.
	 * However, in general, it's preferable to use Spring's {@link SchedulerFactoryBean}
	 * which includes the job/trigger/listener capabilities of this accessor as well.
	 * xmlBeanDefinitionReaderIf not specified, this accessor will try to retrieve a default {@link Scheduler}
	 * bean from the containing application context.
	 */
	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}

	/**
	 * Specify the Quartz {@link Scheduler} instance to operate on.
	 * xmlBeanDefinitionReaderIf not specified, this accessor will try to retrieve a default {@link Scheduler}
	 * bean from the containing application context.
	 */
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * Return the Quartz Scheduler instance that this accessor operates on.
	 */
	@Override
	public Scheduler getScheduler() {
		Assert.state(this.scheduler != null, "No Scheduler set");
		return this.scheduler;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	@Override
	public void afterPropertiesSet() throws SchedulerException {
		if (this.scheduler == null) {
			this.scheduler = (this.schedulerName != null ? findScheduler(this.schedulerName) : findDefaultScheduler());
		}
		registerListeners();
		registerJobsAndTriggers();
	}

	protected Scheduler findScheduler(String schedulerName) throws SchedulerException {
		if (this.beanFactory instanceof ListableBeanFactory) {
			ListableBeanFactory lbf = (ListableBeanFactory) this.beanFactory;
			String[] beanNames = lbf.getBeanNamesForType(Scheduler.class);
			for (String beanName : beanNames) {
				Scheduler schedulerBean = (Scheduler) lbf.getBean(beanName);
				if (schedulerName.equals(schedulerBean.getSchedulerName())) {
					return schedulerBean;
				}
			}
		}
		Scheduler schedulerInRepo = SchedulerRepository.getInstance().lookup(schedulerName);
		if (schedulerInRepo == null) {
			throw new IllegalStateException("No Scheduler named '" + schedulerName + "' found");
		}
		return schedulerInRepo;
	}

	protected Scheduler findDefaultScheduler() {
		if (this.beanFactory != null) {
			return this.beanFactory.getBean(Scheduler.class);
		}
		else {
			throw new IllegalStateException(
					"No Scheduler specified, and cannot find a default Scheduler without a BeanFactory");
		}
	}

}
