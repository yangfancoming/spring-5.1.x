

package org.springframework.http.codec.multipart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * {@code HttpMessageReader} for reading {@code "multipart/form-data"} requests
 * into a {@code MultiValueMap<String, Part>}.
 *
 * Note that this reader depends on access to an
 * {@code HttpMessageReader<Part>} for the actual parsing of multipart content.
 * The purpose of this reader is to collect the parts into a map.
 *
 *
 * @since 5.0
 */
public class MultipartHttpMessageReader extends LoggingCodecSupport
		implements HttpMessageReader<MultiValueMap<String, Part>> {

	private static final ResolvableType MULTIPART_VALUE_TYPE = ResolvableType.forClassWithGenerics(
			MultiValueMap.class, String.class, Part.class);


	private final HttpMessageReader<Part> partReader;


	public MultipartHttpMessageReader(HttpMessageReader<Part> partReader) {
		Assert.notNull(partReader, "'partReader' is required");
		this.partReader = partReader;
	}


	@Override
	public List<MediaType> getReadableMediaTypes() {
		return Collections.singletonList(MediaType.MULTIPART_FORM_DATA);
	}

	@Override
	public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
		return MULTIPART_VALUE_TYPE.isAssignableFrom(elementType) &&
				(mediaType == null || MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mediaType));
	}


	@Override
	public Flux<MultiValueMap<String, Part>> read(ResolvableType elementType,
			ReactiveHttpInputMessage message, Map<String, Object> hints) {

		return Flux.from(readMono(elementType, message, hints));
	}


	@Override
	public Mono<MultiValueMap<String, Part>> readMono(ResolvableType elementType,
			ReactiveHttpInputMessage inputMessage, Map<String, Object> hints) {

		Map<String, Object> allHints = Hints.merge(hints, Hints.SUPPRESS_LOGGING_HINT, true);

		return this.partReader.read(elementType, inputMessage, allHints)
				.collectMultimap(Part::name)
				.doOnNext(map ->
					LogFormatUtils.traceDebug(logger, traceOn -> Hints.getLogPrefix(hints) + "Parsed " +
							(isEnableLoggingRequestDetails() ?
									LogFormatUtils.formatValue(map, !traceOn) :
									"parts " + map.keySet() + " (content masked)")))
				.map(this::toMultiValueMap);
	}

	private LinkedMultiValueMap<String, Part> toMultiValueMap(Map<String, Collection<Part>> map) {
		return new LinkedMultiValueMap<>(map.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> toList(e.getValue()))));
	}

	private List<Part> toList(Collection<Part> collection) {
		return collection instanceof List ? (List<Part>) collection : new ArrayList<>(collection);
	}

}
