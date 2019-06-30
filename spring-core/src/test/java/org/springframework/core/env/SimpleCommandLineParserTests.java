

package org.springframework.core.env;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class SimpleCommandLineParserTests {

	@Test
	public void withNoOptions() {
		SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
		assertThat(parser.parse().getOptionValues("foo"), nullValue());
	}

	@Test
	public void withSingleOptionAndNoValue() {
		SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
		CommandLineArgs args = parser.parse("--o1");
		assertThat(args.containsOption("o1"), is(true));
		assertThat(args.getOptionValues("o1"), equalTo(Collections.EMPTY_LIST));
	}

	@Test
	public void withSingleOptionAndValue() {
		SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
		CommandLineArgs args = parser.parse("--o1=v1");
		assertThat(args.containsOption("o1"), is(true));
		assertThat(args.getOptionValues("o1").get(0), equalTo("v1"));
	}

	@Test
	public void withMixOfOptionsHavingValueAndOptionsHavingNoValue() {
		SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
		CommandLineArgs args = parser.parse("--o1=v1", "--o2");
		assertThat(args.containsOption("o1"), is(true));
		assertThat(args.containsOption("o2"), is(true));
		assertThat(args.containsOption("o3"), is(false));
		assertThat(args.getOptionValues("o1").get(0), equalTo("v1"));
		assertThat(args.getOptionValues("o2"), equalTo(Collections.EMPTY_LIST));
		assertThat(args.getOptionValues("o3"), nullValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void withEmptyOptionText() {
		SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
		parser.parse("--");
	}

	@Test(expected = IllegalArgumentException.class)
	public void withEmptyOptionName() {
		SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
		parser.parse("--=v1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void withEmptyOptionValue() {
		SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
		parser.parse("--o1=");
	}

	@Test(expected = IllegalArgumentException.class)
	public void withEmptyOptionNameAndEmptyOptionValue() {
		SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
		parser.parse("--=");
	}

	@Test
	public void withNonOptionArguments() {
		SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
		CommandLineArgs args = parser.parse("--o1=v1", "noa1", "--o2=v2", "noa2");
		assertThat(args.getOptionValues("o1").get(0), equalTo("v1"));
		assertThat(args.getOptionValues("o2").get(0), equalTo("v2"));

		List<String> nonOptions = args.getNonOptionArgs();
		assertThat(nonOptions.get(0), equalTo("noa1"));
		assertThat(nonOptions.get(1), equalTo("noa2"));
		assertThat(nonOptions.size(), equalTo(2));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void assertOptionNamesIsUnmodifiable() {
		CommandLineArgs args = new SimpleCommandLineArgsParser().parse();
		args.getOptionNames().add("bogus");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void assertNonOptionArgsIsUnmodifiable() {
		CommandLineArgs args = new SimpleCommandLineArgsParser().parse();
		args.getNonOptionArgs().add("foo");
	}

}
