package org.springframework.util;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.springframework.lang.Nullable;

/**
 * Miscellaneous {@link String} utility methods.
 * Mainly for internal use within the framework; consider
 * <a href="https://commons.apache.org/proper/commons-lang/">Apache's Commons Lang</a> for a more comprehensive suite of String utilities.
 * This class delivers some simple functionality that should really be provided by the core Java {@link String} and {@link StringBuilder} classes.
 *  It also provides easy-to-use methods to convert between delimited strings, such as CSV strings, and collections and arrays.
 */
public abstract class StringUtils {

	private static final String FOLDER_SEPARATOR = "/";

	private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

	private static final String TOP_PATH = "..";

	private static final String CURRENT_PATH = ".";

	private static final char EXTENSION_SEPARATOR = '.';

	//---------------------------------------------------------------------
	// General convenience methods for working with Strings  【判断类】
	//---------------------------------------------------------------------

	/**
	 * Check whether the given object (possibly a String) is empty.
	 * This is effectly a shortcut for {@code !hasLength(String)}.
	 * This method accepts any Object as an argument, comparing it to {@code null} and the empty String.
	 * As a consequence, this method will never return {@code true} for a non-null non-String object.
	 * The Object signature is useful for general attribute handling code
	 * that commonly deals with Strings but generally has to iterate over
	 * Objects since attributes may e.g. be primitive value objects as well.
	 * <b>Note: If the object is typed to String upfront, prefer
	 * {@link #hasLength(String)} or {@link #hasText(String)} instead.</b>
	 * @param str the candidate object (possibly a String)
	 * @since 3.2.1
	 * @see #hasLength(String)
	 * @see #hasText(String)
	 */
	public static boolean isEmpty(@Nullable Object str) {
		return (str == null || "".equals(str));
	}

	/**
	 * Check that the given {@code CharSequence} is neither {@code null} nor of length 0.
	 * Note: this method returns {@code true} for a {@code CharSequence} that purely consists of whitespace.
	 * <pre class="code">
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength("") = false
	 * StringUtils.hasLength(" ") = true
	 * StringUtils.hasLength("Hello") = true
	 * @param str the {@code CharSequence} to check (may be {@code null})
	 * @return {@code true} if the {@code CharSequence} is not {@code null} and has length
	 * @see #hasLength(String)
	 * @see #hasText(CharSequence)
	 */
	public static boolean hasLength(@Nullable CharSequence str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * Check that the given String is neither {@code null} nor of length 0.
	 * Note: this method returns {@code true} for a String that purely consists of whitespace.
	 * @param str the String to check (may be {@code null})
	 * @return {@code true} if the String is not {@code null} and has length
	 * @see #hasLength(CharSequence)
	 * @see #hasText(String)
	 */
	public static boolean hasLength(@Nullable String str) {
		return (str != null && !str.isEmpty());
	}

	/**
	 * 字符串是否有内容（不为空，且不全为空格）
	 * Check whether the given {@code CharSequence} contains actual <em>text</em>.
	 * More specifically, this method returns {@code true} if the
	 * {@code CharSequence} is not {@code null}, its length is greater than 0, and it contains at least one non-whitespace character.
	 * StringUtils.hasText(null) = false
	 * StringUtils.hasText("") = false
	 * StringUtils.hasText(" ") = false
	 * StringUtils.hasText("12345") = true
	 * StringUtils.hasText(" 12345 ") = true
	 * @param str the {@code CharSequence} to check (may be {@code null})
	 * @return {@code true} if the {@code CharSequence} is not {@code null}, its length is greater than 0, and it does not contain whitespace only
	 * @see #hasText(String)
	 * @see #hasLength(CharSequence)
	 * @see Character#isWhitespace
	 */
	public static boolean hasText(@Nullable CharSequence str) {
		return (str != null && str.length() > 0 && containsText(str));
	}

	/**
	 * Check whether the given String contains actual <em>text</em>.
	 * More specifically, this method returns {@code true} if the
	 * String is not {@code null}, its length is greater than 0, and it contains at least one non-whitespace character.
	 * @param str the String to check (may be {@code null})
	 * @return {@code true} if the String is not {@code null}, its length is greater than 0, and it does not contain whitespace only
	 * @see #hasText(CharSequence)
	 * @see #hasLength(String)
	 * @see Character#isWhitespace
	 */
	public static boolean hasText(@Nullable String str) {
		return (str != null && !str.isEmpty() && containsText(str));
	}

	private static boolean containsText(CharSequence str) {
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given {@code CharSequence} contains any whitespace characters.
	 * 字符串是否包含空格
	 * @param str the {@code CharSequence} to check (may be {@code null})
	 * @return {@code true} if the {@code CharSequence} is not empty and contains at least 1 whitespace character
	 * @see Character#isWhitespace
	 */
	public static boolean containsWhitespace(@Nullable CharSequence str) {
		if (!hasLength(str)) return false;
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given String contains any whitespace characters.
	 * @param str the String to check (may be {@code null})
	 * @return {@code true} if the String is not empty and contains at least 1 whitespace character
	 * @see #containsWhitespace(CharSequence)
	 */
	public static boolean containsWhitespace(@Nullable String str) {
		return containsWhitespace((CharSequence) str);
	}

	//---------------------------------------------------------------------
	// General convenience methods for working with Strings  【字符串头尾操作】
	//---------------------------------------------------------------------
	/**
	 * 只干掉字符串的头尾空白符
	 * Trim leading and trailing whitespace from the given String.
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 * @see org.springframework.util.StringUtilsTests#testTrimWhitespace() 【测试用例】
	 */
	public static String trimWhitespace(String str) {
		if (!hasLength(str)) return str;
		int beginIndex = 0;
		int endIndex = str.length() - 1;
		while (beginIndex <= endIndex && Character.isWhitespace(str.charAt(beginIndex))) {
			beginIndex++;
		}
		while (endIndex > beginIndex && Character.isWhitespace(str.charAt(endIndex))) {
			endIndex--;
		}
		return str.substring(beginIndex, endIndex + 1);
	}

	/**
	 * 干掉所有空白符： 包括 首尾空白和中间空白
	 * Trim all whitespace from the given String:leading, trailing, and in between characters.
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 * @see org.springframework.util.StringUtilsTests#testTrimAllWhitespace() 【测试用例】
	 */
	public static String trimAllWhitespace(String str) {
		if (!hasLength(str)) return str;
		int len = str.length();
		StringBuilder sb = new StringBuilder(str.length());
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			if (!Character.isWhitespace(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * Trim leading whitespace from the given String.
	 * 只干掉头部空白符
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimLeadingWhitespace(String str) {
		if (!hasLength(str)) return str;
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	/**
	 * Trim trailing whitespace from the given String.
	 * 只干掉尾部空白符
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimTrailingWhitespace(String str) {
		if (!hasLength(str)) return str;
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * Trim all occurrences of the supplied leading character from the given String.
	 * 只干掉 头部 的给定字符
	 * @param str the String to check
	 * @param leadingCharacter the leading character to be trimmed
	 * @return the trimmed String
	 */
	public static String trimLeadingCharacter(String str, char leadingCharacter) {
		if (!hasLength(str)) return str;
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	/**
	 * Trim all occurrences of the supplied trailing character from the given String.
	 * @param str the String to check
	 * @param trailingCharacter the trailing character to be trimmed
	 * @return the trimmed String
	 */
	public static String trimTrailingCharacter(String str, char trailingCharacter) {
		if (!hasLength(str)) return str;
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && sb.charAt(sb.length() - 1) == trailingCharacter) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * Test if the given String starts with the specified prefix,ignoring upper/lower case.
	 * 判断给定字符串的前缀是否与给定的前缀匹配
	 * @param str the String to check
	 * @param prefix the prefix to look for
	 * @see java.lang.String#startsWith
	 */
	public static boolean startsWithIgnoreCase(@Nullable String str, @Nullable String prefix) {
		return (str != null && prefix != null && str.length() >= prefix.length() && str.regionMatches(true, 0, prefix, 0, prefix.length()));
	}

	/**
	 * Test if the given String ends with the specified suffix, ignoring upper/lower case.
	 * 判断给定字符串的后缀是否与给定的后缀匹配
	 * @param str the String to check
	 * @param suffix the suffix to look for
	 * @see java.lang.String#endsWith
	 */
	public static boolean endsWithIgnoreCase(@Nullable String str, @Nullable String suffix) {
		return (str != null && suffix != null && str.length() >= suffix.length() && str.regionMatches(true, str.length() - suffix.length(), suffix, 0, suffix.length()));
	}

	/**
	 * Test whether the given string matches the given substring at the given index.
	 * 用来判断str在index索引位置是否和substring匹配  (大小写敏感)
	 * 判断从指定索引开始，是否匹配子字符串
	 * @param str the original string (or StringBuilder)
	 * @param index the index in the original string to start matching against
	 * @param substring the substring to match at the given index
	 */
	public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
		if (index + substring.length() > str.length()) return false;
		for (int i = 0; i < substring.length(); i++) {
			if (str.charAt(index + i) != substring.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Count the occurrences of the substring {@code sub} in string {@code str}.
	 * 计算给定子串(sub)在指定字符串(str)中出现的次数
	 * @param str string to search in
	 * @param sub string to search for
	 */
	public static int countOccurrencesOf(String str, String sub) {
		if (!hasLength(str) || !hasLength(sub)) return 0;
		int count = 0;
		int pos = 0;
		int idx;
		while ((idx = str.indexOf(sub, pos)) != -1) {
			++count;
			pos = idx + sub.length();
		}
		return count;
	}

	/**
	 * Replace all occurrences of a substring within a string with another string.
	 * 将inString字符串中出现的oldPattern，使用newPattern替换掉 (大小写敏感)
	 * @param inString String to examine
	 * @param oldPattern String to replace
	 * @param newPattern String to insert
	 * @return a String with the replacements
	 */
	public static String replace(String inString, String oldPattern, @Nullable String newPattern) {
		if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
			return inString;
		}
		int index = inString.indexOf(oldPattern);
		if (index == -1) {
			// no occurrence -> can return input as-is
			return inString;
		}
		int capacity = inString.length();
		if (newPattern.length() > oldPattern.length()) {
			capacity += 16;
		}
		StringBuilder sb = new StringBuilder(capacity);
		int pos = 0;  // our position in the old string
		int patLen = oldPattern.length();
		while (index >= 0) {
			sb.append(inString, pos, index);
			sb.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		// append any characters to the right of a match
		sb.append(inString.substring(pos));
		return sb.toString();
	}

	/**
	 * Delete all occurrences of the given substring.
	 * 删除所有匹配的子字符串
	 * @param inString the original String
	 * @param pattern the pattern to delete all occurrences of
	 * @return the resulting String
	 */
	public static String delete(String inString, String pattern) {
		return replace(inString, pattern, "");
	}

	/**
	 * Delete any character in a given String.
	 * 删除子字符串中任意出现的字符
	 * @param inString the original String
	 * @param charsToDelete a set of characters to delete.
	 * E.g. "az\n" will delete 'a's, 'z's and new lines.
	 * eg:	StringUtils.deleteAny("Able was I ere I saw Elba", "I") ===> "Able was  ere  saw Elba"
	 * @return the resulting String
	 */
	public static String deleteAny(String inString, @Nullable String charsToDelete) {
		if (!hasLength(inString) || !hasLength(charsToDelete)) {
			return inString;
		}
		StringBuilder sb = new StringBuilder(inString.length());
		for (int i = 0; i < inString.length(); i++) {
			char c = inString.charAt(i);
			if (charsToDelete.indexOf(c) == -1) {
				sb.append(c);
			}
		}
		return sb.toString();
	}


	//---------------------------------------------------------------------
	// Convenience methods for working with formatted Strings 【文件路径名称相关操作】
	//---------------------------------------------------------------------

	/**
	 * Quote the given String with single quotes.
	 * 在字符串前后增加单引号,比较适合在日志时候使用；
	 * @param str the input String (e.g. "myString")
	 * @return the quoted String (e.g. "'myString'"), or {@code null} if the input was {@code null}
	 */
	@Nullable
	public static String quote(@Nullable String str) {
		return (str != null ? "'" + str + "'" : null);
	}

	/**
	 * Turn the given Object into a String with single quotes
	 * if it is a String; keeping the Object as-is else.
	 * @param obj the input Object (e.g. "myString")
	 * @return the quoted String (e.g. "'myString'"),
	 * or the input object as-is if not a String
	 */
	@Nullable
	public static Object quoteIfString(@Nullable Object obj) {
		return (obj instanceof String ? quote((String) obj) : obj);
	}

	/**
	 * Unqualify a string qualified by a '.' dot character.
	 * For example,"this.name.is.qualified", returns "qualified".
	 * 得到以.分割的最后一个值，可以非常方便的获取类似类名或者文件后缀
	 * @param qualifiedName the qualified name
	 */
	public static String unqualify(String qualifiedName) {
		return unqualify(qualifiedName, '.');
	}

	/**
	 * Unqualify a string qualified by a separator character.
	 * For example,"this:name:is:qualified" returns "qualified" if using a ':' separator.
	 * 得到以给定字符分割的最后一个值，可以非常方便的获取类似文件名
	 * @param qualifiedName the qualified name
	 * @param separator the separator
	 */
	public static String unqualify(String qualifiedName, char separator) {
		return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
	}

	/**
	 * Capitalize a String, changing the first letter to upper case as per {@link Character#toUpperCase(char)}.
	 * No other letters are changed.
	 * 将给定字符串首字母大写
	 * @param str the String to capitalize
	 * @return the capitalized String
	 */
	public static String capitalize(String str) {
		return changeFirstCharacterCase(str, true);
	}

	/**
	 * Uncapitalize a String, changing the first letter to lower case as per {@link Character#toLowerCase(char)}.
	 * No other letters are changed.
	 * 将给定字符串首字母小写
	 * @param str the String to uncapitalize
	 * @return the uncapitalized String
	 */
	public static String uncapitalize(String str) {
		return changeFirstCharacterCase(str, false);
	}

	private static String changeFirstCharacterCase(String str, boolean capitalize) {
		if (!hasLength(str)) return str;
		char baseChar = str.charAt(0);
		char updatedChar = capitalize ? Character.toUpperCase(baseChar):Character.toLowerCase(baseChar);//- modify
		if (baseChar == updatedChar) return str;
		char[] chars = str.toCharArray();
		chars[0] = updatedChar;
		return new String(chars, 0, chars.length);
	}

	/**
	 * Extract the filename from the given Java resource path,
	 * e.g. {@code "mypath/myfile.txt" -> "myfile.txt"}.
	 * 从给定的java资源路径中 提取文件名,就不需要再使用 FilenameUtils
	 * @param path the file path (may be {@code null})
	 * @return the extracted filename, or {@code null} if none
	 */
	@Nullable
	public static String getFilename(@Nullable String path) {
		if (path == null) return null;
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
	}

	/**
	 * Extract the filename extension from the given Java resource path,
	 * e.g. "mypath/myfile.txt" -> "txt".
	 * 从给定的java资源路径中 提取文件名后缀
	 * @param path the file path (may be {@code null})
	 * @return the extracted filename extension, or {@code null} if none
	 */
	@Nullable
	public static String getFilenameExtension(@Nullable String path) {
		if (path == null) return null;
		int extIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		if (extIndex == -1) return null;
		int folderIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (folderIndex > extIndex) return null;
		return path.substring(extIndex + 1);
	}

	/**
	 * Strip the filename extension from the given Java resource path,
	 * e.g. "mypath/myfile.txt" -> "mypath/myfile".
	 * 从给定的java资源路径中，干掉文件后缀名
	 * @param path the file path
	 * @return the path with stripped filename extension
	 */
	public static String stripFilenameExtension(String path) {
		int extIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		if (extIndex == -1) return path;
		int folderIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (folderIndex > extIndex) return path;
		return path.substring(0, extIndex);
	}

	/**
	 * Apply the given relative path to the given Java resource path, assuming standard Java folder separation (i.e. "/" separators).
	 * @param path the path to start from (usually a full file path)
	 * @param relativePath the relative path to apply (relative to the full file path above)
	 * @return the full file path that results from applying the relative path
	 */
	public static String applyRelativePath(String path, String relativePath) {
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (separatorIndex != -1) {
			String newPath = path.substring(0, separatorIndex);
			if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
				newPath += FOLDER_SEPARATOR;
			}
			return newPath + relativePath;
		}else {
			return relativePath;
		}
	}

	/**
	 * Normalize the path by suppressing sequences like "path/.." and inner simple dots.
	 * 就是计算路径  将 .. 等切换上级目录的 符号转换成实际的路径
	 * The result is convenient for path comparison. For other uses,notice that Windows separators ("\") are replaced by simple slashes.
	 * @param path the original path
	 * @return the normalized path
	 */
	public static String cleanPath(String path) {
		if (!hasLength(path)) {
			return path;
		}
		String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);
		// Strip prefix from path to analyze, to not treat it as part of the first path element.
		// This is necessary to correctly parse paths like "file:core/../core/io/Resource.class", where the ".."
		// should just strip the first "core" directory while keeping the "file:" prefix.
		int prefixIndex = pathToUse.indexOf(':');
		String prefix = "";
		if (prefixIndex != -1) {
			prefix = pathToUse.substring(0, prefixIndex + 1);
			if (prefix.contains(FOLDER_SEPARATOR)) {
				prefix = "";
			}else {
				pathToUse = pathToUse.substring(prefixIndex + 1);
			}
		}
		if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
			prefix = prefix + FOLDER_SEPARATOR;
			pathToUse = pathToUse.substring(1);
		}
		String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
		LinkedList<String> pathElements = new LinkedList<>();
		int tops = 0;
		for (int i = pathArray.length - 1; i >= 0; i--) {
			String element = pathArray[i];
			if (CURRENT_PATH.equals(element)) {
				// Points to current directory - drop it.
			}else if (TOP_PATH.equals(element)) {
				// Registering top path found.
				tops++;
			}else {
				if (tops > 0) {
					// Merging path element with element corresponding to top path.
					tops--;
				}else {
					// Normal path element found.
					pathElements.add(0, element);
				}
			}
		}
		// Remaining top paths need to be retained.
		for (int i = 0; i < tops; i++) {
			pathElements.add(0, TOP_PATH);
		}
		// If nothing else left, at least explicitly point to current path.
		if (pathElements.size() == 1 && "".equals(pathElements.getLast()) && !prefix.endsWith(FOLDER_SEPARATOR)) {
			pathElements.add(0, CURRENT_PATH);
		}
		return prefix + collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
	}

	/**
	 * Compare two paths after normalization of them.
	 * @param path1 first path for comparison
	 * @param path2 second path for comparison
	 * @return whether the two paths are equivalent after normalization
	 */
	public static boolean pathEquals(String path1, String path2) {
		return cleanPath(path1).equals(cleanPath(path2));
	}

	/**
	 * Decode the given encoded URI component value. Based on the following rules:
	 * <li>Alphanumeric characters {@code "a"} through {@code "z"}, {@code "A"} through {@code "Z"},and {@code "0"} through {@code "9"} stay the same.</li>
	 * <li>Special characters {@code "-"}, {@code "_"}, {@code "."}, and {@code "*"} stay the same.</li>
	 * <li>A sequence "{@code %xy}" is interpreted as a hexadecimal representation of the character.</li>
	 * @param source the encoded String
	 * @param charset the character set
	 * @return the decoded value
	 * @throws IllegalArgumentException when the given source contains invalid encoded sequences
	 * @since 5.0
	 * @see java.net.URLDecoder#decode(String, String)
	 */
	public static String uriDecode(String source, Charset charset) {
		int length = source.length();
		if (length == 0) return source;
		Assert.notNull(charset, "Charset must not be null");
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		boolean changed = false;
		for (int i = 0; i < length; i++) {
			int ch = source.charAt(i);
			if (ch == '%') {
				if (i + 2 < length) {
					char hex1 = source.charAt(i + 1);
					char hex2 = source.charAt(i + 2);
					int u = Character.digit(hex1, 16);
					int l = Character.digit(hex2, 16);
					if (u == -1 || l == -1) {
						throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
					}
					bos.write((char) ((u << 4) + l));
					i += 2;
					changed = true;
				}else {
					throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
				}
			}else {
				bos.write(ch);
			}
		}
		return (changed ? new String(bos.toByteArray(), charset) : source);
	}

	/**
	 * Parse the given String value into a {@link Locale}, accepting the {@link Locale#toString} format as well as BCP 47 language tags.
	 * @param localeValue the locale value: following either {@code Locale's} {@code toString()} format ("en", "en_UK", etc), also accepting spaces as
	 * separators (as an alternative to underscores), or BCP 47 (e.g. "en-UK") as specified by {@link Locale#forLanguageTag} on Java 7+
	 * @return a corresponding {@code Locale} instance, or {@code null} if none
	 * @throws IllegalArgumentException in case of an invalid locale specification
	 * @since 5.0.4
	 * @see #parseLocaleString
	 * @see Locale#forLanguageTag
	 */
	@Nullable
	public static Locale parseLocale(String localeValue) {
		String[] tokens = tokenizeLocaleSource(localeValue);
		if (tokens.length == 1) {
			validateLocalePart(localeValue);
			Locale resolved = Locale.forLanguageTag(localeValue);
			if (resolved.getLanguage().length() > 0) return resolved;
		}
		return parseLocaleTokens(localeValue, tokens);
	}

	/**
	 * 从本地化字符串中解析出本地化信息，相当于Locale.toString()的逆向方法
	 * Parse the given String representation into a {@link Locale}.
	 * For many parsing scenarios, this is an inverse operation of {@link Locale#toString Locale's toString},in a lenient sense.
	 * This method does not aim for strict {@code Locale} design compliance;
	 * it is rather specifically tailored for typical Spring parsing needs.
	 * <b>Note: This delegate does not accept the BCP 47 language tag format.
	 * Please use {@link #parseLocale} for lenient parsing of both formats.</b>
	 * @param localeString the locale String: following {@code Locale's}
	 * {@code toString()} format ("en", "en_UK", etc), also accepting spaces as separators (as an alternative to underscores)
	 * @return a corresponding {@code Locale} instance, or {@code null} if none
	 * @throws IllegalArgumentException in case of an invalid locale specification
	 */
	@Nullable
	public static Locale parseLocaleString(String localeString) {
		return parseLocaleTokens(localeString, tokenizeLocaleSource(localeString));
	}

	private static String[] tokenizeLocaleSource(String localeSource) {
		return tokenizeToStringArray(localeSource, "_ ", false, false);
	}

	@Nullable
	private static Locale parseLocaleTokens(String localeString, String[] tokens) {
		String language = (tokens.length > 0 ? tokens[0] : "");
		String country = (tokens.length > 1 ? tokens[1] : "");
		validateLocalePart(language);
		validateLocalePart(country);
		String variant = "";
		if (tokens.length > 2) {
			// There is definitely a variant, and it is everything after the country
			// code sans the separator between the country code and the variant.
			int endIndexOfCountryCode = localeString.indexOf(country, language.length()) + country.length();
			// Strip off any leading '_' and whitespace, what's left is the variant.
			variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
			if (variant.startsWith("_")) {
				variant = trimLeadingCharacter(variant, '_');
			}
		}
		if (variant.isEmpty() && country.startsWith("#")) {
			variant = country;
			country = "";
		}
		return (language.length() > 0 ? new Locale(language, country, variant) : null);
	}

	private static void validateLocalePart(String localePart) {
		for (int i = 0; i < localePart.length(); i++) {
			char ch = localePart.charAt(i);
			if (ch != ' ' && ch != '_' && ch != '-' && ch != '#' && !Character.isLetterOrDigit(ch)) {
				throw new IllegalArgumentException("Locale part \"" + localePart + "\" contains invalid characters");
			}
		}
	}

	/**
	 * Determine the RFC 3066 compliant language tag,as used for the HTTP "Accept-Language" header.
	 * @param locale the Locale to transform to a language tag
	 * @return the RFC 3066 compliant language tag as String
	 * @deprecated as of 5.0.4, in favor of {@link Locale#toLanguageTag()}
	 */
	@Deprecated
	public static String toLanguageTag(Locale locale) {
		return locale.getLanguage() + (hasText(locale.getCountry()) ? "-" + locale.getCountry() : "");
	}

	/**
	 * Parse the given {@code timeZoneString} value into a {@link TimeZone}.
	 * @param timeZoneString the time zone String, following {@link TimeZone#getTimeZone(String)}  but throwing {@link IllegalArgumentException} in case of an invalid time zone specification
	 * @return a corresponding {@link TimeZone} instance
	 * @throws IllegalArgumentException in case of an invalid time zone specification
	 */
	public static TimeZone parseTimeZoneString(String timeZoneString) {
		TimeZone timeZone = TimeZone.getTimeZone(timeZoneString);
		if ("GMT".equals(timeZone.getID()) && !timeZoneString.startsWith("GMT")) {
			// We don't want that GMT fallback...
			throw new IllegalArgumentException("Invalid time zone specification '" + timeZoneString + "'");
		}
		return timeZone;
	}


	//---------------------------------------------------------------------
	// Convenience methods for working with String arrays
	//---------------------------------------------------------------------
	/**
	 * 把字符串集合转成字符串数组
	 * Copy the given {@link Collection} into a String array.
	 * The {@code Collection} must contain String elements only.
	 * @param collection the {@code Collection} to copy (potentially {@code null} or empty)
	 * @return the resulting String array
	 */
	public static String[] toStringArray(@Nullable Collection<String> collection) {
		return (collection != null ? collection.toArray(new String[0]) : new String[0]);
	}

	/**
	 * 把字符串枚举类型转成字符串数组
	 * Copy the given {@link Enumeration} into a String array.
	 * The {@code Enumeration} must contain String elements only.
	 * @param enumeration the {@code Enumeration} to copy (potentially {@code null} or empty)
	 * @return the resulting String array
	 */
	public static String[] toStringArray(@Nullable Enumeration<String> enumeration) {
		return (enumeration != null ? toStringArray(Collections.list(enumeration)) : new String[0]);
	}

	/**
	 * 把一个字符串添加到一个字符串数组中
	 * Append the given String to the given String array, returning a new array consisting of the input array contents plus  the given String.
	 * @param array the array to append to (can be {@code null})
	 * @param str the String to append
	 * @return the new array (never {@code null})
	 */
	public static String[] addStringToArray(@Nullable String[] array, String str) {
		if (ObjectUtils.isEmpty(array)) return new String[] {str};
		String[] newArr = new String[array.length + 1];
		System.arraycopy(array, 0, newArr, 0, array.length);
		newArr[array.length] = str;
		return newArr;
	}

	/**
	 * 拼接两个字符串数组，重复的元素不会覆盖
	 * Concatenate the given String arrays into one, with overlapping array elements included twice.
	 * The order of elements in the original arrays is preserved.
	 * @param array1 the first array (can be {@code null})
	 * @param array2 the second array (can be {@code null})
	 * @return the new array ({@code null} if both given arrays were {@code null})
	 */
	@Nullable
	public static String[] concatenateStringArrays(@Nullable String[] array1, @Nullable String[] array2) {
		if (ObjectUtils.isEmpty(array1)) return array2;
		if (ObjectUtils.isEmpty(array2)) return array1;
		String[] newArr = new String[array1.length + array2.length];
		System.arraycopy(array1, 0, newArr, 0, array1.length);
		System.arraycopy(array2, 0, newArr, array1.length, array2.length);
		return newArr;
	}

	/**
	 * 拼接两个字符串数组，重复的元素将被覆盖
	 * Merge the given String arrays into one, with overlapping array elements only included once.
	 * The order of elements in the original arrays is preserved (with the exception of overlapping elements,which are only included on their first occurrence).
	 * @param array1 the first array (can be {@code null})
	 * @param array2 the second array (can be {@code null})
	 * @return the new array ({@code null} if both given arrays were {@code null})
	 * @deprecated as of 4.3.15, in favor of manual merging via {@link LinkedHashSet} (with every entry included at most once, even entries within the first array)
	 */
	@Deprecated
	@Nullable
	public static String[] mergeStringArrays(@Nullable String[] array1, @Nullable String[] array2) {
		if (ObjectUtils.isEmpty(array1)) return array2;
		if (ObjectUtils.isEmpty(array2)) return array1;
		List<String> result = new ArrayList<>();
		result.addAll(Arrays.asList(array1));
		for (String str : array2) {
			if (!result.contains(str)) result.add(str);
		}
		return toStringArray(result);
	}

	/**
	 * Sort the given String array if necessary.
	 * @param array the original array (potentially empty)
	 * @return the array in sorted form (never {@code null})
	 */
	public static String[] sortStringArray(String[] array) {
		if (ObjectUtils.isEmpty(array)) return array;
		Arrays.sort(array);
		return array;
	}

	/**
	 * 把字符串数组中所有字符串执行trim功能
	 * Trim the elements of the given String array,calling {@code String.trim()} on each of them.
	 * @param array the original String array (potentially empty)
	 * @return the resulting array (of the same size) with trimmed elements
	 */
	public static String[] trimArrayElements(String[] array) {
		if (ObjectUtils.isEmpty(array)) return array;
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			String element = array[i];
			result[i] = (element != null ? element.trim() : null);
		}
		return result;
	}

	/**
	 * 去掉给定字符串数组中重复的元素，能保持原顺序；
	 * Remove duplicate strings from the given array.
	 * As of 4.2, it preserves the original order, as it uses a {@link LinkedHashSet}.
	 * @param array the String array (potentially empty)
	 * @return an array without duplicates, in natural sort order
	 */
	public static String[] removeDuplicateStrings(String[] array) {
		if (ObjectUtils.isEmpty(array)) return array;
		Set<String> set = new LinkedHashSet<>(Arrays.asList(array));
		return toStringArray(set);
	}

	/**
	 * Split a String at the first occurrence of the delimiter.
	 * Does not include the delimiter in the result.
	 * @param toSplit the string to split (potentially {@code null} or empty)
	 * @param delimiter to split the string up with (potentially {@code null} or empty)
	 * @return a two element array with index 0 being before the delimiter, and index 1 being after the delimiter (neither element includes the delimiter);
	 * or {@code null} if the delimiter wasn't found in the given input String
	 */
	@Nullable
	public static String[] split(@Nullable String toSplit, @Nullable String delimiter) {
		if (!hasLength(toSplit) || !hasLength(delimiter)) return null;
		int offset = toSplit.indexOf(delimiter);
		if (offset < 0) return null;
		String beforeDelimiter = toSplit.substring(0, offset);
		String afterDelimiter = toSplit.substring(offset + delimiter.length());
		return new String[] {beforeDelimiter, afterDelimiter};
	}

	/**
	 * Take an array of strings and split each element based on the given delimiter.
	 * A {@code Properties} instance is then generated, with the left of the delimiter providing the key,and the right of the delimiter providing the value.
	 * Will trim both the key and value before adding them to the {@code Properties}.
	 * @param array the array to process
	 * @param delimiter to split each element using (typically the equals symbol)
	 * @return a {@code Properties} instance representing the array contents,or {@code null} if the array to process was {@code null} or empty
	 */
	@Nullable
	public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter) {
		return splitArrayElementsIntoProperties(array, delimiter, null);
	}

	/**
	 * 把字符串数组中的每一个字符串按照给定的分隔符装配到一个Properties中,并删除指定字符串，比如括号之类的；
	 * Take an array of strings and split each element based on the given delimiter.
	 * A {@code Properties} instance is then generated, with the left of the  delimiter providing the key, and the right of the delimiter providing the value.
	 * Will trim both the key and value before adding them to the {@code Properties} instance.
	 * @param array the array to process
	 * @param delimiter to split each element using (typically the equals symbol)
	 * @param charsToDelete one or more characters to remove from each element
	 * prior to attempting the split operation (typically the quotation mark symbol), or {@code null} if no removal should occur
	 * @return a {@code Properties} instance representing the array contents,or {@code null} if the array to process was {@code null} or empty
	 */
	@Nullable
	public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter, @Nullable String charsToDelete) {
		if (ObjectUtils.isEmpty(array)) return null;
		Properties result = new Properties();
		for (String element : array) {
			if (charsToDelete != null) {
				element = deleteAny(element, charsToDelete);
			}
			String[] splittedElement = split(element, delimiter);
			if (splittedElement == null) {
				continue;
			}
			result.setProperty(splittedElement[0].trim(), splittedElement[1].trim());
		}
		return result;
	}

	/**
	 * Tokenize the given String into a String array via a {@link StringTokenizer}.
	 * Trims tokens and omits empty tokens.
	 * The given {@code delimiters} string can consist of any number of delimiter characters.
	 * Each of those characters can be used to separate tokens.
	 * A delimiter is always a single character; for multi-character delimiters, consider using {@link #delimitedListToStringArray}.
	 * @param str the String to tokenize (potentially {@code null} or empty)
	 * @param delimiters the delimiter characters, assembled as a String (each of the characters is individually considered as a delimiter)
	 * @return an array of the tokens
	 * @see java.util.StringTokenizer
	 * @see String#trim()
	 * @see #delimitedListToStringArray
	 */
	public static String[] tokenizeToStringArray(@Nullable String str, String delimiters) {
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	/**
	 * Tokenize the given String into a String array via a {@link StringTokenizer}.
	 * The given {@code delimiters} string can consist of any number of delimiter characters.
	 * Each of those characters can be used to separate tokens.
	 * A delimiter is always a single character; for multi-character delimiters, consider using {@link #delimitedListToStringArray}.
	 * @param str the String to tokenize (potentially {@code null} or empty)
	 * @param delimiters the delimiter characters, assembled as a String (each of the characters is individually considered as a delimiter)
	 * @param trimTokens trim the tokens via {@link String#trim()}
	 * @param ignoreEmptyTokens omit empty tokens from the result array (only applies to tokens that are empty after trimming; StringTokenizer will not consider subsequent delimiters as token in the first place).
	 * @return an array of the tokens
	 * @see java.util.StringTokenizer
	 * @see String#trim()
	 * @see #delimitedListToStringArray
	 */
	public static String[] tokenizeToStringArray(@Nullable String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
		if (str == null) return new String[0];
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	/**
	 * Take a String that is a delimited list and convert it into a String array.
	 * A single {@code delimiter} may consist of more than one character, but it will still be considered as a single delimiter string,
	 * rather than as bunch of potential delimiter characters, in contrast to {@link #tokenizeToStringArray}.
	 * @param str the input String (potentially {@code null} or empty)
	 * @param delimiter the delimiter between elements (this is a single delimiter, rather than a bunch individual delimiter characters)
	 * @return an array of the tokens in the list
	 * @see #tokenizeToStringArray
	 */
	public static String[] delimitedListToStringArray(@Nullable String str, @Nullable String delimiter) {
		return delimitedListToStringArray(str, delimiter, null);
	}

	/**
	 * 分割字符串，会把delimiter作为整体分隔符，增加一个要从分割字符串中删除的字符；
	 * Take a String that is a delimited list and convert it into a String array.
	 * A single {@code delimiter} may consist of more than one character,but it will still be considered as a single delimiter string,
	 * rather than as bunch of potential delimiter characters, in contrast to {@link #tokenizeToStringArray}.
	 * @param str the input String (potentially {@code null} or empty)
	 * @param delimiter the delimiter between elements (this is a single delimiter, rather than a bunch individual delimiter characters)
	 * @param charsToDelete a set of characters to delete; useful for deleting unwanted line breaks: e.g. "\r\n\f" will delete all new lines and line feeds in a String
	 * @return an array of the tokens in the list
	 * @see #tokenizeToStringArray
	 */
	public static String[] delimitedListToStringArray(@Nullable String str, @Nullable String delimiter, @Nullable String charsToDelete) {
		if (str == null) return new String[0];
		if (delimiter == null) return new String[] {str};

		List<String> result = new ArrayList<>();
		if (delimiter.isEmpty()) {
			for (int i = 0; i < str.length(); i++) {
				result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
			}
		}else {
			int pos = 0;
			int delPos;
			while ((delPos = str.indexOf(delimiter, pos)) != -1) {
				result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
				pos = delPos + delimiter.length();
			}
			if (str.length() > 0 && pos <= str.length()) {
				// Add rest of String, but not in case of empty input.
				result.add(deleteAny(str.substring(pos), charsToDelete));
			}
		}
		return toStringArray(result);
	}

	/**
	 * 使用逗号分割字符串
	 * Convert a comma delimited list (e.g., a row from a CSV file) into an array of strings.
	 * @param str the input String (potentially {@code null} or empty)
	 * @return an array of strings, or the empty array in case of empty input
	 */
	public static String[] commaDelimitedListToStringArray(@Nullable String str) {
		return delimitedListToStringArray(str, ",");
	}

	/**
	 * 使用【逗号】分割字符串，并放到set中去重
	 * Convert a comma delimited list (e.g., a row from a CSV file) into a set.
	 * Note that this will suppress duplicates, and as of 4.2, the elements in
	 * the returned set will preserve the original order in a {@link LinkedHashSet}.
	 * @param str the input String (potentially {@code null} or empty)
	 * @return a set of String entries in the list
	 * @see #removeDuplicateStrings(String[])
	 */
	public static Set<String> commaDelimitedListToSet(@Nullable String str) {
		String[] tokens = commaDelimitedListToStringArray(str);
		return new LinkedHashSet<>(Arrays.asList(tokens));
	}

	// 使用 【指定分隔符】 分割字符串，并放到set中去重
	public static Set<String> commaDelimitedListToSet(@Nullable String str,@Nullable String delimiter) {
		String[] tokens = delimitedListToStringArray(str, delimiter);
		return new LinkedHashSet<>(Arrays.asList(tokens));
	}

	/**
	 * 将一个集合中的元素，使用前缀，后缀，分隔符拼装一个字符串，前缀后后缀是针对每一个字符串的
	 * Convert a {@link Collection} to a delimited String (e.g. CSV).
	 * Useful for {@code toString()} implementations.
	 * @param coll the {@code Collection} to convert (potentially {@code null} or empty)
	 * @param delim the delimiter to use (typically a ",")
	 * @param prefix the String to start each element with
	 * @param suffix the String to end each element with
	 * @return the delimited String
	 */
	public static String collectionToDelimitedString(@Nullable Collection<?> coll, String delim, String prefix, String suffix) {
		if (CollectionUtils.isEmpty(coll)) return "";
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = coll.iterator();
		while (it.hasNext()) {
			sb.append(prefix).append(it.next()).append(suffix);
			if (it.hasNext()) sb.append(delim);
		}
		return sb.toString();
	}

	/**
	 * 集合转成指定字符串连接的字符串；
	 * Convert a {@code Collection} into a delimited String (e.g. CSV).
	 * 		List<String> list = Arrays.asList("1","2","3");
	 * 		StringUtils.collectionToDelimitedString(list, "*") ===> 1*2*3
	 * Useful for {@code toString()} implementations.
	 * @param coll the {@code Collection} to convert (potentially {@code null} or empty)
	 * @param delim the delimiter to use (typically a ",")
	 * @return the delimited String
	 */
	public static String collectionToDelimitedString(@Nullable Collection<?> coll, String delim) {
		return collectionToDelimitedString(coll, delim, "", "");
	}

	/**
	 * 集合转成逗号连接的字符串；
	 * Convert a {@code Collection} into a delimited String (e.g., CSV).
	 * Useful for {@code toString()} implementations.
	 * @param coll the {@code Collection} to convert (potentially {@code null} or empty)
	 * @return the delimited String
	 */
	public static String collectionToCommaDelimitedString(@Nullable Collection<?> coll) {
		return collectionToDelimitedString(coll, ",");
	}

	/**
	 * 数组使用指定字符串连接；
	 * Convert a String array into a delimited String (e.g. CSV).
	 * Useful for {@code toString()} implementations.
	 * @param arr the array to display (potentially {@code null} or empty)
	 * @param delim the delimiter to use (typically a ",")
	 * @return the delimited String
	 */
	public static String arrayToDelimitedString(@Nullable Object[] arr, String delim) {
		if (ObjectUtils.isEmpty(arr)) return "";
		if (arr.length == 1) return ObjectUtils.nullSafeToString(arr[0]);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) sb.append(delim);
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	/**
	 * 使用逗号连接数组，拼成字符串；
	 * Convert a String array into a comma delimited String (i.e., CSV).
	 * Useful for {@code toString()} implementations.
	 * @param arr the array to display (potentially {@code null} or empty)
	 * @return the delimited String
	 */
	public static String arrayToCommaDelimitedString(@Nullable Object[] arr) {
		return arrayToDelimitedString(arr, ",");
	}

}
