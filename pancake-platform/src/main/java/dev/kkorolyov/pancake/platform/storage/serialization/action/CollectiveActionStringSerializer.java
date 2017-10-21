package dev.kkorolyov.pancake.platform.storage.serialization.action;

import dev.kkorolyov.pancake.platform.action.Action;
import dev.kkorolyov.pancake.platform.action.ActionRegistry;
import dev.kkorolyov.pancake.platform.action.CollectiveAction;
import dev.kkorolyov.pancake.platform.storage.serialization.AutoContextualSerializer;
import dev.kkorolyov.pancake.platform.storage.serialization.ContextualSerializer;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Serializes collective actions.
 */
public class CollectiveActionStringSerializer extends ActionSerializer<CollectiveAction> {
	private static final ContextualSerializer<Action, String, ActionRegistry> autoSerializer = new AutoContextualSerializer(ActionSerializer.class);

	/**
	 * Constructs a new collective action serializer
	 */
	public CollectiveActionStringSerializer() {
		super("\\{.+(,\\s?.+)+}");
	}

	@Override
	public CollectiveAction read(String out, ActionRegistry context) {
		return new CollectiveAction(Arrays.stream(out.substring(1, out.length() - 1).split(",\\s?"))
				.map(s -> autoSerializer.read(s, context))
				.collect(Collectors.toList()));
	}
	@Override
	public String write(CollectiveAction in, ActionRegistry context) {
		// TODO
		return null;
	}
}
