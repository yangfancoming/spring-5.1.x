

package org.springframework.beans.factory.parsing;

import org.apache.commons.logging.Log;
import org.junit.Test;

import org.springframework.core.io.DescriptiveResource;

import static org.mockito.BDDMockito.*;


public class FailFastProblemReporterTests {

	@Test(expected = BeanDefinitionParsingException.class)
	public void testError() throws Exception {
		FailFastProblemReporter reporter = new FailFastProblemReporter();
		reporter.error(new Problem("VGER", new Location(new DescriptiveResource("here")),
				null, new IllegalArgumentException()));
	}

	@Test
	public void testWarn() throws Exception {
		Problem problem = new Problem("VGER", new Location(new DescriptiveResource("here")),
				null, new IllegalArgumentException());

		Log log = mock(Log.class);

		FailFastProblemReporter reporter = new FailFastProblemReporter();
		reporter.setLogger(log);
		reporter.warning(problem);

		verify(log).warn(any(), isA(IllegalArgumentException.class));
	}

}
