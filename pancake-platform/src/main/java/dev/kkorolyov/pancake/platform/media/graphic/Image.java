package dev.kkorolyov.pancake.platform.media.graphic;

import dev.kkorolyov.pancake.platform.math.Vector;

/**
 * A renderable static image.
 */
public abstract class Image implements Renderable {
	private final Vector size = new Vector();

	/** @return mutable size vector */
	public Vector getSize() {
		return size;
	}
}
