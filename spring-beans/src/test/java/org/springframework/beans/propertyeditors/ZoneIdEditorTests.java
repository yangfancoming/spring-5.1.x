

package org.springframework.beans.propertyeditors;

import java.time.ZoneId;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Nicholas Williams
 */
public class ZoneIdEditorTests {

	private final ZoneIdEditor editor = new ZoneIdEditor();

	@Test
	public void americaChicago() {
		editor.setAsText("America/Chicago");

		ZoneId zoneId = (ZoneId) editor.getValue();
		assertNotNull("The zone ID should not be null.", zoneId);
		assertEquals("The zone ID is not correct.", ZoneId.of("America/Chicago"), zoneId);

		assertEquals("The text version is not correct.", "America/Chicago", editor.getAsText());
	}

	@Test
	public void americaLosAngeles() {
		editor.setAsText("America/Los_Angeles");

		ZoneId zoneId = (ZoneId) editor.getValue();
		assertNotNull("The zone ID should not be null.", zoneId);
		assertEquals("The zone ID is not correct.", ZoneId.of("America/Los_Angeles"), zoneId);

		assertEquals("The text version is not correct.", "America/Los_Angeles", editor.getAsText());
	}

	@Test
	public void getNullAsText() {
		assertEquals("The returned value is not correct.", "", editor.getAsText());
	}

	@Test
	public void getValueAsText() {
		editor.setValue(ZoneId.of("America/New_York"));
		assertEquals("The text version is not correct.", "America/New_York", editor.getAsText());
	}

}
