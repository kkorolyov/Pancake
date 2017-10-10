package dev.kkorolyov.pancake.platform.action

import dev.kkorolyov.pancake.platform.entity.Entity

import spock.lang.Shared
import spock.lang.Specification

import static dev.kkorolyov.pancake.platform.action.MultiStageAction.ArmingOption.ACTIVATE
import static dev.kkorolyov.pancake.platform.action.MultiStageAction.ArmingOption.DEACTIVATE

class MultiStageActionSpec extends Specification {
	@Shared float holdThreshold = 0
	@Shared float dt = 0
	Entity entity = Mock()
	Action start = Mock()
	Action hold = Mock()
	Action end = Mock()

	MultiStageAction action = new MultiStageAction(start, hold, end, holdThreshold)

	def "does nothing if not armed before applied"() {
		when:
		action.accept(entity)

		then:
		0 * _.accept(_)
	}

	def "{ACTIVATE} -> start"() {
		when:
		action.arm(ACTIVATE, dt)
				.apply(entity)

		then:
		1 * start.accept(entity)
		0 * _.accept(_)
	}
	def "{DEACTIVATE} -> none"() {
		when:
		action.arm(DEACTIVATE, dt)
				.apply(entity)

		then:
		0 * _.accept(_)
	}

	def "{ACTIVATE, ACTIVATE} -> start, hold"() {
		when:
		action.arm(ACTIVATE, dt)
				.apply(entity)
		action.arm(ACTIVATE, dt)
				.apply(entity)

		then:
		1 * start.accept(entity)
		1 * hold.accept(entity)
		0 * _.accept(_)
	}
	def "{ACTIVATE, DEACTIVATE} -> start, end"() {
		when:
		action.arm(ACTIVATE, dt)
				.apply(entity)
		action.arm(DEACTIVATE, dt)
				.apply(entity)

		then:
		1 * start.accept(entity)
		1 * end.accept(entity)
		0 * _.accept(_)
	}

	def "{ACTIVATE, ACTIVATE, ACTIVATE} -> start, hold, none"() {
		when:
		action.arm(ACTIVATE, dt)
				.apply(entity)
		action.arm(ACTIVATE, dt)
				.apply(entity)
		action.arm(ACTIVATE, dt)
				.apply(entity)

		then:
		1 * start.accept(entity)
		1 * hold.accept(entity)
		0 * _.accept(_)
	}
	def "{ACTIVATE, ACTIVATE, DEACTIVATE} -> start, hold, end"() {
		when:
		action.arm(ACTIVATE, dt)
				.apply(entity)
		action.arm(ACTIVATE, dt)
				.apply(entity)
		action.arm(DEACTIVATE, dt)
				.apply(entity)

		then:
		1 * start.accept(entity)
		1 * hold.accept(entity)
		1 * end.accept(entity)
	}

	def "{ACTIVATE, ACTIVATE(before holdThreshold)} -> start, none"() {
		float holdThreshold = 1
		float dt = holdThreshold - 0.1
		action = new MultiStageAction(start, hold, end, holdThreshold)

		when:
		action.arm(ACTIVATE, dt)
				.apply(entity)
		action.arm(ACTIVATE, dt)
				.apply(entity)

		then:
		1 * start.accept(entity)
		0 * _.accept(_)
	}
}
