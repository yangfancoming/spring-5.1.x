

package org.springframework.transaction.config;

import java.io.Serializable;

import org.springframework.transaction.annotation.Transactional;


@SuppressWarnings("serial")
public class TransactionalService implements Serializable {

	@Transactional("synch")
	public void setSomething(String name) {
	}

	@Transactional("noSynch")
	public void doSomething() {
	}

}
