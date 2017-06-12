package dev.kkorolyov.pancake.system;

import java.util.HashSet;
import java.util.Set;

import dev.kkorolyov.pancake.entity.Entity;
import dev.kkorolyov.pancake.GameSystem;
import dev.kkorolyov.pancake.entity.Signature;
import dev.kkorolyov.pancake.component.Chain;
import dev.kkorolyov.pancake.component.Sprite;
import dev.kkorolyov.pancake.component.Transform;
import dev.kkorolyov.pancake.math.Vector;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Renders all game entities.
 */
public class RenderSystem extends GameSystem {
	private static final Signature cameraSignature = new Signature(Chain.class);

	private final Canvas canvas;
	private final GraphicsContext g;

	private final float unitPixels;
	private final Vector camera;
	private final Vector drawPosition = new Vector();

	private final Set<Sprite> tickedSprites = new HashSet<>();

	/**
	 * Constructs a new render system.
	 * @param canvas canvas on which to render
	 * @param unitPixels number of pixels within 1 unit distance
	 * @param camera viewport center
	 */
	public RenderSystem(Canvas canvas, float unitPixels, Vector camera) {
		super(new Signature(Transform.class,
												Sprite.class));

		this.canvas = canvas;
		g = canvas.getGraphicsContext2D();

		this.unitPixels = unitPixels;
		this.camera = camera;
	}

	@Override
	public void before(float dt) {
		tickedSprites.clear();
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		// TODO Sort sprites by z-index (lesser rendered first)
	}

	@Override
	public void update(Entity entity, float dt) {
		Transform transform = entity.get(Transform.class);
		Sprite sprite = entity.get(Sprite.class);

		if (!tickedSprites.contains(sprite)) {
			sprite.tick(dt);
			tickedSprites.add(sprite);
		}
		drawPosition.set(transform.getPosition());	// Raw position
		drawPosition.sub(camera); // Position relative to camera

		drawPosition.scale(unitPixels);	// Scale to pixels

		drawPosition.translate((float) canvas.getWidth() / 2, (float) canvas.getHeight() / 2);	// Position relative to display center
		drawPosition.sub(sprite.getSize(), .5f);	// Sprite top-left corner

		g.drawImage(sprite.getBaseImage(), sprite.getOrigin().getX(), sprite.getOrigin().getY(), sprite.getSize().getX(), sprite.getSize().getY(),
								drawPosition.getX(), drawPosition.getY(), sprite.getSize().getX(), sprite.getSize().getY());
	}

	@Override
	public void after(float dt) {
		g.strokeText("FPS: " + Math.round(1 / dt), 0, 10);
	}
}
