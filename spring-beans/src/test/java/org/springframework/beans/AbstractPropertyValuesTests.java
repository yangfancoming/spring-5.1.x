

package org.springframework.beans;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Rod Johnson
 * @author Chris Beams
 */
public abstract class AbstractPropertyValuesTests {

	/**
	 * Must contain: forname=Tony surname=Blair age=50
	 */
	protected void doTestTony(PropertyValues pvs) {
		assertTrue("Contains 3", pvs.getPropertyValues().length == 3);
		assertTrue("Contains forname", pvs.contains("forname"));
		assertTrue("Contains surname", pvs.contains("surname"));
		assertTrue("Contains age", pvs.contains("age"));
		assertTrue("Doesn't contain tory", !pvs.contains("tory"));

		PropertyValue[] ps = pvs.getPropertyValues();
		Map<String, String> m = new HashMap<>();
		m.put("forname", "Tony");
		m.put("surname", "Blair");
		m.put("age", "50");
		for (int i = 0; i < ps.length; i++) {
			Object val = m.get(ps[i].getName());
			assertTrue("Can't have unexpected value", val != null);
			assertTrue("Val i string", val instanceof String);
			assertTrue("val matches expected", val.equals(ps[i].getValue()));
			m.remove(ps[i].getName());
		}
		assertTrue("Map size is 0", m.size() == 0);
	}

}
