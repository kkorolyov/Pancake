package dev.kkorolyov.pancake.core.registry;

import dev.kkorolyov.flopple.function.convert.Converter;
import dev.kkorolyov.pancake.core.action.ForceAction;
import dev.kkorolyov.pancake.core.action.TransformAction;
import dev.kkorolyov.pancake.platform.registry.Deferred;
import dev.kkorolyov.pancake.platform.registry.DeferredConverterFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static dev.kkorolyov.flopple.function.Memoizer.memoize;

/**
 * {@link DeferredConverterFactory.ActionStrat} for core actions.
 */
public class ActionStratDeferredConverterFactory implements DeferredConverterFactory.ActionStrat {
	private static final Collection<String> FORCE_KEYS = Set.of("force");
	private static final Collection<String> TRANSFORM_KEYS = Set.of("position", "rotation");

	private final Supplier<Converter<Object, Optional<Deferred<String, dev.kkorolyov.pancake.platform.math.Vector>>>> vectorReader = memoize(() -> DeferredConverterFactory.get(VectorStrat.class));

	private static final Pattern NUMBER_PATTERN = Pattern.compile("[+-]?(\\d*\\.)?\\d+([eE][+-]?\\d+)?");
	private static final Pattern VECTOR_PATTERN = Pattern.compile(String.format("%s(,\\s*%s)*", NUMBER_PATTERN, NUMBER_PATTERN));

	private Converter<Object, Optional<Deferred<String, dev.kkorolyov.pancake.platform.action.Action>>> force() {
		return Converter.selective(
				in -> in instanceof Map && ((Map<?, ?>) in).keySet().stream().allMatch(key -> FORCE_KEYS.contains(key.toString())),
				in -> Deferred.direct(new ForceAction(toVector(((Map<?, ?>) in).get("force"))))
		);
	}
	private Converter<Object, Optional<Deferred<String, dev.kkorolyov.pancake.platform.action.Action>>> transform() {
		return Converter.selective(
				in -> in instanceof Map && ((Map<?, ?>) in).keySet().stream().allMatch(key -> TRANSFORM_KEYS.contains(key.toString())),
				in -> Deferred.direct(new TransformAction(toVector(((Map<?, ?>) in).get("position")), toVector(((Map<?, ?>) in).get("rotation"))))
		);
	}

	private dev.kkorolyov.pancake.platform.math.Vector toVector(Object in) {
		return in == null ? null : vectorReader.get().convert(in)
				.map(Deferred::resolve)
				.orElseThrow(() -> new IllegalArgumentException("No resource reader matches: " + in));
	}

	@Override
	public Converter<Object, Optional<Deferred<String, dev.kkorolyov.pancake.platform.action.Action>>> get() {
		return Converter.reducing(
				force(),
				transform()
		);
	}
}
