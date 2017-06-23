package dev.kkorolyov.pancake;

import java.util.Comparator;

import dev.kkorolyov.pancake.entity.Component;
import dev.kkorolyov.pancake.entity.Entity;
import dev.kkorolyov.pancake.entity.EntityPool;
import dev.kkorolyov.pancake.entity.Signature;
import dev.kkorolyov.pancake.event.EventBroadcaster;
import dev.kkorolyov.pancake.event.Receiver;

/**
 * Performs work on entities matching a certain component signature.
 */
public abstract class GameSystem {
	private final Signature signature;
	private final Comparator<Entity> comparator;
	private EventBroadcaster events;
	private EntityPool entities;

	/**
	 * Constructs a new system with arbitrary entity order.
	 * @param signature defines all components an entity must have to be affected by this system
	 */
	public GameSystem(Signature signature) {
		this(signature, null);
	}
	/**
	 * Constructs a new system.
	 * @param signature defines all components an entity must have to be affected by this system
	 * @param comparator defines the order in which entities are supplied to this system
	 */
	public GameSystem(Signature signature, Comparator<Entity> comparator) {
		this.signature = signature;
		this.comparator = comparator;
	}

	/**
	 * Invoked when this system is attached to a {@link GameEngine}.
	 */
	public void attach() {}
	/**
	 * Invoked when this system is detached from a {@link GameEngine}.
	 */
	public void detach() {}

	/**
	 * Function invoked on each entity affected by this system.
	 * @param dt seconds elapsed since last update
	 */
	public abstract void update(Entity entity, float dt);

	/**
	 * Function invoked at the beginning of an update cycle.
	 * @param dt seconds elapsed since last update
	 */
	public void before(float dt) {}
	/**
	 * Function invoked at the end of an update cycle.
	 * @param dt seconds elapsed since last update
	 */
	public void after(float dt) {}

	/**
	 * Registers to receive broadcasts of an event.
	 * @param event event identifier
	 * @param receiver action invoked on event reception
	 */
	public void register(String event, Receiver receiver) {
		events.register(event, receiver);
	}
	/**
	 * Removes a receiver from a set of registered receivers
	 * @param event event identifier
	 * @param receiver removed receiver
	 * @return {@code true} if {@code receiver} was present and removed
	 */
	public boolean unregister(String event, Receiver receiver) {
		return events.unregister(event, receiver);
	}

	/**
	 * Queues an event.
	 * @param event event identifier
	 * @param target entity affected by event, or {@code null} if not applicable
	 */
	public void enqueue(String event, Entity target) {
		events.enqueue(event, target);
	}

	/**
	 * Adds a new entity to the attached entity pool.
	 * @param components components composing entity
	 * @return created entity
	 */
	public Entity create(Component... components) {
		return entities.create(components);
	}
	/**
	 * Adds a new entity to the attached entity pool.
	 * @param components components composing entity
	 * @return created entity
	 */
	public Entity create(Iterable<Component> components) {
		return entities.create(components);
	}

	/**
	 * Removes an entity from the attached entity pool
	 * @param entity entity to remove
	 * @return {@code true} if the attached entity pool contained {@code entity} and it was removed
	 */
	public boolean destroy(Entity entity) {
		return entities.destroy(entity);
	}

	/** @return component signature */
	public Signature getSignature() {
		return signature;
	}
	/** @return required entity order */
	public Comparator<Entity> getComparator() {
		return comparator;
	}

	/** @param events event queue and broadcaster accessible to this system */
	void setEvents(EventBroadcaster events) {
		this.events = events;
	}
	/** @param entities entity pool accessible to this system */
	void setEntities(EntityPool entities) {
		this.entities = entities;
	}
}
