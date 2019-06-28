

package org.springframework.jmx.export.annotation;

import org.springframework.jmx.support.MetricType;

/**
 * @author Stephane Nicoll
 */
@ManagedResource(objectName = "bean:name=interfaceTestBean", description = "My Managed Bean")
public interface AnotherAnnotationTestBean {

	@ManagedOperation(description = "invoke foo")
	void foo();

	@ManagedAttribute(description = "Bar description")
	String getBar();

	void setBar(String bar);

	@ManagedMetric(description = "a metric", metricType = MetricType.COUNTER)
	int getCacheEntries();

}
