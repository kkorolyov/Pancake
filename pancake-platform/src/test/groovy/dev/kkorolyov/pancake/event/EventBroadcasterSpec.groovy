package dev.kkorolyov.pancake.event

import dev.kkorolyov.pancake.SpecUtilities

import spock.lang.Shared
import spock.lang.Specification

import java.util.function.Consumer

class EventBroadcasterSpec extends Specification {
	@Shared String event = "SomeEvent"
	@Shared Consumer<?> receiver = {t, rt -> 1}

	Map<String, Set<Consumer<?>>> receivers = Mock()
	HashSet<Consumer<?>> receiverSet = Mock()

	EventBroadcaster eventBroadcaster = new EventBroadcaster()

	def setup() {
		SpecUtilities.setField("receivers", eventBroadcaster, receivers)
	}

	def "registers receiver to correct event"() {
		when:
		eventBroadcaster.register(event, receiver)

		then:
		1 * receivers.computeIfAbsent(event, _) >> receiverSet
		1 * receiverSet.add(receiver)
	}

	def "unregisters receiver from correct event"() {
		when:
		eventBroadcaster.unregister(event, receiver)

		then:
		1 * receivers.get(event) >> receiverSet	// Mock event existence
		1 * receiverSet.remove(receiver)
	}
	def "does nothing when unregistering from nonexistent event"() {
		when:
		eventBroadcaster.unregister(event, receiver)

		then:
		1 * receivers.get(event)	// Mock event nonexistence
		0 * receiverSet.remove(receiver)
	}
}
