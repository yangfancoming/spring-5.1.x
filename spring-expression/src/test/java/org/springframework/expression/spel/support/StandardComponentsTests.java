

package org.springframework.expression.spel.support;

import java.util.List;

import org.junit.Test;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.OperatorOverloader;
import org.springframework.expression.TypeComparator;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypeLocator;

import static org.junit.Assert.*;

public class StandardComponentsTests {

	@Test
	public void testStandardEvaluationContext() {
		StandardEvaluationContext context = new StandardEvaluationContext();
		assertNotNull(context.getTypeComparator());

		TypeComparator tc = new StandardTypeComparator();
		context.setTypeComparator(tc);
		assertEquals(tc, context.getTypeComparator());

		TypeLocator tl = new StandardTypeLocator();
		context.setTypeLocator(tl);
		assertEquals(tl, context.getTypeLocator());
	}

	@Test(expected = EvaluationException.class)
	public void testStandardOperatorOverloader() throws EvaluationException {
		OperatorOverloader oo = new StandardOperatorOverloader();
		assertFalse(oo.overridesOperation(Operation.ADD, null, null));
		oo.operate(Operation.ADD, 2, 3);
	}

	@Test
	public void testStandardTypeLocator() {
		StandardTypeLocator tl = new StandardTypeLocator();
		List<String> prefixes = tl.getImportPrefixes();
		assertEquals(1, prefixes.size());
		tl.registerImport("java.util");
		prefixes = tl.getImportPrefixes();
		assertEquals(2, prefixes.size());
		tl.removeImport("java.util");
		prefixes = tl.getImportPrefixes();
		assertEquals(1, prefixes.size());
	}

	@Test
	public void testStandardTypeConverter() throws EvaluationException {
		TypeConverter tc = new StandardTypeConverter();
		tc.convertValue(3, TypeDescriptor.forObject(3), TypeDescriptor.valueOf(Double.class));
	}

}
