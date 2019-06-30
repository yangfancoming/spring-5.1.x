

package org.springframework.test.context.testng;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Simple {@link ITestListener} which tracks how many times certain TestNG
 * callback methods were called: only intended for the integration test suite.
 *
 * @author Sam Brannen
 * @since 4.2
 */
public class TrackingTestNGTestListener implements ITestListener {

	public int testStartCount = 0;

	public int testSuccessCount = 0;

	public int testFailureCount = 0;

	public int failedConfigurationsCount = 0;


	@Override
	public void onFinish(ITestContext testContext) {
		this.failedConfigurationsCount += testContext.getFailedConfigurations().size();
	}

	@Override
	public void onStart(ITestContext testContext) {
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult testResult) {
	}

	@Override
	public void onTestFailure(ITestResult testResult) {
		this.testFailureCount++;
	}

	@Override
	public void onTestSkipped(ITestResult testResult) {
	}

	@Override
	public void onTestStart(ITestResult testResult) {
		this.testStartCount++;
	}

	@Override
	public void onTestSuccess(ITestResult testResult) {
		this.testSuccessCount++;
	}

}
