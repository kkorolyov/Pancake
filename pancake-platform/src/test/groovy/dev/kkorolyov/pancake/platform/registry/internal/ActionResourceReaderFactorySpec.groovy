package dev.kkorolyov.pancake.platform.registry.internal

import dev.kkorolyov.pancake.platform.action.Action
import dev.kkorolyov.pancake.platform.action.CollectiveAction
import dev.kkorolyov.pancake.platform.action.KeyAction
import dev.kkorolyov.pancake.platform.action.MultiStageAction
import dev.kkorolyov.pancake.platform.registry.Registry
import dev.kkorolyov.simplefuncs.convert.Converter
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton

import spock.lang.Shared
import spock.lang.Specification

import java.util.function.Function

import static dev.kkorolyov.pancake.platform.SpecUtilities.randKeyCode
import static dev.kkorolyov.pancake.platform.SpecUtilities.randMouseButton
import static dev.kkorolyov.simplefuncs.function.Memoizer.memoize

class ActionResourceReaderFactorySpec extends Specification {
	@Shared
	String[] references = ["ref", "ref4", "newRef"]
	@Shared
	String[] multiStages = ["startStep", "holdStep", "endStep"]

	@Shared
	MouseButton mouseButton = randMouseButton()
	@Shared
	KeyCode keyCode = randKeyCode()

	@Shared
	ActionResourceReaderFactory factory = new ActionResourceReaderFactory(memoize({ factory.get(it) } as Function))

	Registry<String, Action> registry = new Registry<>();
	Converter<String, Optional<Action>> converter = factory.get(registry)

	def "reads reference"() {
		when:
		Map<String, Action> referenceToAction = references.collectEntries { [(it): Mock(Action)] }
		referenceToAction.each(registry.&put)

		then:
		referenceToAction.each { key, action ->
			converter.convert(key).orElse(null) == action
		}
	}

	def "reads collective"() {
		when:
		Map<String, Action> referenceToAction = references.collectEntries { [(it): Mock(Action)] }
		referenceToAction.each(registry.&put)

		then:
		converter.convert(references as String).orElse(null) == new CollectiveAction(referenceToAction.values())
	}

	def "reads multi-stage"() {
		when:
		Map<String, Action> stageToAction = multiStages.collectEntries { [(it): Mock(Action)] }
		stageToAction.each(registry.&put)

		then:
		converter.convert("{${multiStages.join(',')}}" as String).orElse(null) == new MultiStageAction(
				stageToAction.values()[0],
				stageToAction.values()[1],
				stageToAction.values()[2],
				ActionResourceReaderFactory.MULTI_STAGE_HOLD_THRESHOLD
		)
	}

	def "reads key"() {
		when:
		Map<String, Action> referenceToAction = references.collectEntries { [(it): Mock(Action)] }
		referenceToAction.each(registry.&put)

		then:
		converter.convert("($mouseButton, $keyCode)=${references}" as String).orElse(null) == new KeyAction(
				new MultiStageAction(new CollectiveAction(referenceToAction.values()), null, null, 0),
				[mouseButton, keyCode]
		)
	}
}