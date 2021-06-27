package org.springframework.cglib.item08;

import org.junit.Test;
import org.springframework.cglib.core.DebuggingClassWriter;

/**
 * Created by Administrator on 2020/4/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/16---11:01
 */
public class App {

	@Test
	public void test(){
		System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, ".//");
	}
}
