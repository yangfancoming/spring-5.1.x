

package org.springframework.core.constants;

import java.util.Locale;
import java.util.Set;
import org.junit.Test;
import org.springframework.core.Constants;

import static org.junit.Assert.*;


public class ConstantsTests {

	Constants c = new Constants(A.class);

	@Test
	public void constants() {
		assertEquals(A.class.getName(), c.getClassName());
		assertEquals(9, c.getSize());
		assertEquals(A.DOG, c.asNumber("DOG").intValue());
		assertEquals(A.DOG, c.asNumber("dog").intValue());
		assertEquals(A.CAT, c.asNumber("cat").intValue());

		try {
			assertEquals(A.P, c.asNumber("P").intValue());
		} catch (Constants.ConstantException e) {
			e.printStackTrace();
		}

		try {
			c.asNumber("bogus");
		}
		catch (Constants.ConstantException expected) {
			System.out.println(expected.getMessage());
		}

		assertTrue(c.asString("S1").equals(A.S1));
		try {
			c.asNumber("S1");
		}
		catch (Constants.ConstantException expected) {
			System.out.println(expected.getMessage() + "Wrong type");
		}
	}

	@Test
	public void getNames() {
		Set<?> names = c.getNames("");
		names.stream().forEach(a->System.out.println(a));
		assertEquals(c.getSize(), names.size());
		assertTrue(names.contains("DOG"));
		assertTrue(names.contains("CAT"));
		assertTrue(names.contains("S1"));

		// 不区分大小写 
		names = c.getNames("D");
		assertEquals(1, names.size());
		assertTrue(names.contains("DOG"));
		forEachSet(names);

		names = c.getNames("d");
		assertEquals(1, names.size());
		assertTrue(names.contains("DOG"));
		forEachSet(names);
	}
	
	public void forEachSet(Set<?> names){
		System.out.println("--------------------------------------------------->>>>>>>>>>>>");
		names.stream().forEach(a->System.out.println(a));
	}

	@Test
	public void getValues() {
		Set<?> values = c.getValues("");
		forEachSet(values);
		assertEquals(7, values.size());
		assertTrue(values.contains(Integer.valueOf(0)));
		assertTrue(values.contains(Integer.valueOf(66)));
		assertTrue(values.contains(""));


		values = c.getValues("D");
		forEachSet(values);
		assertEquals(1, values.size());
		assertTrue(values.contains(Integer.valueOf(0)));

		values = c.getValues("prefix");
		forEachSet(values);
		assertEquals(2, values.size());
		assertTrue(values.contains(Integer.valueOf(1)));
		assertTrue(values.contains(Integer.valueOf(2)));

		// 详情参考  getValuesForProperty() ---> propertyToConstantNamePrefix()
		values = c.getValuesForProperty("myProperty");
		forEachSet(values);
		assertEquals(2, values.size());
		assertTrue(values.contains(Integer.valueOf(1)));
		assertTrue(values.contains(Integer.valueOf(2)));
	}

	@Test
	public void getValuesInTurkey() {
		Locale oldLocale = Locale.getDefault();
		Locale.setDefault(new Locale("tr", ""));
		try {
			Set<?> values = c.getValues("");
			assertEquals(7, values.size());
			assertTrue(values.contains(Integer.valueOf(0)));
			assertTrue(values.contains(Integer.valueOf(66)));
			assertTrue(values.contains(""));

			values = c.getValues("D");
			assertEquals(1, values.size());
			assertTrue(values.contains(Integer.valueOf(0)));

			values = c.getValues("prefix");
			assertEquals(2, values.size());
			assertTrue(values.contains(Integer.valueOf(1)));
			assertTrue(values.contains(Integer.valueOf(2)));

			values = c.getValuesForProperty("myProperty");
			assertEquals(2, values.size());
			assertTrue(values.contains(Integer.valueOf(1)));
			assertTrue(values.contains(Integer.valueOf(2)));
		}
		finally {
			Locale.setDefault(oldLocale);
		}
	}

	@Test
	public void suffixAccess() { // 通过后缀获取
	
		Set<?> names = c.getNamesForSuffix("_PROPERTY");
		assertEquals(2, names.size());
		assertTrue(names.contains("NO_PROPERTY"));
		assertTrue(names.contains("YES_PROPERTY"));

		Set<?> values = c.getValuesForSuffix("_PROPERTY");
		assertEquals(2, values.size());
		assertTrue(values.contains(Integer.valueOf(3)));
		assertTrue(values.contains(Integer.valueOf(4)));
	}

	@Test
	public void toCode() {
		assertEquals("DOG", c.toCode(Integer.valueOf(0), ""));
		assertEquals("DOG", c.toCode(Integer.valueOf(0), "D"));
		assertEquals("DOG", c.toCode(Integer.valueOf(0), "DO"));
		assertEquals("DOG", c.toCode(Integer.valueOf(0), "DoG"));
		assertEquals("DOG", c.toCode(Integer.valueOf(0), null));
		assertEquals("CAT", c.toCode(Integer.valueOf(66), ""));
		assertEquals("CAT", c.toCode(Integer.valueOf(66), "C"));
		assertEquals("CAT", c.toCode(Integer.valueOf(66), "ca"));
		assertEquals("CAT", c.toCode(Integer.valueOf(66), "cAt"));
		assertEquals("CAT", c.toCode(Integer.valueOf(66), null));
		assertEquals("S1", c.toCode("", ""));
		assertEquals("S1", c.toCode("", "s"));
		assertEquals("S1", c.toCode("", "s1"));
		assertEquals("S1", c.toCode("", null));
		try {
			c.toCode("bogus", "bogus");
			fail("Should have thrown ConstantException");
		}
		catch (Constants.ConstantException expected) {
		}
		try {
			c.toCode("bogus", null);
			fail("Should have thrown ConstantException");
		}
		catch (Constants.ConstantException expected) {
		}

		assertEquals("MY_PROPERTY_NO", c.toCodeForProperty(Integer.valueOf(1), "myProperty"));
		assertEquals("MY_PROPERTY_YES", c.toCodeForProperty(Integer.valueOf(2), "myProperty"));
		try {
			c.toCodeForProperty("bogus", "bogus");
			fail("Should have thrown ConstantException");
		}
		catch (Constants.ConstantException expected) {
		}

		assertEquals("DOG", c.toCodeForSuffix(Integer.valueOf(0), ""));
		assertEquals("DOG", c.toCodeForSuffix(Integer.valueOf(0), "G"));
		assertEquals("DOG", c.toCodeForSuffix(Integer.valueOf(0), "OG"));
		assertEquals("DOG", c.toCodeForSuffix(Integer.valueOf(0), "DoG"));
		assertEquals("DOG", c.toCodeForSuffix(Integer.valueOf(0), null));
		assertEquals("CAT", c.toCodeForSuffix(Integer.valueOf(66), ""));
		assertEquals("CAT", c.toCodeForSuffix(Integer.valueOf(66), "T"));
		assertEquals("CAT", c.toCodeForSuffix(Integer.valueOf(66), "at"));
		assertEquals("CAT", c.toCodeForSuffix(Integer.valueOf(66), "cAt"));
		assertEquals("CAT", c.toCodeForSuffix(Integer.valueOf(66), null));
		assertEquals("S1", c.toCodeForSuffix("", ""));
		assertEquals("S1", c.toCodeForSuffix("", "1"));
		assertEquals("S1", c.toCodeForSuffix("", "s1"));
		assertEquals("S1", c.toCodeForSuffix("", null));
		try {
			c.toCodeForSuffix("bogus", "bogus");
			fail("Should have thrown ConstantException");
		}
		catch (Constants.ConstantException expected) {
		}
		try {
			c.toCodeForSuffix("bogus", null);
			fail("Should have thrown ConstantException");
		}
		catch (Constants.ConstantException expected) {
		}
	}

	@Test
	public void getValuesWithNullPrefix()  {
		Set<?> values = c.getValues(null);
		assertEquals("Must have returned *all* public static final values", 7, values.size());
	}

	@Test
	public void getValuesWithEmptyStringPrefix()  {
		Set<Object> values = c.getValues("");
		assertEquals("Must have returned *all* public static final values", 7, values.size());
	}

	@Test
	public void getValuesWithWhitespacedStringPrefix()  {
		Set<?> values = c.getValues(" ");
		assertEquals("Must have returned *all* public static final values", 7, values.size());
	}

	@Test
	public void withClassThatExposesNoConstants()  {
		Constants c = new Constants(NoConstants.class);
		assertEquals(0, c.getSize());
		final Set<?> values = c.getValues("");
		assertNotNull(values);
		assertEquals(0, values.size());
	}

	@Test
	public void ctorWithNullClass()  {
		try {
			new Constants(null);
			fail("Must have thrown IllegalArgumentException");
		}
		catch (IllegalArgumentException expected) {}
	}




}
