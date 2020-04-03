/**
 * Remoting classes for transparent Java-to-Java remoting via a JMS provider.
 *
 * xmlBeanDefinitionReaderAllows the target service to be load-balanced across a number of queue
 * receivers, and provides a level of indirection between the client and the
 * service: They only need to agree on a queue name and a service interface.
 */
@NonNullApi
@NonNullFields
package org.springframework.jms.remoting;

import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;
