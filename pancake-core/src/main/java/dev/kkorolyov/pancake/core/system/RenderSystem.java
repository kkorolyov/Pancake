package dev.kkorolyov.pancake.core.system;

import dev.kkorolyov.pancake.core.component.Transform;
import dev.kkorolyov.pancake.core.component.media.Graphic;
import dev.kkorolyov.pancake.platform.GameSystem;
import dev.kkorolyov.pancake.platform.Resources;
import dev.kkorolyov.pancake.platform.entity.Entity;
import dev.kkorolyov.pancake.platform.entity.Signature;
import dev.kkorolyov.pancake.platform.media.graphic.RenderTransform;
import dev.kkorolyov.pancake.platform.utility.Limiter;

import java.util.Collection;
import java.util.HashSet;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import static java.util.stream.Collectors.toList;

/**
 * Renders all game entities.
 */
public class RenderSystem extends GameSystem {
	private final RenderTransform renderTransform = new RenderTransform();

	private final NavigableMap<Double, Set<Entity>> drawBuckets = new TreeMap<>();

	/**
	 * Constructs a new render system.
	 */
	public RenderSystem() {
		super(
				new Signature(Transform.class, Graphic.class),
				Limiter.fromConfig(RenderSystem.class)
		);
	}

	@Override
	public void before(long dt) {
		for (Collection<Entity> bucket : drawBuckets.values()) bucket.clear();
	}

	@Override
	public void update(Entity entity, long dt) {
		drawBuckets.computeIfAbsent(entity.get(Transform.class).getGlobalPosition().getZ(), k -> new HashSet<>())
				.add(entity);
	}

	@Override
	public void after(long dt) {
		Collection<Entity> toDraw = drawBuckets.values().stream()
				.flatMap(Collection::stream)
				.collect(toList());

		Resources.RENDER_MEDIUM.clearInvoke(() -> {
			for (Entity entity : toDraw) {
				draw(
						entity.get(Transform.class),
						entity.get(Graphic.class)
				);
			}
		});
	}
	private void draw(Transform transform, Graphic graphic) {
		graphic.render(
				renderTransform
						.reset()
						.setPosition(Resources.RENDER_MEDIUM.getCamera().getRelativePosition(transform.getGlobalPosition()))
						.setRotation(transform.getGlobalOrientation())
		);
	}
}
