package dev.kkorolyov.pancake.platform.serialization.action

import dev.kkorolyov.pancake.platform.action.ActionRegistry
import dev.kkorolyov.pancake.platform.action.KeyAction
import dev.kkorolyov.pancake.platform.action.MultiStageAction
import dev.kkorolyov.pancake.platform.specbase.BaseSerializerSpec
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton

import static dev.kkorolyov.pancake.platform.SpecUtilities.randString

class KeyActionSerializerSpec extends BaseSerializerSpec<KeyAction, String> {
	List<Enum> inputs = KeyCode.values() + MouseButton.values()
	MultiStageAction delegateAction = Mock()
	String delegateActionS = randString()

	def setup() {
		reps << [(new KeyAction(delegateAction, inputs)): "$inputs=$delegateActionS"]

		serializer = new KeyActionSerializer(Mock(ActionRegistry))
		mockAutoSerializer([delegateAction], [delegateActionS])
	}
}
