package dev.kkorolyov.pancake.skillet.ui.entity;

import dev.kkorolyov.pancake.muffin.data.DataChangeListener;
import dev.kkorolyov.pancake.muffin.data.DataObservable.DataChangeEvent;
import dev.kkorolyov.pancake.muffin.data.type.Entity;
import dev.kkorolyov.pancake.muffin.data.type.Entity.EntityChangeEvent;
import dev.kkorolyov.pancake.skillet.ui.Panel;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import static dev.kkorolyov.pancake.skillet.utility.decorator.UIDecorator.decorate;

/**
 * Editable label displaying an entity name.
 */
public class EntityLabel implements Panel, DataChangeListener<Entity> {
	private final Label label = new Label();
	private final TextField textField = new TextField();
	private final VBox root = new VBox();

	/**
	 * Constructs a new entity tab.
	 * @param entity associated entity
	 */
	public EntityLabel(Entity entity) {
		decorate(label)
				.styleClass("entity-name")
				.minSize(10.0, null)
				.click(() -> {
					swapTo(textField);
					textField.requestFocus();
				}, 2);

		decorate(textField)
				.change(Node::focusedProperty,
						(target, oldValue, newValue) -> {
							if (!newValue) swapTo(label);
						})
				.change(TextInputControl::textProperty,
						(target, oldValue, newValue) -> entity.setName(newValue))
				.press(() -> swapTo(label), KeyCode.ENTER, KeyCode.ESCAPE);

		changed(entity, EntityChangeEvent.NAME);  // Force a text change
		swapTo(label);

		entity.register(this);
	}
	private void swapTo(Node node) {
		root.getChildren().clear();
		root.getChildren().add(node);
	}

	@Override
	public Node getRoot() {
		return root;
	}

	@Override
	public void changed(Entity target, DataChangeEvent event) {
		if (EntityChangeEvent.NAME == event) {
			label.setText(target.getName());
			textField.setText(target.getName());
		}
	}
}
