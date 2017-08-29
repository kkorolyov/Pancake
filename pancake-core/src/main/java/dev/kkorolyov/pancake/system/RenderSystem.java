package dev.kkorolyov.pancake.system;

import dev.kkorolyov.pancake.Config;
import dev.kkorolyov.pancake.GameSystem;
import dev.kkorolyov.pancake.PerformanceCounter;
import dev.kkorolyov.pancake.PerformanceCounter.Usage;
import dev.kkorolyov.pancake.component.Sprite;
import dev.kkorolyov.pancake.component.Transform;
import dev.kkorolyov.pancake.entity.Entity;
import dev.kkorolyov.pancake.entity.Signature;
import dev.kkorolyov.pancake.graphics.Camera;
import dev.kkorolyov.pancake.math.Vector;
import dev.kkorolyov.simplelogs.Logger;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Rotate;
import java.util.HashSet;
import java.util.Set;

/**
 * Renders all game entities.
 */
public class RenderSystem extends GameSystem {
	private static final int LINE_HEIGHT = 14;
	private static final Logger log = Config.getLogger(RenderSystem.class);

	private final Canvas canvas;
	private final GraphicsContext g;

	private final Camera camera;
	private final Rotate rotate = new Rotate();

	private final Set<Sprite> tickedSprites = new HashSet<>();

	private float last;

	/**
	 * Constructs a new render system.
	 * @param canvas canvas on which to render
	 * @param camera view of render space
	 */
	public RenderSystem(Canvas canvas, Camera camera) {
		super(new Signature(Transform.class,
												Sprite.class),
					(e1, e2) -> e1.get(Transform.class).getPosition().getZ() < e2.get(Transform.class).getPosition().getZ() ? -1 : 1);

		this.canvas = canvas;
		g = canvas.getGraphicsContext2D();

		this.camera = camera;
	}

	@Override
	public void before(float dt) {
		tickedSprites.clear();

		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	@Override
	public void update(Entity entity, float dt) {
		Transform transform = entity.get(Transform.class);
		Sprite sprite = entity.get(Sprite.class);

		if (!tickedSprites.contains(sprite)) {
			sprite.update(dt);
			tickedSprites.add(sprite);
		}
		draw(transform, sprite);
	}

	@Override
	public void after(float dt) {
		rotate(0, null);

		drawDebug(dt);
	}

	private void draw(Transform transform, Sprite sprite) {
		Vector drawPosition = camera.getRelativePosition(transform.getPosition());

		rotate(transform.getRotation(), drawPosition);	// Rotate around transform origin
		drawPosition.sub(sprite.getSize(), .5f);	// Sprite top-left corner

		for (Image image : sprite.getImage()) {
			g.drawImage(image, sprite.getOrigin().getX(), sprite.getOrigin().getY(), sprite.getSize().getX(), sprite.getSize().getY(),
									drawPosition.getX(), drawPosition.getY(), sprite.getSize().getX(), sprite.getSize().getY());
		}
	}

	private void drawDebug(float dt) {
		String[] args = Config.config.getArray("renderInfo");
		if (args == null) return;

		double y = 0;

		for (String arg : args) {
			switch (arg) {
				case "fps":
					float now = System.nanoTime();
					g.strokeText("FPS: " + Math.round(1000000000 / (now - last)), 0, y += LINE_HEIGHT);
					last = now;
					break;
				case "usage":
					for (Usage usage : PerformanceCounter.usages()) {
						Paint previous = g.getStroke();

						if (usage.exceedsMax()) g.setStroke(Color.DARKRED);
						g.strokeText(usage.toString(), 0, y += LINE_HEIGHT);
						g.setStroke(previous);
					}
					break;
				default:
					log.warning("Unknown renderInfo arg: {}", arg);
			}
		}
	}

	private void rotate(float angle, Vector pivot) {
		rotate.setAngle(angle);

		if (pivot != null) {
			rotate.setPivotX(pivot.getX());
			rotate.setPivotY(pivot.getY());
		}
		g.setTransform(rotate.getMxx(), rotate.getMyx(),
									 rotate.getMxy(), rotate.getMyy(),
									 rotate.getTx(), rotate.getTy());
	}
}
