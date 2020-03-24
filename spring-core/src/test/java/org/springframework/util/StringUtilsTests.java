
package org.springframework.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

import static org.junit.Assert.*;

public class StringUtilsTests {

	@Test
	public void testTrimAllWhitespace() {
		assertEquals("", StringUtils.trimAllWhitespace(""));
		assertEquals("", StringUtils.trimAllWhitespace(" "));
		assertEquals("", StringUtils.trimAllWhitespace("\t"));
		assertEquals("", StringUtils.trimAllWhitespace("\n"));

		assertEquals("a", StringUtils.trimAllWhitespace(" a"));
		assertEquals("a", StringUtils.trimAllWhitespace("a "));
		assertEquals("a", StringUtils.trimAllWhitespace(" a "));

		assertEquals("ab", StringUtils.trimAllWhitespace(" a b "));
		assertEquals("abc", StringUtils.trimAllWhitespace(" a b  c "));
		// jdk自带的trim()只能干掉首尾空格，不能干掉字符间空白符
		System.out.println("a b \t c ");
		System.out.println("a b \t c ".trim());
		System.out.println(StringUtils.trimAllWhitespace("a b \t c "));
	}

	// 测试 只干掉 头部 空白符
	@Test
	public void testTrimLeadingWhitespace() {
		assertEquals(null, StringUtils.trimLeadingWhitespace(null));
		assertEquals("", StringUtils.trimLeadingWhitespace(""));
		assertEquals("", StringUtils.trimLeadingWhitespace(" "));
		assertEquals("", StringUtils.trimLeadingWhitespace("\t"));

		assertEquals("a", StringUtils.trimLeadingWhitespace(" a"));
		assertEquals("a ", StringUtils.trimLeadingWhitespace("a "));
		assertEquals("a ", StringUtils.trimLeadingWhitespace(" a "));
		assertEquals("a b ", StringUtils.trimLeadingWhitespace(" a b "));
		assertEquals("a b  c ", StringUtils.trimLeadingWhitespace(" a b  c "));

		assertEquals("a b  c ", StringUtils.trimLeadingWhitespace(" \t a b  c "));
		assertEquals("a b  c ", StringUtils.trimLeadingWhitespace(" \n a b  c "));
	}

	// 测试 只干掉 尾部 空白符
	@Test
	public void testTrimTrailingWhitespace() {
		assertEquals(null, StringUtils.trimTrailingWhitespace(null));
		assertEquals("", StringUtils.trimTrailingWhitespace(""));
		assertEquals("", StringUtils.trimTrailingWhitespace(" "));
		assertEquals("", StringUtils.trimTrailingWhitespace("\t"));

		assertEquals("a", StringUtils.trimTrailingWhitespace("a "));
		assertEquals(" a", StringUtils.trimTrailingWhitespace(" a"));
		assertEquals(" a", StringUtils.trimTrailingWhitespace(" a "));

		assertEquals(" a b", StringUtils.trimTrailingWhitespace(" a b "));
		assertEquals(" a b  c", StringUtils.trimTrailingWhitespace(" a b  c "));
	}

	// 测试 只干掉 头部 的给定字符
	@Test
	public void testTrimLeadingCharacter() {
		assertEquals(null, StringUtils.trimLeadingCharacter(null, ' '));
		assertEquals("", StringUtils.trimLeadingCharacter("", ' '));
		assertEquals("", StringUtils.trimLeadingCharacter(" ", ' '));

		assertEquals("\t", StringUtils.trimLeadingCharacter("\t", ' '));

		assertEquals("a", StringUtils.trimLeadingCharacter(" a", ' '));
		assertEquals("a ", StringUtils.trimLeadingCharacter("a ", ' '));
		assertEquals("a ", StringUtils.trimLeadingCharacter(" a ", ' '));

		assertEquals("a b ", StringUtils.trimLeadingCharacter(" a b ", ' '));
		assertEquals("a b  c ", StringUtils.trimLeadingCharacter(" a b  c ", ' '));
		assertEquals(" a b  c ", StringUtils.trimLeadingCharacter("11 a b  c ", '1'));
	}

	// 测试 只干掉 尾部 的给定字符
	@Test
	public void testTrimTrailingCharacter() {
		assertEquals(null, StringUtils.trimTrailingCharacter(null, ' '));
		assertEquals("", StringUtils.trimTrailingCharacter("", ' '));
		assertEquals("", StringUtils.trimTrailingCharacter(" ", ' '));
		assertEquals("\t", StringUtils.trimTrailingCharacter("\t", ' '));
		assertEquals("a", StringUtils.trimTrailingCharacter("a ", ' '));
		assertEquals(" a", StringUtils.trimTrailingCharacter(" a", ' '));
		assertEquals(" a", StringUtils.trimTrailingCharacter(" a ", ' '));
		assertEquals(" a b", StringUtils.trimTrailingCharacter(" a b ", ' '));
		assertEquals(" a b  c", StringUtils.trimTrailingCharacter(" a b  c ", ' '));
	}

	// 测试 判断给定字符串的前缀是否与给定的前缀匹配
	@Test
	public void testStartsWithIgnoreCase() {
		String prefix = "fOo";
		assertTrue(StringUtils.startsWithIgnoreCase("foo", prefix));
		assertTrue(StringUtils.startsWithIgnoreCase("Foo", prefix));
		assertTrue(StringUtils.startsWithIgnoreCase("foobar", prefix));
		assertTrue(StringUtils.startsWithIgnoreCase("foobarbar", prefix));
		assertTrue(StringUtils.startsWithIgnoreCase("Foobar", prefix));
		assertTrue(StringUtils.startsWithIgnoreCase("FoobarBar", prefix));
		assertTrue(StringUtils.startsWithIgnoreCase("foObar", prefix));
		assertTrue(StringUtils.startsWithIgnoreCase("FOObar", prefix));
		assertTrue(StringUtils.startsWithIgnoreCase("fOobar", prefix));

		assertFalse(StringUtils.startsWithIgnoreCase(null, prefix));
		assertFalse(StringUtils.startsWithIgnoreCase("fOobar", null));
		assertFalse(StringUtils.startsWithIgnoreCase("b", prefix));
		assertFalse(StringUtils.startsWithIgnoreCase("barfoo", prefix));
		assertFalse(StringUtils.startsWithIgnoreCase("barfoobar", prefix));
	}

	// 测试 判断给定字符串的后缀是否与给定的后缀匹配
	@Test
	public void testEndsWithIgnoreCase() {
		String suffix = "fOo";
		assertTrue(StringUtils.endsWithIgnoreCase("foo", suffix));
		assertTrue(StringUtils.endsWithIgnoreCase("Foo", suffix));
		assertTrue(StringUtils.endsWithIgnoreCase("barfoo", suffix));
		assertTrue(StringUtils.endsWithIgnoreCase("barbarfoo", suffix));
		assertTrue(StringUtils.endsWithIgnoreCase("barFoo", suffix));
		assertTrue(StringUtils.endsWithIgnoreCase("barBarFoo", suffix));
		assertTrue(StringUtils.endsWithIgnoreCase("barfoO", suffix));
		assertTrue(StringUtils.endsWithIgnoreCase("barFOO", suffix));
		assertTrue(StringUtils.endsWithIgnoreCase("barfOo", suffix));
		assertFalse(StringUtils.endsWithIgnoreCase(null, suffix));
		assertFalse(StringUtils.endsWithIgnoreCase("barfOo", null));
		assertFalse(StringUtils.endsWithIgnoreCase("b", suffix));
		assertFalse(StringUtils.endsWithIgnoreCase("foobar", suffix));
		assertFalse(StringUtils.endsWithIgnoreCase("barfoobar", suffix));
	}

	@Test
	public void testSubstringMatch() {
		assertTrue(StringUtils.substringMatch("aabbccdd", 1, "abb"));

		assertTrue(StringUtils.substringMatch("foo", 0, "foo"));
		assertTrue(StringUtils.substringMatch("foo", 1, "oo"));
		assertTrue(StringUtils.substringMatch("foo", 2, "o"));

		assertFalse(StringUtils.substringMatch("foo", 0, "fOo"));
		assertFalse(StringUtils.substringMatch("foo", 1, "fOo"));
		assertFalse(StringUtils.substringMatch("foo", 2, "fOo"));
		assertFalse(StringUtils.substringMatch("foo", 3, "fOo"));
		assertFalse(StringUtils.substringMatch("foo", 1, "Oo"));
		assertFalse(StringUtils.substringMatch("foo", 2, "Oo"));
		assertFalse(StringUtils.substringMatch("foo", 3, "Oo"));
		assertFalse(StringUtils.substringMatch("foo", 2, "O"));
		assertFalse(StringUtils.substringMatch("foo", 3, "O"));
	}

	// 测试 计算给定子串(sub)在指定字符串(str)中出现的次数
	@Test
	public void testCountOccurrencesOf() {
		// int countOccurrencesOf(String str, String sub):判断子字符串在字符串中出现的次数
		assertEquals(4, StringUtils.countOccurrencesOf("ababaabab", "ab"));

		assertTrue("nullx2 = 0",StringUtils.countOccurrencesOf(null, null) == 0);
		assertTrue("null string = 0",StringUtils.countOccurrencesOf("s", null) == 0);
		assertTrue("null substring = 0",StringUtils.countOccurrencesOf(null, "s") == 0);
		String s = "erowoiueoiur";
		assertTrue("not found = 0",StringUtils.countOccurrencesOf(s, "WERWER") == 0);
		assertTrue("not found char = 0",StringUtils.countOccurrencesOf(s, "x") == 0);
		assertTrue("not found ws = 0",StringUtils.countOccurrencesOf(s, " ") == 0);
		assertTrue("not found empty string = 0",StringUtils.countOccurrencesOf(s, "") == 0);
		assertTrue("found char=2", StringUtils.countOccurrencesOf(s, "e") == 2);
		assertTrue("found substring=2",StringUtils.countOccurrencesOf(s, "oi") == 2);
		assertTrue("found substring=2",StringUtils.countOccurrencesOf(s, "oiu") == 2);
		assertTrue("found substring=3",StringUtils.countOccurrencesOf(s, "oiur") == 1);
		assertTrue("test last", StringUtils.countOccurrencesOf(s, "r") == 2);
	}

	@Test
	public void testReplace() {
		String inString = "a6AazAaa77abaa";
		String oldPattern = "aa";
		String newPattern = "foo";
		// Simple replace
		String s = StringUtils.replace(inString, oldPattern, newPattern);
		assertEquals("a6AazAfoo77abfoo", s);

		// Non match: no change
		s = StringUtils.replace(inString, "qwoeiruqopwieurpoqwieur", newPattern);
		assertSame("Replace non-matched is returned as-is", inString, s);

		// Null new pattern: should ignore
		s = StringUtils.replace(inString, oldPattern, null);
		assertSame("Replace non-matched is returned as-is", inString, s);

		// Null old pattern: should ignore
		s = StringUtils.replace(inString, null, newPattern);
		assertSame("Replace non-matched is returned as-is", inString, s);
	}

	@Test
	public void testDelete() {
		String inString = "The quick brown fox jumped over the lazy dog";

		String noThe = StringUtils.delete(inString, "the");
		assertEquals("The quick brown fox jumped over  lazy dog", noThe);
		String nohe = StringUtils.delete(inString, "he");
		assertEquals("T quick brown fox jumped over t lazy dog", nohe);
		String nosp = StringUtils.delete(inString, " ");
		assertEquals("Thequickbrownfoxjumpedoverthelazydog", nosp);
		String killEnd = StringUtils.delete(inString, "dog");
		assertEquals("The quick brown fox jumped over the lazy ", killEnd);
		// 测试 不匹配情况下  不做任何操作 原值返回
		String mismatch = StringUtils.delete(inString, "dxxcxcxog");
		assertEquals(inString, mismatch);
		String nochange = StringUtils.delete(inString, "");
		assertEquals(inString, nochange);
		// String deleteAny(String inString, String charsToDelete):删除子字符串中任意出现的字符
		assertEquals("", StringUtils.deleteAny("ababaabab", "bar"));
	}


	@Test
	public void unqualifyTest() {
		// 得到以.分割的最后一个值，可以非常方便的获取类似类名或者文件后缀
		assertEquals("java", StringUtils.unqualify("cn.goat.java"));
		assertEquals("java", StringUtils.unqualify("cn/goat/Hello.java"));
		// 得到以给定字符分割的最后一个值，可以非常方便的获取类似文件名
		assertEquals("Hello.java", StringUtils.unqualify("cn/goat/Hello.java", '/'));

		assertEquals("'hello'", StringUtils.quote("hello"));
	}

	@Test
	public void applyRelativePathTest() {
		// String applyRelativePath(String path, String relativePath):
		// 找到给定的文件，和另一个相对路径的文件，返回第二个文件的全路径
		assertEquals("d:/java/wolfcode/other/Some.java", StringUtils.applyRelativePath("d:/java/wolfcode/Test.java", "other/Some.java"));
		// 但是不支持重新定位绝对路径和上级目录等复杂一些的相对路径写法：
		// 仍然打印：d:/java/wolfcode/../other/Some.java
		assertEquals("d:/java/wolfcode/../other/Some.java", StringUtils.applyRelativePath("d:/java/wolfcode/Test.java", "../other/Some.java"));
	}

	@Test
	public void CleanPathtest() {
		// 清理文件路径,这个方法配合applyRelativePath就可以计算一些简单的相对路径了
		assertEquals("d:/java/other/Some.java", StringUtils.cleanPath("d:/java/wolfcode/../other/Some.java"));
		// 需求：获取d:/java/wolfcode/Test.java相对路径为../../other/Some.java的文件全路径：
		String s = StringUtils.applyRelativePath("d:/java/wolfcode/Test.java", "../../other/Some.java");
		assertEquals("d:/other/Some.java", StringUtils.cleanPath(s));
		// 判断两个文件路径是否相同，会先执行cleanPath之后再比较
		assertTrue(StringUtils.pathEquals("d:/wolfcode.txt","d:/somefile/../wolfcode.txt"));
	}

	@Test
	public void testCleanPath() {
		assertEquals("mypath/myfile", StringUtils.cleanPath("mypath/myfile"));
		assertEquals("mypath/myfile", StringUtils.cleanPath("mypath\\myfile"));
		assertEquals("mypath/myfile", StringUtils.cleanPath("mypath/../mypath/myfile"));
		assertEquals("mypath/myfile", StringUtils.cleanPath("mypath/myfile/../../mypath/myfile"));

		assertEquals("../mypath/myfile", StringUtils.cleanPath("../mypath/myfile"));
		assertEquals("../mypath/myfile", StringUtils.cleanPath("../mypath/../mypath/myfile"));
		assertEquals("../mypath/myfile", StringUtils.cleanPath("mypath/../../mypath/myfile"));
		assertEquals("/../mypath/myfile", StringUtils.cleanPath("/../mypath/myfile"));
		assertEquals("/mypath/myfile", StringUtils.cleanPath("/a/:b/../../mypath/myfile"));
		assertEquals("/", StringUtils.cleanPath("/"));
		assertEquals("/", StringUtils.cleanPath("/mypath/../"));
		assertEquals("", StringUtils.cleanPath("mypath/.."));
		assertEquals("", StringUtils.cleanPath("mypath/../."));
		assertEquals("./", StringUtils.cleanPath("mypath/../"));
		assertEquals("./", StringUtils.cleanPath("././"));
		assertEquals("./", StringUtils.cleanPath("./"));
		assertEquals("../", StringUtils.cleanPath("../"));
		assertEquals("../", StringUtils.cleanPath("./../"));
		assertEquals("../", StringUtils.cleanPath(".././"));
		assertEquals("file:/", StringUtils.cleanPath("file:/"));
		assertEquals("file:/", StringUtils.cleanPath("file:/mypath/../"));
		assertEquals("file:", StringUtils.cleanPath("file:mypath/.."));
		assertEquals("file:", StringUtils.cleanPath("file:mypath/../."));
		assertEquals("file:./", StringUtils.cleanPath("file:mypath/../"));
		assertEquals("file:./", StringUtils.cleanPath("file:././"));
		assertEquals("file:./", StringUtils.cleanPath("file:./"));
		assertEquals("file:../", StringUtils.cleanPath("file:../"));
		assertEquals("file:../", StringUtils.cleanPath("file:./../"));
		assertEquals("file:../", StringUtils.cleanPath("file:.././"));
		assertEquals("file:///c:/path/the%20file.txt", StringUtils.cleanPath("file:///c:/some/../path/the%20file.txt"));
	}

	@Test
	public void testPathEquals() {
		assertTrue("Must be true for the same strings",StringUtils.pathEquals("/dummy1/dummy2/dummy3", "/dummy1/dummy2/dummy3"));
		assertTrue("Must be true for the same win strings",StringUtils.pathEquals("C:\\dummy1\\dummy2\\dummy3", "C:\\dummy1\\dummy2\\dummy3"));
		assertTrue("Must be true for one top path on 1",StringUtils.pathEquals("/dummy1/bin/../dummy2/dummy3", "/dummy1/dummy2/dummy3"));
		assertTrue("Must be true for one win top path on 2",StringUtils.pathEquals("C:\\dummy1\\dummy2\\dummy3", "C:\\dummy1\\bin\\..\\dummy2\\dummy3"));
		assertTrue("Must be true for two top paths on 1",StringUtils.pathEquals("/dummy1/bin/../dummy2/bin/../dummy3", "/dummy1/dummy2/dummy3"));
		assertTrue("Must be true for two win top paths on 2",StringUtils.pathEquals("C:\\dummy1\\dummy2\\dummy3", "C:\\dummy1\\bin\\..\\dummy2\\bin\\..\\dummy3"));
		assertTrue("Must be true for double top paths on 1",StringUtils.pathEquals("/dummy1/bin/tmp/../../dummy2/dummy3", "/dummy1/dummy2/dummy3"));
		assertTrue("Must be true for double top paths on 2 with similarity",StringUtils.pathEquals("/dummy1/dummy2/dummy3", "/dummy1/dum/dum/../../dummy2/dummy3"));
		assertTrue("Must be true for current paths",StringUtils.pathEquals("./dummy1/dummy2/dummy3", "dummy1/dum/./dum/../../dummy2/dummy3"));
		assertFalse("Must be false for relative/absolute paths",StringUtils.pathEquals("./dummy1/dummy2/dummy3", "/dummy1/dum/./dum/../../dummy2/dummy3"));
		assertFalse("Must be false for different strings",StringUtils.pathEquals("/dummy1/dummy2/dummy3", "/dummy1/dummy4/dummy3"));
		assertFalse("Must be false for one false path on 1",StringUtils.pathEquals("/dummy1/bin/tmp/../dummy2/dummy3", "/dummy1/dummy2/dummy3"));
		assertFalse("Must be false for one false win top path on 2",StringUtils.pathEquals("C:\\dummy1\\dummy2\\dummy3", "C:\\dummy1\\bin\\tmp\\..\\dummy2\\dummy3"));
		assertFalse("Must be false for top path on 1 + difference",StringUtils.pathEquals("/dummy1/bin/../dummy2/dummy3", "/dummy1/dummy2/dummy4"));
	}

	@Test
	public void testCommaDelimitedListToSet() {
		String temp = "111\n222\n333\n111";
		// 使用【逗号】分割字符串，并放到set中去重
		System.out.println(StringUtils.commaDelimitedListToSet(temp));
		// 使用 【指定分隔符】 分割字符串，并放到set中去重
		System.out.println(StringUtils.commaDelimitedListToSet(temp,"\n"));
	}

	@Test
	public void testSortStringArray() {
		String[] input = new String[] {"myString2"};
		input = StringUtils.addStringToArray(input, "myString1");
		assertEquals("myString2", input[0]);
		assertEquals("myString1", input[1]);

		StringUtils.sortStringArray(input);
		assertEquals("myString1", input[0]);
		assertEquals("myString2", input[1]);
	}

	@Test
	public void concatenateStringArraysTest() {
		String[] input1 = new String[] {"myString1","myString2","myString3"};
		String[] input2 = new String[] {"myString2","myString2","myString4"};
		String[] strings = StringUtils.concatenateStringArrays(input1, input2);
		assertEquals(6, strings.length);
	}

	@Test
	public void mergeStringArraysTest() {
		String[] input1 = new String[] {"myString1","myString2","myString3"};
		String[] input2 = new String[] {"myString2","myString2","myString4"};
		String[] strings = StringUtils.mergeStringArrays(input1, input2);
		assertEquals(4, strings.length);
	}
	@Test
	public void removeDuplicateStringsTest() {
		String[] input = new String[] {"myString2","myString2","myString4"};
		String[] strings = StringUtils.removeDuplicateStrings(input);
		assertEquals(2, strings.length);
	}


	@Test
	public void testCommaDelimitedListToStringArrayWithNullProducesEmptyArray() {
		String[] sa = StringUtils.commaDelimitedListToStringArray(null);
		assertTrue("String array isn't null with null input", sa != null);
		assertTrue("String array length == 0 with null input", sa.length == 0);
	}

	@Test
	public void testCommaDelimitedListToStringArrayWithEmptyStringProducesEmptyArray() {
		String[] sa = StringUtils.commaDelimitedListToStringArray("");
		assertTrue("String array isn't null with null input", sa != null);
		assertTrue("String array length == 0 with null input", sa.length == 0);
	}

	@Test
	public void testDelimitedListToStringArrayWithComma() {
		String[] sa = StringUtils.delimitedListToStringArray("a,b", ",");
		assertEquals(2, sa.length);
		assertEquals("a", sa[0]);
		assertEquals("b", sa[1]);
	}

	@Test
	public void testDelimitedListToStringArrayWithSemicolon() {
		String[] sa = StringUtils.delimitedListToStringArray("a;b", ";");
		assertEquals(2, sa.length);
		assertEquals("a", sa[0]);
		assertEquals("b", sa[1]);
	}

	@Test
	public void testDelimitedListToStringArrayWithEmptyString() {
		String[] sa = StringUtils.delimitedListToStringArray("a,b", "");
		assertEquals(3, sa.length);
		assertEquals("a", sa[0]);
		assertEquals(",", sa[1]);
		assertEquals("b", sa[2]);
	}

	@Test
	public void testDelimitedListToStringArrayWithNullDelimiter() {
		String[] sa = StringUtils.delimitedListToStringArray("a,b", null);
		assertEquals(1, sa.length);
		assertEquals("a,b", sa[0]);
	}

	@Test
	public void testCommaDelimitedListToStringArrayMatchWords() {
		// Could read these from files
		String[] sa = new String[] {"foo", "bar", "big"};
		doTestCommaDelimitedListToStringArrayLegalMatch(sa);
		doTestStringArrayReverseTransformationMatches(sa);

		sa = new String[] {"a", "b", "c"};
		doTestCommaDelimitedListToStringArrayLegalMatch(sa);
		doTestStringArrayReverseTransformationMatches(sa);

		// Test same words
		sa = new String[] {"AA", "AA", "AA", "AA", "AA"};
		doTestCommaDelimitedListToStringArrayLegalMatch(sa);
		doTestStringArrayReverseTransformationMatches(sa);
	}

	private void doTestStringArrayReverseTransformationMatches(String[] sa) {
		String[] reverse = StringUtils.commaDelimitedListToStringArray(StringUtils.arrayToCommaDelimitedString(sa));
		assertEquals("Reverse transformation is equal",Arrays.asList(sa),Arrays.asList(reverse));
	}

	@Test
	public void testCommaDelimitedListToStringArraySingleString() {
		// Could read these from files
		String s = "woeirqupoiewuropqiewuorpqiwueopriquwopeiurqopwieur";
		String[] sa = StringUtils.commaDelimitedListToStringArray(s);
		assertTrue("Found one String with no delimiters", sa.length == 1);
		assertTrue("Single array entry matches input String with no delimiters",sa[0].equals(s));
	}

	@Test
	public void splitTest() {
		//String[] split(String toSplit, String delimiter):按照指定字符串分割字符串；
		assertArrayEquals(new String[]{"wolfcode","cn"}, StringUtils.split("wolfcode.cn", "."));
		//只分割第一次，打印：[www, wolfcode.cn]
		System.out.println(Arrays.toString(StringUtils.split("www.wolfcode.cn", ".")));
	}


	@Test
	public void tokenizeToStringArrayTest() {
		//String[] tokenizeToStringArray(String str, String delimiters)
		//会对每一个元素执行trim操作，并去掉空字符串
		//使用的是StringTokenizer完成，
		//打印[b, c, d]
		//String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens)
		//后面两个参数在限定是否对每一个元素执行trim操作，是否去掉空字符串
		assertEquals("[b, c, d]",Arrays.toString(StringUtils.tokenizeToStringArray("aa,ba,ca,da", "a,")));
	}

	@Test
	public void testCommaDelimitedListToStringArrayWithOtherPunctuation() {
		// Could read these from files
		String[] sa = new String[] {"xcvwert4456346&*.", "///", ".!", ".", ";"};
		doTestCommaDelimitedListToStringArrayLegalMatch(sa);
	}

	@Test
	public void delimitedListToStringArrayTest() {
		//String[] delimitedListToStringArray(String str, String delimiter):分割字符串，会把delimiter作为整体分隔符
		assertEquals("[a, b, c, da]",Arrays.toString(StringUtils.delimitedListToStringArray("aa,ba,ca,da", "a,")));
		//String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete)
		//分割字符串，会把delimiter作为整体分隔符，增加一个要从分割字符串中删除的字符；
		//String[] commaDelimitedListToStringArray(String str):使用逗号分割字符串
		//是delimitedListToStringArray(str, ",")的简单方法
	}
	@Test
	public void collectionToDelimitedStringTest() {
		//将一个集合中的元素，使用前缀，后缀，分隔符拼装一个字符串，前缀后后缀是针对每一个字符串的
		String[] arrs=new String[]{"aa","bb","cc","dd"};
		assertEquals("{aa},{bb},{cc},{dd}", StringUtils.collectionToDelimitedString(Arrays.asList(arrs),",","{","}"));

	}

	/**
	 * We expect to see the empty Strings in the output.
	 */
	@Test
	public void testCommaDelimitedListToStringArrayEmptyStrings() {
		// Could read these from files
		String[] sa = StringUtils.commaDelimitedListToStringArray("a,,b");
		assertEquals("a,,b produces array length 3", 3, sa.length);
		assertTrue("components are correct",sa[0].equals("a") && sa[1].equals("") && sa[2].equals("b"));
		sa = new String[] {"", "", "a", ""};
		doTestCommaDelimitedListToStringArrayLegalMatch(sa);
	}

	private void doTestCommaDelimitedListToStringArrayLegalMatch(String[] components) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < components.length; i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(components[i]);
		}
		String[] sa = StringUtils.commaDelimitedListToStringArray(sb.toString());
		assertTrue("String array isn't null with legal match", sa != null);
		assertEquals("String array length is correct with legal match", components.length, sa.length);
		assertTrue("Output equals input", Arrays.equals(sa, components));
	}

	@Test
	public void parseLocaleStringTest() {
		// 从本地化字符串中解析出本地化信息，相当于Locale.toString()的逆向方法
		assertEquals(Locale.CHINA, StringUtils.parseLocaleString("zh_CN"));
	}
	@Test
	public void testParseLocaleStringSunnyDay() {
		Locale expectedLocale = Locale.UK;
		Locale locale = StringUtils.parseLocaleString(expectedLocale.toString());
		assertNotNull("When given a bona-fide Locale string, must not return null.", locale);
		assertEquals(expectedLocale, locale);
	}

	@Test
	public void testParseLocaleStringWithMalformedLocaleString() {
		Locale locale = StringUtils.parseLocaleString("_banjo_on_my_knee");
		assertNotNull("When given a malformed Locale string, must not return null.", locale);
	}

	@Test
	public void testParseLocaleStringWithEmptyLocaleStringYieldsNullLocale() {
		Locale locale = StringUtils.parseLocaleString("");
		assertNull("When given an empty Locale string, must return null.", locale);
	}

	@Test  // SPR-8637
	public void testParseLocaleWithMultiSpecialCharactersInVariant() {
		String variant = "proper-northern";
		String localeString = "en_GB_" + variant;
		Locale locale = StringUtils.parseLocaleString(localeString);
		assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
	}

	@Test  // SPR-3671
	public void testParseLocaleWithMultiValuedVariant() {
		String variant = "proper_northern";
		String localeString = "en_GB_" + variant;
		Locale locale = StringUtils.parseLocaleString(localeString);
		assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
	}

	@Test  // SPR-3671
	public void testParseLocaleWithMultiValuedVariantUsingSpacesAsSeparators() {
		String variant = "proper northern";
		String localeString = "en GB " + variant;
		Locale locale = StringUtils.parseLocaleString(localeString);
		assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
	}

	@Test  // SPR-3671
	public void testParseLocaleWithMultiValuedVariantUsingMixtureOfUnderscoresAndSpacesAsSeparators() {
		String variant = "proper northern";
		String localeString = "en_GB_" + variant;
		Locale locale = StringUtils.parseLocaleString(localeString);
		assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
	}

	@Test  // SPR-3671
	public void testParseLocaleWithMultiValuedVariantUsingSpacesAsSeparatorsWithLotsOfLeadingWhitespace() {
		String variant = "proper northern";
		String localeString = "en GB            " + variant;  // lots of whitespace
		Locale locale = StringUtils.parseLocaleString(localeString);
		assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
	}

	@Test  // SPR-3671
	public void testParseLocaleWithMultiValuedVariantUsingUnderscoresAsSeparatorsWithLotsOfLeadingWhitespace() {
		String variant = "proper_northern";
		String localeString = "en_GB_____" + variant;  // lots of underscores
		Locale locale = StringUtils.parseLocaleString(localeString);
		assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
	}

	@Test  // SPR-7779
	public void testParseLocaleWithInvalidCharacters() {
		try {
			StringUtils.parseLocaleString("%0D%0AContent-length:30%0D%0A%0D%0A%3Cscript%3Ealert%28123%29%3C/script%3E");
			fail("Should have thrown IllegalArgumentException");
		}catch (IllegalArgumentException ex) {
			// expected
		}
	}

	@Test  // SPR-9420
	public void testParseLocaleWithSameLowercaseTokenForLanguageAndCountry() {
		assertEquals("tr_TR", StringUtils.parseLocaleString("tr_tr").toString());
		assertEquals("bg_BG_vnt", StringUtils.parseLocaleString("bg_bg_vnt").toString());
	}

	@Test  // SPR-11806
	public void testParseLocaleWithVariantContainingCountryCode() {
		String variant = "GBtest";
		String localeString = "en_GB_" + variant;
		Locale locale = StringUtils.parseLocaleString(localeString);
		assertEquals("Variant containing country code not extracted correctly", variant, locale.getVariant());
	}

	@Test  // SPR-14718, SPR-7598
	public void testParseJava7Variant() {
		assertEquals("sr__#LATN", StringUtils.parseLocaleString("sr__#LATN").toString());
	}

	@Test  // SPR-16651
	public void testAvailableLocalesWithLocaleString() {
		for (Locale locale : Locale.getAvailableLocales()) {
			Locale parsedLocale = StringUtils.parseLocaleString(locale.toString());
			if (parsedLocale == null) {
				assertEquals("", locale.getLanguage());
			}else {
				assertEquals(parsedLocale.toString(), locale.toString());
			}
		}
	}

	@Test
	public void toLanguageTagTest() {
		// String toLanguageTag(Locale locale):把Locale转化成HTTP中Accept-Language能接受的本地化标准；
		// 比如标准的本地化字符串为：zh_CN，更改为zh-CN
		System.out.println(StringUtils.toLanguageTag(StringUtils.parseLocaleString("zh_CN")));
	}

	@Test
	public void splitArrayElementsIntoPropertiesTest() {
		// 把字符串数组中的每一个字符串按照给定的分隔符装配到一个Properties中
		String[] strs = new String[]{"key:value","key2:中文"};
		Properties ps = StringUtils.splitArrayElementsIntoProperties(strs, ":");
		assertEquals("{key=value, key2=中文}", ps.toString());
	}

	@Test  // SPR-16651
	public void testAvailableLocalesWithLanguageTag() {
		for (Locale locale : Locale.getAvailableLocales()) {
			Locale parsedLocale = StringUtils.parseLocale(locale.toLanguageTag());
			if (parsedLocale == null) {
				assertEquals("", locale.getLanguage());
			}else {
				assertEquals(parsedLocale.toLanguageTag(), locale.toLanguageTag());
			}
		}
	}

	@Test
	public void testInvalidLocaleWithLocaleString() {
		assertEquals(new Locale("invalid"), StringUtils.parseLocaleString("invalid"));
		assertEquals(new Locale("invalidvalue"), StringUtils.parseLocaleString("invalidvalue"));
		assertEquals(new Locale("invalidvalue", "foo"), StringUtils.parseLocaleString("invalidvalue_foo"));
		assertNull(StringUtils.parseLocaleString(""));
	}

	@Test
	public void testInvalidLocaleWithLanguageTag() {
		assertEquals(new Locale("invalid"), StringUtils.parseLocale("invalid"));
		assertEquals(new Locale("invalidvalue"), StringUtils.parseLocale("invalidvalue"));
		assertEquals(new Locale("invalidvalue", "foo"), StringUtils.parseLocale("invalidvalue_foo"));
		assertNull(StringUtils.parseLocale(""));
	}

}
