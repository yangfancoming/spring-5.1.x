

package org.springframework.web.jsf;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.junit.Test;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.StaticListableBeanFactory;

import static org.junit.Assert.*;

/**
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 */
public class DelegatingPhaseListenerTests {

	private final MockFacesContext facesContext = new MockFacesContext();

	private final StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();

	@SuppressWarnings("serial")
	private final DelegatingPhaseListenerMulticaster delPhaseListener = new DelegatingPhaseListenerMulticaster() {
		@Override
		protected ListableBeanFactory getBeanFactory(FacesContext facesContext) {
			return beanFactory;
		}
	};

	@Test
	public void beforeAndAfterPhaseWithSingleTarget() {
		TestListener target = new TestListener();
		beanFactory.addBean("testListener", target);

		assertEquals(PhaseId.ANY_PHASE, delPhaseListener.getPhaseId());
		PhaseEvent event = new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, new MockLifecycle());

		delPhaseListener.beforePhase(event);
		assertTrue(target.beforeCalled);

		delPhaseListener.afterPhase(event);
		assertTrue(target.afterCalled);
	}

	@Test
	public void beforeAndAfterPhaseWithMultipleTargets() {
		TestListener target1 = new TestListener();
		TestListener target2 = new TestListener();
		beanFactory.addBean("testListener1", target1);
		beanFactory.addBean("testListener2", target2);

		assertEquals(PhaseId.ANY_PHASE, delPhaseListener.getPhaseId());
		PhaseEvent event = new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, new MockLifecycle());

		delPhaseListener.beforePhase(event);
		assertTrue(target1.beforeCalled);
		assertTrue(target2.beforeCalled);

		delPhaseListener.afterPhase(event);
		assertTrue(target1.afterCalled);
		assertTrue(target2.afterCalled);
	}


	@SuppressWarnings("serial")
	public static class TestListener implements PhaseListener {

		boolean beforeCalled = false;
		boolean afterCalled = false;

		@Override
		public PhaseId getPhaseId() {
			return PhaseId.ANY_PHASE;
		}

		@Override
		public void beforePhase(PhaseEvent arg0) {
			beforeCalled = true;
		}

		@Override
		public void afterPhase(PhaseEvent arg0) {
			afterCalled = true;
		}
	}

}
