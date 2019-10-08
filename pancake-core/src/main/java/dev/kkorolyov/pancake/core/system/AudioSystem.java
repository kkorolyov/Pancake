package dev.kkorolyov.pancake.core.system;

import dev.kkorolyov.pancake.core.component.AudioEmitter;
import dev.kkorolyov.pancake.core.component.Transform;
import dev.kkorolyov.pancake.platform.Config;
import dev.kkorolyov.pancake.platform.GameSystem;
import dev.kkorolyov.pancake.platform.entity.Entity;
import dev.kkorolyov.pancake.platform.entity.Signature;
import dev.kkorolyov.pancake.platform.event.CameraCreated;
import dev.kkorolyov.pancake.platform.math.Vector;
import dev.kkorolyov.pancake.platform.media.Camera;
import dev.kkorolyov.pancake.platform.utility.Limiter;

/**
 * Starts and stops audio clips.
 */
public class AudioSystem extends GameSystem {
	private Camera camera;
	private final Vector relativeEmitter = new Vector();

	/**
	 * Constructs a new sound system.
	 */
	public AudioSystem() {
		super(
				new Signature(AudioEmitter.class, Transform.class),
				Limiter.fromConfig(AudioSystem.class)
		);
	}

	@Override
	public void attach() {
		resources.events.register(CameraCreated.class, e -> camera = e.getCamera());
	}

	@Override
	public void update(Entity entity, long dt) {
		AudioEmitter emitter = entity.get(AudioEmitter.class);
		Vector position = entity.get(Transform.class).getGlobalPosition();

		relativeEmitter
				.set(position)
				.sub(camera.getPosition());

		double centralRadius = Double.parseDouble(Config.config(getClass()).get("centralRadius"));

		emitter.apply(
				centralRadius / relativeEmitter.getMagnitude(),
				relativeEmitter.getX() / centralRadius
		);
	}
}