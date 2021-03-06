package dev.kkorolyov.pancake.core.component.media;

import dev.kkorolyov.pancake.platform.entity.Component;
import dev.kkorolyov.pancake.platform.media.Animated;

/**
 * A swappable animation.
 */
public class Animation implements Component, Animated {
	private Animated delegate;

	/**
	 * Constructs a new animation.
	 * @param delegate delegate providing animation logic
	 */
	public Animation(Animated delegate) {
		setDelegate(delegate);
	}

	/** @return delegate providing animation logic */
	public Animated getDelegate() {
		return delegate;
	}
	/**
	 * @param delegate delegate providing animation logic
	 * @return {@code this}
	 */
	public Animation setDelegate(Animated delegate) {
		this.delegate = delegate;
		return this;
	}

	@Override
	public void tick(long dt) {
		delegate.tick(dt);
	}

	@Override
	public void toggle() {
		delegate.toggle();
	}

	@Override
	public boolean isActive() {
		return delegate.isActive();
	}
	@Override
	public void setActive(boolean active) {
		delegate.setActive(active);
	}

	@Override
	public int getFrame() {
		return delegate.getFrame();
	}
	@Override
	public void setFrame(int frame) {
		delegate.setFrame(frame);
	}

	@Override
	public long getFrameInterval() {
		return delegate.getFrameInterval();
	}
	@Override
	public void setFrameInterval(long frameInterval) {
		delegate.setFrameInterval(frameInterval);
	}

	@Override
	public int length() {
		return delegate.length();
	}
}
