

package org.springframework.context.annotation;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author Juergen Hoeller
 * @author Oliver Gierke
 */
public class Spr16179Tests {

	@Test
	public void repro() {
		AnnotationConfigApplicationContext bf =
				new AnnotationConfigApplicationContext(AssemblerConfig.class, AssemblerInjection.class);

		assertSame(bf.getBean("someAssembler"), bf.getBean(AssemblerInjection.class).assembler0);
		// assertNull(bf.getBean(AssemblerInjection.class).assembler1);  TODO: accidental match
		// assertNull(bf.getBean(AssemblerInjection.class).assembler2);
		assertSame(bf.getBean("pageAssembler"), bf.getBean(AssemblerInjection.class).assembler3);
		assertSame(bf.getBean("pageAssembler"), bf.getBean(AssemblerInjection.class).assembler4);
		assertSame(bf.getBean("pageAssembler"), bf.getBean(AssemblerInjection.class).assembler5);
		assertSame(bf.getBean("pageAssembler"), bf.getBean(AssemblerInjection.class).assembler6);
	}


	@Configuration
	static class AssemblerConfig {

		@Bean
		PageAssemblerImpl<?> pageAssembler() {
			return new PageAssemblerImpl<>();
		}

		@Bean
		Assembler<SomeType> someAssembler() {
			return new Assembler<SomeType>() {};
		}
	}


	public static class AssemblerInjection {

		@Autowired(required = false)
		Assembler<SomeType> assembler0;

		@Autowired(required = false)
		Assembler<SomeOtherType> assembler1;

		@Autowired(required = false)
		Assembler<Page<String>> assembler2;

		@Autowired(required = false)
		Assembler<Page> assembler3;

		@Autowired(required = false)
		Assembler<Page<?>> assembler4;

		@Autowired(required = false)
		PageAssembler<?> assembler5;

		@Autowired(required = false)
		PageAssembler<String> assembler6;
	}


	interface Assembler<T> {}

	interface PageAssembler<T> extends Assembler<Page<T>> {}

	static class PageAssemblerImpl<T> implements PageAssembler<T> {}

	interface Page<T> {}

	interface SomeType {}

	interface SomeOtherType {}

}
