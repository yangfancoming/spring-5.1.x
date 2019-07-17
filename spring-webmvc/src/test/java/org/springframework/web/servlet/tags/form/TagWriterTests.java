

package org.springframework.web.servlet.tags.form;

import java.io.StringWriter;

import org.junit.Test;

import static org.junit.Assert.*;


public class TagWriterTests {

	private final StringWriter data = new StringWriter();

	private final TagWriter writer = new TagWriter(this.data);


	@Test
	public void simpleTag() throws Exception {
		this.writer.startTag("br");
		this.writer.endTag();

		assertEquals("<br/>", this.data.toString());
	}

	@Test
	public void emptyTag() throws Exception {
		this.writer.startTag("input");
		this.writer.writeAttribute("type", "text");
		this.writer.endTag();

		assertEquals("<input type=\"text\"/>", this.data.toString());
	}

	@Test
	public void simpleBlockTag() throws Exception {
		this.writer.startTag("textarea");
		this.writer.appendValue("foobar");
		this.writer.endTag();

		assertEquals("<textarea>foobar</textarea>", this.data.toString());
	}

	@Test
	public void blockTagWithAttributes() throws Exception {
		this.writer.startTag("textarea");
		this.writer.writeAttribute("width", "10");
		this.writer.writeAttribute("height", "20");
		this.writer.appendValue("foobar");
		this.writer.endTag();

		assertEquals("<textarea width=\"10\" height=\"20\">foobar</textarea>", this.data.toString());
	}

	@Test
	public void nestedTags() throws Exception {
		this.writer.startTag("span");
		this.writer.writeAttribute("style", "foo");
		this.writer.startTag("strong");
		this.writer.appendValue("Rob Harrop");
		this.writer.endTag();
		this.writer.endTag();

		assertEquals("<span style=\"foo\"><strong>Rob Harrop</strong></span>", this.data.toString());
	}

	@Test
	public void multipleNestedTags() throws Exception {
		this.writer.startTag("span");
		this.writer.writeAttribute("class", "highlight");
		{
			this.writer.startTag("strong");
			this.writer.appendValue("Rob");
			this.writer.endTag();
		}
		this.writer.appendValue(" ");
		{
			this.writer.startTag("emphasis");
			this.writer.appendValue("Harrop");
			this.writer.endTag();
		}
		this.writer.endTag();

		assertEquals("<span class=\"highlight\"><strong>Rob</strong> <emphasis>Harrop</emphasis></span>",
				this.data.toString());
	}

	@Test
	public void writeInterleavedWithForceBlock() throws Exception {
		this.writer.startTag("span");
		this.writer.forceBlock();
		this.data.write("Rob Harrop"); // interleaved writing
		this.writer.endTag();

		assertEquals("<span>Rob Harrop</span>", this.data.toString());
	}

	@Test
	public void appendingValue() throws Exception {
		this.writer.startTag("span");
		this.writer.appendValue("Rob ");
		this.writer.appendValue("Harrop");
		this.writer.endTag();

		assertEquals("<span>Rob Harrop</span>", this.data.toString());
	}

}
