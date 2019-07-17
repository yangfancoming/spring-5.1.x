

package org.springframework.scripting;

import org.springframework.context.ApplicationContext;
import org.springframework.tests.sample.beans.TestBean;

/**

 * @since 08.08.2006
 */
public interface ContextScriptBean extends ScriptBean {

	TestBean getTestBean();

	ApplicationContext getApplicationContext();

}
