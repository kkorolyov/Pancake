package dev.kkorolyov.pancake.skillet.ui.entity

import dev.kkorolyov.pancake.skillet.click
import dev.kkorolyov.pancake.skillet.model.GenericEntity
import dev.kkorolyov.pancake.skillet.model.GenericEntity.EntityChangeEvent
import dev.kkorolyov.pancake.skillet.press
import dev.kkorolyov.pancake.skillet.ui.Panel
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.layout.VBox

/**
 * Editable label displaying an entity name.
 * @param entity displayed entity
 */
class EntityLabel(entity: GenericEntity) : Panel {
	private val label: Label = Label(entity.name).apply {
		styleClass += "entity-name"
		minWidth = 10.0
		click(2) {
			textField.text = entity.name
			swapTo(textField)
		}
	}
	private val textField: TextField = TextField().apply {
		focusedProperty().addListener {_, _, newValue ->
			if (!newValue) swapTo(label)
		}
		press(mapOf(
				KeyCodeCombination(KeyCode.ENTER) to { _ ->
					entity.name = text
					swapTo(label)
				},
				KeyCodeCombination(KeyCode.ESCAPE) to { _ -> swapTo(label) }
		))
	}
	override val root: VBox = VBox(label).apply {
		styleClass += "entity-name"
	}

	init {
		entity.register({ target, event ->
			when (event) {
				EntityChangeEvent.NAME -> {
					label.text = target.name
					textField.text = target.name
				}
			}
		})
	}

	private fun swapTo(node: Node) {
		root.children.clear()
		root.children += node

		node.requestFocus()
	}
}