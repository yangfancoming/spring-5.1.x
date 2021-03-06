

package org.springframework.web.reactive.function.client;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.publisher.Mono;

import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.util.Assert;

/**
 * Static factory methods to create an {@link ExchangeFunction}.
 *
 * @author Arjen Poutsma
 *
 * @since 5.0
 */
public abstract class ExchangeFunctions {

	private static final Log logger = LogFactory.getLog(ExchangeFunctions.class);


	/**
	 * Create an {@code ExchangeFunction} with the given {@code ClientHttpConnector}.
	 * This is the same as calling
	 * {@link #create(ClientHttpConnector, ExchangeStrategies)} and passing
	 * {@link ExchangeStrategies#withDefaults()}.
	 * @param connector the connector to use for connecting to servers
	 * @return the created {@code ExchangeFunction}
	 */
	public static ExchangeFunction create(ClientHttpConnector connector) {
		return create(connector, ExchangeStrategies.withDefaults());
	}

	/**
	 * Create an {@code ExchangeFunction} with the given
	 * {@code ClientHttpConnector} and {@code ExchangeStrategies}.
	 * @param connector the connector to use for connecting to servers
	 * @param strategies the {@code ExchangeStrategies} to use
	 * @return the created {@code ExchangeFunction}
	 */
	public static ExchangeFunction create(ClientHttpConnector connector, ExchangeStrategies strategies) {
		return new DefaultExchangeFunction(connector, strategies);
	}


	private static class DefaultExchangeFunction implements ExchangeFunction {

		private final ClientHttpConnector connector;

		private final ExchangeStrategies strategies;

		private boolean enableLoggingRequestDetails;


		public DefaultExchangeFunction(ClientHttpConnector connector, ExchangeStrategies strategies) {
			Assert.notNull(connector, "ClientHttpConnector must not be null");
			Assert.notNull(strategies, "ExchangeStrategies must not be null");
			this.connector = connector;
			this.strategies = strategies;

			strategies.messageWriters().stream()
					.filter(LoggingCodecSupport.class::isInstance)
					.forEach(reader -> {
						if (((LoggingCodecSupport) reader).isEnableLoggingRequestDetails()) {
							this.enableLoggingRequestDetails = true;
						}
					});
		}


		@Override
		public Mono<ClientResponse> exchange(ClientRequest clientRequest) {
			Assert.notNull(clientRequest, "ClientRequest must not be null");
			HttpMethod httpMethod = clientRequest.method();
			URI url = clientRequest.url();
			String logPrefix = clientRequest.logPrefix();

			return this.connector
					.connect(httpMethod, url, httpRequest -> clientRequest.writeTo(httpRequest, this.strategies))
					.doOnRequest(n -> logRequest(clientRequest))
					.doOnCancel(() -> logger.debug(logPrefix + "Cancel signal (to close connection)"))
					.map(httpResponse -> {
						logResponse(httpResponse, logPrefix);
						return new DefaultClientResponse(httpResponse, this.strategies, logPrefix);
					});
		}

		private void logRequest(ClientRequest request) {
			LogFormatUtils.traceDebug(logger, traceOn ->
					request.logPrefix() + "HTTP " + request.method() + " " + request.url() +
							(traceOn ? ", headers=" + formatHeaders(request.headers()) : "")
			);
		}

		private void logResponse(ClientHttpResponse response, String logPrefix) {
			LogFormatUtils.traceDebug(logger, traceOn -> {
				int code = response.getRawStatusCode();
				HttpStatus status = HttpStatus.resolve(code);
				return logPrefix + "Response " + (status != null ? status : code) +
						(traceOn ? ", headers=" + formatHeaders(response.getHeaders()) : "");
			});
		}

		private String formatHeaders(HttpHeaders headers) {
			return this.enableLoggingRequestDetails ? headers.toString() : headers.isEmpty() ? "{}" : "{masked}";
		}
	}

}
