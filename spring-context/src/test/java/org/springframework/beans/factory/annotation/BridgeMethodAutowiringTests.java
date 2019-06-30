

package org.springframework.beans.factory.annotation;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import static org.junit.Assert.*;

public class BridgeMethodAutowiringTests {

	@Test
	public void SPR8434() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(UserServiceImpl.class, Foo.class);
		ctx.refresh();
		assertNotNull(ctx.getBean(UserServiceImpl.class).object);
	}


	static abstract class GenericServiceImpl<D> {
		public abstract void setObject(D object);
	}


	public static class UserServiceImpl extends GenericServiceImpl<Foo> {

		protected Foo object;

		@Override
		@Inject
		@Named("userObject")
		public void setObject(Foo object) {
			if (this.object != null) {
				throw new IllegalStateException("Already called");
			}
			this.object = object;
		}
	}


	@Component("userObject")
	public static class Foo {
	}

}
