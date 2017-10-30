package dev.kkorolyov.pancake.skillet.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A container of distinct components.
 */
public class GenericEntity extends Model<GenericEntity> implements Iterable<GenericComponent> {
	private String name;
	private final Map<String, GenericComponent> components = new LinkedHashMap<>();

	/**
	 * Constructs a new entity with an empty name and no components.
	 */
	public GenericEntity() {}
	/**
	 * Constructs a new entity with no components.
	 * @param name entity name
	 */
	public GenericEntity(String name) {
		this(name, Collections.emptyList());
	}
	/**
	 * Constructs a new entity.
	 * @param name entity name
	 * @param components entity components
	 */
	public GenericEntity(String name, Iterable<GenericComponent> components) {
		this.name = name;
		for (GenericComponent component : components) addComponent(component);
	}

	/**
	 * @param name name to search by
	 * @return {@code true} if this entity contains a component of name {@code name}
	 */
	public boolean containsComponent(String name) {
		return components.containsKey(name);
	}

	/**
	 * @param component added component
	 * @return {@code this}
	 */
	public GenericEntity addComponent(GenericComponent component) {
		components.put(component.getName(), component);

		changed(EntityChangeEvent.ADD);

		return this;
	}
	/**
	 * @param components components to add
	 * @return {@code this}
	 */
	public GenericEntity addAll(Iterable<GenericComponent> components) {
		components.forEach(this::addComponent);
		return this;
	}

	/**
	 * @param name removed component name
	 * @return removed component, or {@code null} if no such component
	 */
	public GenericComponent removeComponent(String name) {
		GenericComponent oldComponent = components.remove(name);

		changed(EntityChangeEvent.REMOVE);

		return oldComponent;
	}

	/** @return entity name */
	public String getName() {
		return name;
	}
	/** @param name entity name */
	public void setName(String name) {
		this.name = name;

		changed(EntityChangeEvent.NAME);
	}

	/** @return	entity components */
	public Collection<GenericComponent> getComponents() {
		return components.values();
	}

	@Override
	public Iterator<GenericComponent> iterator() {
		return getComponents().iterator();
	}

	/**
	 * A change to an entity.
	 */
	public enum EntityChangeEvent implements ModelChangeEvent {
		NAME,
		ADD,
		REMOVE
	}
}