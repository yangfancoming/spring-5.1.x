

package org.springframework.scripting.groovy;

import org.codehaus.groovy.control.customizers.ImportCustomizer;

/**
 * @author Juergen Hoeller
 */
public class MyImportCustomizer extends ImportCustomizer {

	public MyImportCustomizer() {
		addStarImports("org.springframework.scripting.groovy");
	}

}
