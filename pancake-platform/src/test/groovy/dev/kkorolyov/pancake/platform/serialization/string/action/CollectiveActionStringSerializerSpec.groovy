package dev.kkorolyov.pancake.platform.serialization.string.action

import dev.kkorolyov.pancake.platform.action.Action
import dev.kkorolyov.pancake.platform.action.ActionRegistry
import dev.kkorolyov.pancake.platform.action.CollectiveAction
import dev.kkorolyov.pancake.platform.specbase.BaseSerializerSpec

import java.util.stream.Collectors

import static dev.kkorolyov.simplespecs.SpecUtilities.randString

class CollectiveActionStringSerializerSpec extends BaseSerializerSpec<CollectiveAction, String> {
	List<Action> actions = (1..5).collect {Mock(Action)}
	List<String> actionsS = actions.collect {randString()}

	def setup() {
		reps << [(new CollectiveAction(actions)): actionsS.stream().collect(Collectors.joining(", ", "[", "]"))]

		serializer = new CollectiveActionStringSerializer(Mock(ActionRegistry))
		mockAutoSerializer(actions, actionsS)
	}
}
