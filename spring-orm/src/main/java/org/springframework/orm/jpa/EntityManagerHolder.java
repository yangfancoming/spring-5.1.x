

package org.springframework.orm.jpa;

import javax.persistence.EntityManager;

import org.springframework.lang.Nullable;
import org.springframework.transaction.SavepointManager;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.util.Assert;

/**
 * Resource holder wrapping a JPA {@link EntityManager}.
 * {@link JpaTransactionManager} binds instances of this class to the thread,
 * for a given {@link javax.persistence.EntityManagerFactory}.
 *
 * xmlBeanDefinitionReaderAlso serves as a base class for {@link org.springframework.orm.hibernate5.SessionHolder},
 * as of 5.1.
 *
 * xmlBeanDefinitionReaderNote: This is an SPI class, not intended to be used by applications.
 *

 * @since 2.0
 * @see JpaTransactionManager
 * @see EntityManagerFactoryUtils
 */
public class EntityManagerHolder extends ResourceHolderSupport {

	@Nullable
	private final EntityManager entityManager;

	private boolean transactionActive;

	@Nullable
	private SavepointManager savepointManager;


	public EntityManagerHolder(@Nullable EntityManager entityManager) {
		this.entityManager = entityManager;
	}


	public EntityManager getEntityManager() {
		Assert.state(this.entityManager != null, "No EntityManager available");
		return this.entityManager;
	}

	protected void setTransactionActive(boolean transactionActive) {
		this.transactionActive = transactionActive;
	}

	protected boolean isTransactionActive() {
		return this.transactionActive;
	}

	protected void setSavepointManager(@Nullable SavepointManager savepointManager) {
		this.savepointManager = savepointManager;
	}

	@Nullable
	protected SavepointManager getSavepointManager() {
		return this.savepointManager;
	}


	@Override
	public void clear() {
		super.clear();
		this.transactionActive = false;
		this.savepointManager = null;
	}

}
