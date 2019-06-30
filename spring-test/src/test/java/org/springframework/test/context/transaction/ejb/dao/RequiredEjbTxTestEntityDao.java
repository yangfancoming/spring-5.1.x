

package org.springframework.test.context.transaction.ejb.dao;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * EJB implementation of {@link TestEntityDao} which declares transaction
 * semantics for {@link #incrementCount(String)} with
 * {@link TransactionAttributeType#REQUIRED}.
 *
 * @author Sam Brannen
 * @author Xavier Detant
 * @since 4.0.1
 * @see RequiresNewEjbTxTestEntityDao
 */
@Stateless
@Local(TestEntityDao.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class RequiredEjbTxTestEntityDao extends AbstractEjbTxTestEntityDao {

	@Override
	public int getCount(String name) {
		return super.getCountInternal(name);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public int incrementCount(String name) {
		return super.incrementCountInternal(name);
	}

}
