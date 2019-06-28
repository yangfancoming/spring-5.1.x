

package org.springframework.scripting.support;

import org.junit.Test;

import org.springframework.beans.factory.BeanFactory;

import static org.mockito.Mockito.*;

/**
 * @author Rick Evans
 */
public class RefreshableScriptTargetSourceTests {

	@Test(expected = IllegalArgumentException.class)
	public void createWithNullScriptSource() throws Exception {
		new RefreshableScriptTargetSource(mock(BeanFactory.class), "a.bean", null, null, false);
	}

}
