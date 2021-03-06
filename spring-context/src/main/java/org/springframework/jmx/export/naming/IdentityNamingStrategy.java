

package org.springframework.jmx.export.naming;

import java.util.Hashtable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * An implementation of the {@code ObjectNamingStrategy} interface that
 * creates a name based on the identity of a given instance.
 *
 * The resulting {@code ObjectName} will be in the form
 * <i>package</i>:class=<i>class name</i>,hashCode=<i>identity hash (in hex)</i>
 *
 * @author Rob Harrop

 * @since 1.2
 */
public class IdentityNamingStrategy implements ObjectNamingStrategy {

	/**
	 * The type key.
	 */
	public static final String TYPE_KEY = "type";

	/**
	 * The hash code key.
	 */
	public static final String HASH_CODE_KEY = "hashCode";


	/**
	 * Returns an instance of {@code ObjectName} based on the identity
	 * of the managed resource.
	 */
	@Override
	public ObjectName getObjectName(Object managedBean, @Nullable String beanKey) throws MalformedObjectNameException {
		String domain = ClassUtils.getPackageName(managedBean.getClass());
		Hashtable<String, String> keys = new Hashtable<>();
		keys.put(TYPE_KEY, ClassUtils.getShortName(managedBean.getClass()));
		keys.put(HASH_CODE_KEY, ObjectUtils.getIdentityHexString(managedBean));
		return ObjectNameManager.getInstance(domain, keys);
	}

}
