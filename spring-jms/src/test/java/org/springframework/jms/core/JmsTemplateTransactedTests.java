

package org.springframework.jms.core;

import javax.jms.Session;

import static org.mockito.BDDMockito.*;

/**

 * @author Stephane Nicoll
 * @since 06.01.2005
 */
public class JmsTemplateTransactedTests extends JmsTemplateTests {

	private Session localSession;


	@Override
	public void setupMocks() throws Exception {
		super.setupMocks();
		this.localSession = mock(Session.class);
		given(this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).willReturn(this.localSession);
	}

	@Override
	protected Session getLocalSession() {
		return this.localSession;
	}

	@Override
	protected boolean useTransactedSession() {
		return true;
	}

	@Override
	protected boolean useTransactedTemplate() {
		return true;
	}

}
