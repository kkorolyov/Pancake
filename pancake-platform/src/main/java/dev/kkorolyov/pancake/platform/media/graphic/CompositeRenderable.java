package dev.kkorolyov.pancake.platform.media.graphic;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static dev.kkorolyov.simplefuncs.stream.Iterables.append;

/**
 * A collection of renderable artifacts rendered as a unit.
 * @param <T> aggregated renderable type
 */
public final class CompositeRenderable<T extends Renderable> implements Renderable, Iterable<T> {
	private final Collection<T> delegates = new LinkedHashSet<>();

	/** @see #CompositeRenderable(Iterable) */
	@SafeVarargs
	public CompositeRenderable(T delegate, T... delegates) {
		this(append(delegate, delegates));
	}
	/**
	 * Constructs a new composite renderable.
	 * @param delegates delegates to render in order
	 */
	public CompositeRenderable(Iterable<T> delegates) {
		delegates.forEach(this.delegates::add);
	}

	@Override
	public void render(RenderTransform transform) {
		for (T delegate : this) {
			delegate.render(transform);
		}
	}

	@Override
	public Iterator<T> iterator() {
		return delegates.iterator();
	}
}