

package org.springframework.test.context.configuration.interfaces;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 4.3
 */
@RunWith(SpringRunner.class)
public class WebAppConfigurationInterfaceTests implements WebAppConfigurationTestInterface {

	@Autowired
	WebApplicationContext wac;


	@Test
	public void wacLoaded() {
		assertNotNull(wac);
	}

}
