package dev.kkorolyov.pancake.platform.action;

import dev.kkorolyov.pancake.platform.entity.Entity;

import java.util.Objects;

/**
 * An {@link Action} which applies a different action depending on its current state.
 */
public class MultiStageAction implements Action {
	private final Action start, hold, end;
	private final long holdThreshold;
	private long holdTime;
	private ArmingOption armingOption;
	private State state = State.INACTIVE;

	/**
	 * Constructs a new multi-stage action.
	 * @param start action applied when this action is "activated"
	 * @param hold action applied the first time this action is signalled after it has been "activated"
	 * @param end action applied when this action is "deactivated"
	 * @param holdThreshold minimum number of {@code ns} this action must remain in the "active" state before moving on to the "decayed" state
	 */
	public MultiStageAction(
			Action start,
			Action hold,
			Action end,
			long holdThreshold
	) {
		this.start = start;
		this.hold = hold;
		this.end = end;
		this.holdThreshold = holdThreshold;
	}

	/**
	 * Arms this action to move to the next state in its state sequence, according to the given arming option.
	 * The next time {@link Action#apply(Entity)} is invoked on this action, its state will change according to the current arming option.
	 * @param armingOption option influencing the next state this action moves to after its next application
	 * @param dt {@code ns} elapsed since the last invocation of this method
	 * @return {@code this}
	 */
	public MultiStageAction arm(ArmingOption armingOption, long dt) {
		this.armingOption = armingOption;
		holdTime += dt;
		return this;
	}

	/**
	 * Applies this action to an entity if it is currently armed.
	 * If this action is not armed, does nothing.
	 * Disarms after application.
	 */
	@Override
	public void apply(Entity entity) {
		if (armingOption != null) state.apply(entity, this);

		armingOption = null;
	}

	/** @return action applied on activation, or {@code null} */
	public Action getStart() {
		return start;
	}
	/** @return action applied on first post-activation signal, or {@code null} */
	public Action getHold() {
		return hold;
	}
	/** @return action applied on deactivation, or {@code null} */
	public Action getEnd() {
		return end;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		MultiStageAction o = (MultiStageAction) obj;
		return holdThreshold == o.holdThreshold &&
				Objects.equals(start, o.start) &&
				Objects.equals(hold, o.hold) &&
				Objects.equals(end, o.end);
	}
	@Override
	public int hashCode() {
		return Objects.hash(start, hold, end, holdThreshold);
	}

	/**
	 * Arms a {@link MultiStageAction} to change state in a particular way next time it is applied.
	 */
	public enum ArmingOption {
		ACTIVATE,
		DEACTIVATE
	}

	private static abstract class State {
		static final State INACTIVE = new State() {
			@Override
			void apply(Entity entity, MultiStageAction client) {
				switch (client.armingOption) {
					case ACTIVATE:
						invokeNullable(client.start, entity);

						client.holdTime = 0;
						client.state = ACTIVE;
						break;
				}
			}
		};
		static final State ACTIVE = new State() {
			@Override
			void apply(Entity entity, MultiStageAction client) {
				switch (client.armingOption) {
					case ACTIVATE:
						if (client.holdTime >= client.holdThreshold) {
							invokeNullable(client.hold, entity);

							client.holdTime = 0;
							client.state = DECAYED;
						}
						break;
					case DEACTIVATE:
						invokeNullable(client.end, entity);

						client.holdTime = 0;
						client.state = INACTIVE;
						break;
				}
			}
		};
		static final State DECAYED = new State() {
			@Override
			void apply(Entity entity, MultiStageAction client) {
				switch (client.armingOption) {
					case DEACTIVATE:
						invokeNullable(client.end, entity);

						client.holdTime = 0;
						client.state = INACTIVE;
						break;
				}
			}
		};

		abstract void apply(Entity entity, MultiStageAction client);

		void invokeNullable(Action action, Entity entity) {
			if (action != null) action.apply(entity);
		}
	}
}
