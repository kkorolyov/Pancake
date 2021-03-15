package dev.kkorolyov.pancake.platform

import dev.kkorolyov.pancake.platform.entity.Component
import dev.kkorolyov.pancake.platform.entity.Entity
import dev.kkorolyov.pancake.platform.entity.EntityPool
import dev.kkorolyov.pancake.platform.entity.Signature
import dev.kkorolyov.pancake.platform.event.EventBroadcaster
import dev.kkorolyov.pancake.platform.media.graphic.RenderMedium
import dev.kkorolyov.pancake.platform.utility.DebugRenderer
import dev.kkorolyov.pancake.platform.utility.Limiter

import spock.lang.Specification

import static dev.kkorolyov.pancake.platform.SpecUtilities.randLong

class GameEngineSpec extends Specification {
	long dt = randLong()
	Signature signature = new Signature(MockComponent)

	EventBroadcaster.Managed events = new EventBroadcaster.Managed()
	DebugRenderer debugRenderer = new DebugRenderer(Mock(RenderMedium))
	EntityPool entities = new EntityPool(events)
	Entity entity = entities.create()
			.add(new MockComponent())

	GameSystem deadSystem = Mock() {
		getSignature() >> signature
		getLimiter() >> Mock(Limiter)
	}
	GameSystem readySystem = Mock() {
		getSignature() >> signature
		getLimiter() >> Mock(Limiter) {
			isReady(_) >> true
			consumeElapsed() >> dt
		}
	}

	GameEngine engine = new GameEngine(events, entities, [readySystem, deadSystem], debugRenderer)

	def "invokes 'before' and 'after' on ready systems on update"() {
		when:
		engine.update(dt)

		then:
		with(deadSystem) {
			0 * before(dt)
			0 * after(dt)
		}
		with(readySystem) {
			1 * before(dt)
			1 * after(dt)
		}
	}

	def "invokes 'update' on system for each relevant entity on update"() {
		when:
		engine.update(dt)

		then:
		1 * readySystem.update(entity, dt)
		0 * deadSystem.update(_, _)
	}
	def "does not invoke 'update' on system if no relevant entities"() {
		when:
		engine.update(dt)

		then:
		1 * readySystem.getSignature() >> new Signature(new Component() {}.class)

		0 * readySystem.update(_, _)
		0 * deadSystem.update(_, _)
	}

	def "shares services with added system"() {
		when:
		engine.add(readySystem)

		then:
		1 * readySystem.setResources(!null)
	}
	def "unshares services from removed system"() {
		when:
		engine.remove(readySystem)

		then:
		1 * readySystem.setResources(null)
	}

	def "invokes 'attach' on added system"() {
		when:
		engine.add(readySystem)

		then:
		1 * readySystem.attach()
	}
	def "invokes 'detach' on removed system"() {
		when:
		engine.remove(readySystem)

		then:
		1 * readySystem.detach()
	}

	class MockComponent implements Component {}
}
