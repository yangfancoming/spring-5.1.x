

package org.springframework.test.context.transaction.ejb.dao;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * EJB implementation of {@link TestEntityDao} which declares transaction
 * semantics for {@link #incrementCount(String)} with
 * {@link TransactionAttributeType#REQUIRES_NEW}.
 *
 * @author Sam Brannen
 * @author Xavier Detant
 * @since 4.0.1
 * @see RequiredEjbTxTestEntityDao
 */
@Stateless
@Local(TestEntityDao.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class RequiresNewEjbTxTestEntityDao extends AbstractEjbTxTestEntityDao {

	@Override
	public int getCount(String name) {
		return super.getCountInternal(name);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Override
	public int incrementCount(String name) {
		return super.incrementCountInternal(name);
	}

}
