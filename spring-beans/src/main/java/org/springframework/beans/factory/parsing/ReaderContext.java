

package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/**
 * Context that gets passed along a bean definition reading process,encapsulating all relevant configuration as well as state.
 * @since 2.0
 */
public class ReaderContext {

	private final Resource resource;

	private final ProblemReporter problemReporter;

	private final ReaderEventListener eventListener;

	private final SourceExtractor sourceExtractor;

	/**
	 * Construct a new {@code ReaderContext}.
	 * @param resource the XML bean definition resource
	 * @param problemReporter the problem reporter in use
	 * @param eventListener the event listener in use
	 * @param sourceExtractor the source extractor in use
	 */
	public ReaderContext(Resource resource, ProblemReporter problemReporter,ReaderEventListener eventListener, SourceExtractor sourceExtractor) {
		this.resource = resource;
		this.problemReporter = problemReporter;
		this.eventListener = eventListener;
		this.sourceExtractor = sourceExtractor;
	}

	public final Resource getResource() {
		return resource;
	}

	// Errors and warnings

	// Raise a fatal error.
	public void fatal(String message, @Nullable Object source) {
		fatal(message, source, null, null);
	}

	// Raise a fatal error.
	public void fatal(String message, @Nullable Object source, @Nullable Throwable cause) {
		fatal(message, source, null, cause);
	}

	// Raise a fatal error.
	public void fatal(String message, @Nullable Object source, @Nullable ParseState parseState) {
		fatal(message, source, parseState, null);
	}

	// Raise a fatal error.
	public void fatal(String message, @Nullable Object source, @Nullable ParseState parseState, @Nullable Throwable cause) {
		Location location = new Location(getResource(), source);
		problemReporter.fatal(new Problem(message, location, parseState, cause));
	}

	// Raise a regular error.
	public void error(String message, @Nullable Object source) {
		error(message, source, null, null);
	}

	// Raise a regular error.
	public void error(String message, @Nullable Object source, @Nullable Throwable cause) {
		error(message, source, null, cause);
	}

	// Raise a regular error.
	public void error(String message, @Nullable Object source, @Nullable ParseState parseState) {
		error(message, source, parseState, null);
	}

	// Raise a regular error.
	public void error(String message, @Nullable Object source, @Nullable ParseState parseState, @Nullable Throwable cause) {
		Location location = new Location(getResource(), source);
		problemReporter.error(new Problem(message, location, parseState, cause));
	}

	// Raise a non-critical warning.
	public void warning(String message, @Nullable Object source) {
		warning(message, source, null, null);
	}

	// Raise a non-critical warning.
	public void warning(String message, @Nullable Object source, @Nullable Throwable cause) {
		warning(message, source, null, cause);
	}

	//  Raise a non-critical warning.
	public void warning(String message, @Nullable Object source, @Nullable ParseState parseState) {
		warning(message, source, parseState, null);
	}

	//  Raise a non-critical warning.
	public void warning(String message, @Nullable Object source, @Nullable ParseState parseState, @Nullable Throwable cause) {
		Location location = new Location(getResource(), source);
		problemReporter.warning(new Problem(message, location, parseState, cause));
	}

	// 【Explicit parse events】

	// Fire an defaults-registered event.
	public void fireDefaultsRegistered(DefaultsDefinition defaultsDefinition) {
		eventListener.defaultsRegistered(defaultsDefinition);
	}

	// Fire an component-registered event.
	public void fireComponentRegistered(ComponentDefinition componentDefinition) {
		eventListener.componentRegistered(componentDefinition);
	}

	// Fire an alias-registered event.
	public void fireAliasRegistered(String beanName, String alias, @Nullable Object source) {
		eventListener.aliasRegistered(new AliasDefinition(beanName, alias, source));
	}

	// Fire an import-processed event.
	public void fireImportProcessed(String importedResource, @Nullable Object source) {
		eventListener.importProcessed(new ImportDefinition(importedResource, source));
	}

	//  Fire an import-processed event.
	public void fireImportProcessed(String importedResource, Resource[] actualResources, @Nullable Object source) {
		eventListener.importProcessed(new ImportDefinition(importedResource, actualResources, source));
	}

	// 【Source extraction】

	// Return the source extractor in use.
	public SourceExtractor getSourceExtractor() {
		return sourceExtractor;
	}

	/**
	 * Call the source extractor for the given source object.
	 * @param sourceCandidate the original source object
	 * @return the source object to store, or {@code null} for none.
	 * @see #getSourceExtractor()
	 * @see SourceExtractor#extractSource
	 */
	@Nullable
	public Object extractSource(Object sourceCandidate) {
		return sourceExtractor.extractSource(sourceCandidate, resource);
	}
}
