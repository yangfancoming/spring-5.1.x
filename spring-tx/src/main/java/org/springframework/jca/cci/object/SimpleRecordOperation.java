

package org.springframework.jca.cci.object;

import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;

import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * EIS operation object that accepts a passed-in CCI input Record
 * and returns a corresponding CCI output Record.
 *

 * @since 1.2
 */
public class SimpleRecordOperation extends EisOperation {

	/**
	 * Constructor that allows use as a JavaBean.
	 */
	public SimpleRecordOperation() {
	}

	/**
	 * Convenient constructor with ConnectionFactory and specifications
	 * (connection and interaction).
	 * @param connectionFactory the ConnectionFactory to use to obtain connections
	 */
	public SimpleRecordOperation(ConnectionFactory connectionFactory, InteractionSpec interactionSpec) {
		getCciTemplate().setConnectionFactory(connectionFactory);
		setInteractionSpec(interactionSpec);
	}


	/**
	 * Execute the CCI interaction encapsulated by this operation object.
	 * This method will call CCI's {@code Interaction.execute} variant
	 * that returns an output Record.
	 * @param inputRecord the input record
	 * @return the output record
	 * @throws DataAccessException if there is any problem
	 * @see javax.resource.cci.Interaction#execute(javax.resource.cci.InteractionSpec, Record)
	 */
	@Nullable
	public Record execute(Record inputRecord) throws DataAccessException {
		InteractionSpec interactionSpec = getInteractionSpec();
		Assert.state(interactionSpec != null, "No InteractionSpec set");
		return getCciTemplate().execute(interactionSpec, inputRecord);
	}

	/**
	 * Execute the CCI interaction encapsulated by this operation object.
	 * This method will call CCI's {@code Interaction.execute} variant
	 * with a passed-in output Record.
	 * @param inputRecord the input record
	 * @param outputRecord the output record
	 * @throws DataAccessException if there is any problem
	 * @see javax.resource.cci.Interaction#execute(javax.resource.cci.InteractionSpec, Record, Record)
	 */
	public void execute(Record inputRecord, Record outputRecord) throws DataAccessException {
		InteractionSpec interactionSpec = getInteractionSpec();
		Assert.state(interactionSpec != null, "No InteractionSpec set");
		getCciTemplate().execute(interactionSpec, inputRecord, outputRecord);
	}

}
