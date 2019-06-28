

package org.springframework.cache.interceptor;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests for {@link SimpleKeyGenerator} and {@link SimpleKey}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class SimpleKeyGeneratorTests {

	private final SimpleKeyGenerator generator = new SimpleKeyGenerator();


	@Test
	public void noValues() {
		Object k1 = generateKey(new Object[] {});
		Object k2 = generateKey(new Object[] {});
		Object k3 = generateKey(new Object[] { "different" });
		assertThat(k1.hashCode(), equalTo(k2.hashCode()));
		assertThat(k1.hashCode(), not(equalTo(k3.hashCode())));
		assertThat(k1, equalTo(k2));
		assertThat(k1, not(equalTo(k3)));
	}

	@Test
	public void singleValue(){
		Object k1 = generateKey(new Object[] { "a" });
		Object k2 = generateKey(new Object[] { "a" });
		Object k3 = generateKey(new Object[] { "different" });
		assertThat(k1.hashCode(), equalTo(k2.hashCode()));
		assertThat(k1.hashCode(), not(equalTo(k3.hashCode())));
		assertThat(k1, equalTo(k2));
		assertThat(k1, not(equalTo(k3)));
		assertThat(k1, equalTo("a"));
	}

	@Test
	public void multipleValues()  {
		Object k1 = generateKey(new Object[] { "a", 1, "b" });
		Object k2 = generateKey(new Object[] { "a", 1, "b" });
		Object k3 = generateKey(new Object[] { "b", 1, "a" });
		assertThat(k1.hashCode(), equalTo(k2.hashCode()));
		assertThat(k1.hashCode(), not(equalTo(k3.hashCode())));
		assertThat(k1, equalTo(k2));
		assertThat(k1, not(equalTo(k3)));
	}

	@Test
	public void singleNullValue() {
		Object k1 = generateKey(new Object[] { null });
		Object k2 = generateKey(new Object[] { null });
		Object k3 = generateKey(new Object[] { "different" });
		assertThat(k1.hashCode(), equalTo(k2.hashCode()));
		assertThat(k1.hashCode(), not(equalTo(k3.hashCode())));
		assertThat(k1, equalTo(k2));
		assertThat(k1, not(equalTo(k3)));
		assertThat(k1, instanceOf(SimpleKey.class));
	}

	@Test
	public void multipleNullValues() {
		Object k1 = generateKey(new Object[] { "a", null, "b", null });
		Object k2 = generateKey(new Object[] { "a", null, "b", null });
		Object k3 = generateKey(new Object[] { "a", null, "b" });
		assertThat(k1.hashCode(), equalTo(k2.hashCode()));
		assertThat(k1.hashCode(), not(equalTo(k3.hashCode())));
		assertThat(k1, equalTo(k2));
		assertThat(k1, not(equalTo(k3)));
	}

	@Test
	public void plainArray() {
		Object k1 = generateKey(new Object[] { new String[]{"a", "b"} });
		Object k2 = generateKey(new Object[] { new String[]{"a", "b"} });
		Object k3 = generateKey(new Object[] { new String[]{"b", "a"} });
		assertThat(k1.hashCode(), equalTo(k2.hashCode()));
		assertThat(k1.hashCode(), not(equalTo(k3.hashCode())));
		assertThat(k1, equalTo(k2));
		assertThat(k1, not(equalTo(k3)));
	}

	@Test
	public void arrayWithExtraParameter() {
		Object k1 = generateKey(new Object[] { new String[]{"a", "b"}, "c" });
		Object k2 = generateKey(new Object[] { new String[]{"a", "b"}, "c" });
		Object k3 = generateKey(new Object[] { new String[]{"b", "a"}, "c" });
		assertThat(k1.hashCode(), equalTo(k2.hashCode()));
		assertThat(k1.hashCode(), not(equalTo(k3.hashCode())));
		assertThat(k1, equalTo(k2));
		assertThat(k1, not(equalTo(k3)));
	}


	private Object generateKey(Object[] arguments) {
		return this.generator.generate(null, null, arguments);
	}

}
