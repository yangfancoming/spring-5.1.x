

package org.springframework.orm.jpa.support;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.util.Assert;

/**
 * {@link FactoryBean} that exposes a shared JPA {@link javax.persistence.EntityManager}
 * reference for a given EntityManagerFactory. Typically used for an EntityManagerFactory
 * created by {@link org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean},
 * as direct alternative to a JNDI lookup for a Java EE server's EntityManager reference.
 *
 * xmlBeanDefinitionReaderThe shared EntityManager will behave just like an EntityManager fetched from an
 * application server's JNDI environment, as defined by the JPA specification.
 * It will delegate all calls to the current transactional EntityManager, if any;
 * otherwise, it will fall back to a newly created EntityManager per operation.
 *
 * xmlBeanDefinitionReaderCan be passed to DAOs that expect a shared EntityManager reference rather than an
 * EntityManagerFactory. Note that Spring's {@link org.springframework.orm.jpa.JpaTransactionManager}
 * always needs an EntityManagerFactory in order to create new transactional EntityManager instances.
 *

 * @since 2.0
 * @see #setEntityManagerFactory
 * @see #setEntityManagerInterface
 * @see org.springframework.orm.jpa.LocalEntityManagerFactoryBean
 * @see org.springframework.orm.jpa.JpaTransactionManager
 */
public class SharedEntityManagerBean extends EntityManagerFactoryAccessor
		implements FactoryBean<EntityManager>, InitializingBean {

	@Nullable
	private Class<? extends EntityManager> entityManagerInterface;

	private boolean synchronizedWithTransaction = true;

	@Nullable
	private EntityManager shared;


	/**
	 * Specify the EntityManager interface to expose.
	 * xmlBeanDefinitionReaderDefault is the EntityManager interface as defined by the
	 * EntityManagerFactoryInfo, if available. Else, the standard
	 * {@code javax.persistence.EntityManager} interface will be used.
	 * @see org.springframework.orm.jpa.EntityManagerFactoryInfo#getEntityManagerInterface()
	 * @see javax.persistence.EntityManager
	 */
	public void setEntityManagerInterface(Class<? extends EntityManager> entityManagerInterface) {
		Assert.notNull(entityManagerInterface, "'entityManagerInterface' must not be null");
		this.entityManagerInterface = entityManagerInterface;
	}

	/**
	 * Set whether to automatically join ongoing transactions (according
	 * to the JPA 2.1 SynchronizationType rules). Default is "true".
	 */
	public void setSynchronizedWithTransaction(boolean synchronizedWithTransaction) {
		this.synchronizedWithTransaction = synchronizedWithTransaction;
	}


	@Override
	public final void afterPropertiesSet() {
		EntityManagerFactory emf = getEntityManagerFactory();
		if (emf == null) {
			throw new IllegalArgumentException("'entityManagerFactory' or 'persistenceUnitName' is required");
		}
		if (emf instanceof EntityManagerFactoryInfo) {
			EntityManagerFactoryInfo emfInfo = (EntityManagerFactoryInfo) emf;
			if (this.entityManagerInterface == null) {
				this.entityManagerInterface = emfInfo.getEntityManagerInterface();
				if (this.entityManagerInterface == null) {
					this.entityManagerInterface = EntityManager.class;
				}
			}
		}
		else {
			if (this.entityManagerInterface == null) {
				this.entityManagerInterface = EntityManager.class;
			}
		}
		this.shared = SharedEntityManagerCreator.createSharedEntityManager(
				emf, getJpaPropertyMap(), this.synchronizedWithTransaction, this.entityManagerInterface);
	}


	@Override
	@Nullable
	public EntityManager getObject() {
		return this.shared;
	}

	@Override
	public Class<? extends EntityManager> getObjectType() {
		return (this.entityManagerInterface != null ? this.entityManagerInterface : EntityManager.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
