

package org.springframework.core.type;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Abstract base class for testing implementations of
 * {@link ClassMetadata#getMemberClassNames()}.

 * @since 3.1
 */
public abstract class AbstractClassMetadataMemberClassTests {

	public abstract ClassMetadata getClassMetadataFor(Class<?> clazz);

	@Test
	public void withNoMemberClasses() {
		ClassMetadata metadata = getClassMetadataFor(L0_a.class);
		String[] nestedClasses = metadata.getMemberClassNames();
		assertThat(nestedClasses, equalTo(new String[]{}));
	}

	public static class L0_a {
	}


	@Test
	public void withPublicMemberClasses() {
		ClassMetadata metadata = getClassMetadataFor(L0_b.class);
		String[] nestedClasses = metadata.getMemberClassNames();
		assertThat(nestedClasses, equalTo(new String[]{L0_b.L1.class.getName()}));
	}

	public static class L0_b {
		public static class L1 { }
	}


	@Test
	public void withNonPublicMemberClasses() {
		ClassMetadata metadata = getClassMetadataFor(L0_c.class);
		String[] nestedClasses = metadata.getMemberClassNames();
		assertThat(nestedClasses, equalTo(new String[]{L0_c.L1.class.getName()}));
	}

	public static class L0_c {
		private static class L1 { }
	}


	@Test
	public void againstMemberClass() {
		ClassMetadata metadata = getClassMetadataFor(L0_b.L1.class);
		String[] nestedClasses = metadata.getMemberClassNames();
		assertThat(nestedClasses, equalTo(new String[]{}));
	}
}
