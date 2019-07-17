

package org.springframework.scheduling.config;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ScheduledTaskRegistrar}.
 *
 * @author Tobias Montagna-Hay

 * @since 4.2
 */
public class ScheduledTaskRegistrarTests {

	private final ScheduledTaskRegistrar taskRegistrar = new ScheduledTaskRegistrar();


	@Test
	public void emptyTaskLists() {
		assertTrue(this.taskRegistrar.getTriggerTaskList().isEmpty());
		assertTrue(this.taskRegistrar.getCronTaskList().isEmpty());
		assertTrue(this.taskRegistrar.getFixedRateTaskList().isEmpty());
		assertTrue(this.taskRegistrar.getFixedDelayTaskList().isEmpty());
	}

	@Test
	public void getTriggerTasks() {
		TriggerTask mockTriggerTask = mock(TriggerTask.class);
		List<TriggerTask> triggerTaskList = Collections.singletonList(mockTriggerTask);
		this.taskRegistrar.setTriggerTasksList(triggerTaskList);
		List<TriggerTask> retrievedList = this.taskRegistrar.getTriggerTaskList();
		assertEquals(1, retrievedList.size());
		assertEquals(mockTriggerTask, retrievedList.get(0));
	}

	@Test
	public void getCronTasks() {
		CronTask mockCronTask = mock(CronTask.class);
		List<CronTask> cronTaskList = Collections.singletonList(mockCronTask);
		this.taskRegistrar.setCronTasksList(cronTaskList);
		List<CronTask> retrievedList = this.taskRegistrar.getCronTaskList();
		assertEquals(1, retrievedList.size());
		assertEquals(mockCronTask, retrievedList.get(0));
	}

	@Test
	public void getFixedRateTasks() {
		IntervalTask mockFixedRateTask = mock(IntervalTask.class);
		List<IntervalTask> fixedRateTaskList = Collections.singletonList(mockFixedRateTask);
		this.taskRegistrar.setFixedRateTasksList(fixedRateTaskList);
		List<IntervalTask> retrievedList = this.taskRegistrar.getFixedRateTaskList();
		assertEquals(1, retrievedList.size());
		assertEquals(mockFixedRateTask, retrievedList.get(0));
	}

	@Test
	public void getFixedDelayTasks() {
		IntervalTask mockFixedDelayTask = mock(IntervalTask.class);
		List<IntervalTask> fixedDelayTaskList = Collections.singletonList(mockFixedDelayTask);
		this.taskRegistrar.setFixedDelayTasksList(fixedDelayTaskList);
		List<IntervalTask> retrievedList = this.taskRegistrar.getFixedDelayTaskList();
		assertEquals(1, retrievedList.size());
		assertEquals(mockFixedDelayTask, retrievedList.get(0));
	}

}
