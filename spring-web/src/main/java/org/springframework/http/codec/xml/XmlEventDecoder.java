

package org.springframework.http.codec.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

import com.fasterxml.aalto.AsyncByteBufferFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.evt.EventAllocatorImpl;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import org.reactivestreams.Publisher;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;

import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.xml.StaxUtils;

/**
 * Decodes a {@link DataBuffer} stream into a stream of {@link XMLEvent XMLEvents}.
 *
 * Given the following XML:
 *
 * <pre class="code">
 * &lt;root>
 *     &lt;child&gt;foo&lt;/child&gt;
 *     &lt;child&gt;bar&lt;/child&gt;
 * &lt;/root&gt;
 * </pre>
 *
 * this decoder will produce a {@link Flux} with the following events:
 *
 * <ol>
 * <li>{@link javax.xml.stream.events.StartDocument}</li>
 * <li>{@link javax.xml.stream.events.StartElement} {@code root}</li>
 * <li>{@link javax.xml.stream.events.StartElement} {@code child}</li>
 * <li>{@link javax.xml.stream.events.Characters} {@code foo}</li>
 * <li>{@link javax.xml.stream.events.EndElement} {@code child}</li>
 * <li>{@link javax.xml.stream.events.StartElement} {@code child}</li>
 * <li>{@link javax.xml.stream.events.Characters} {@code bar}</li>
 * <li>{@link javax.xml.stream.events.EndElement} {@code child}</li>
 * <li>{@link javax.xml.stream.events.EndElement} {@code root}</li>
 * </ol>
 *
 * Note that this decoder is not registered by default but is used internally
 * by other decoders which are registered by default.
 *
 * @author Arjen Poutsma
 * @since 5.0
 */
public class XmlEventDecoder extends AbstractDecoder<XMLEvent> {

	private static final XMLInputFactory inputFactory = StaxUtils.createDefensiveInputFactory();

	private static final boolean aaltoPresent = ClassUtils.isPresent(
			"com.fasterxml.aalto.AsyncXMLStreamReader", XmlEventDecoder.class.getClassLoader());

	boolean useAalto = aaltoPresent;


	public XmlEventDecoder() {
		super(MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML);
	}


	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})  // on JDK 9 where XMLEventReader is Iterator<Object>
	public Flux<XMLEvent> decode(Publisher<DataBuffer> input, ResolvableType elementType,
			@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {

		if (this.useAalto) {
			AaltoDataBufferToXmlEvent mapper = new AaltoDataBufferToXmlEvent();
			return Flux.from(input)
					.flatMapIterable(mapper)
					.doFinally(signalType -> mapper.endOfInput());
		}
		else {
			return DataBufferUtils.join(input).
					flatMapIterable(buffer -> {
						try {
							InputStream is = buffer.asInputStream();
							Iterator eventReader = inputFactory.createXMLEventReader(is);
							List<XMLEvent> result = new ArrayList<>();
							eventReader.forEachRemaining(event -> result.add((XMLEvent) event));
							return result;
						}
						catch (XMLStreamException ex) {
							throw Exceptions.propagate(ex);
						}
						finally {
							DataBufferUtils.release(buffer);
						}
					});
		}
	}


	/*
	 * Separate static class to isolate Aalto dependency.
	 */
	private static class AaltoDataBufferToXmlEvent implements Function<DataBuffer, List<? extends XMLEvent>> {

		private static final AsyncXMLInputFactory inputFactory =
				StaxUtils.createDefensiveInputFactory(InputFactoryImpl::new);

		private final AsyncXMLStreamReader<AsyncByteBufferFeeder> streamReader =
				inputFactory.createAsyncForByteBuffer();

		private final XMLEventAllocator eventAllocator = EventAllocatorImpl.getDefaultInstance();


		@Override
		public List<? extends XMLEvent> apply(DataBuffer dataBuffer) {
			try {
				this.streamReader.getInputFeeder().feedInput(dataBuffer.asByteBuffer());
				List<XMLEvent> events = new ArrayList<>();
				while (true) {
					if (this.streamReader.next() == AsyncXMLStreamReader.EVENT_INCOMPLETE) {
						// no more events with what currently has been fed to the reader
						break;
					}
					else {
						XMLEvent event = this.eventAllocator.allocate(this.streamReader);
						events.add(event);
						if (event.isEndDocument()) {
							break;
						}
					}
				}
				return events;
			}
			catch (XMLStreamException ex) {
				throw Exceptions.propagate(ex);
			}
			finally {
				DataBufferUtils.release(dataBuffer);
			}
		}

		public void endOfInput() {
			this.streamReader.getInputFeeder().endOfInput();
		}
	}

}
