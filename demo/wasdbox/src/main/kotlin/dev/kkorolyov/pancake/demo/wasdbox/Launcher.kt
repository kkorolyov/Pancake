package dev.kkorolyov.pancake.demo.wasdbox

import dev.kkorolyov.pancake.core.component.ActionQueue
import dev.kkorolyov.pancake.core.component.Transform
import dev.kkorolyov.pancake.core.component.movement.Damping
import dev.kkorolyov.pancake.core.component.movement.Force
import dev.kkorolyov.pancake.core.component.movement.Mass
import dev.kkorolyov.pancake.core.component.movement.Velocity
import dev.kkorolyov.pancake.core.system.AccelerationSystem
import dev.kkorolyov.pancake.core.system.ActionSystem
import dev.kkorolyov.pancake.core.system.DampingSystem
import dev.kkorolyov.pancake.core.system.MovementSystem
import dev.kkorolyov.pancake.graphics.jfx.AddCamera
import dev.kkorolyov.pancake.graphics.jfx.Camera
import dev.kkorolyov.pancake.graphics.jfx.component.Graphic
import dev.kkorolyov.pancake.graphics.jfx.renderable.Rectangle
import dev.kkorolyov.pancake.graphics.jfx.system.DrawSystem
import dev.kkorolyov.pancake.input.jfx.Compensated
import dev.kkorolyov.pancake.input.jfx.Reaction
import dev.kkorolyov.pancake.input.jfx.component.Input
import dev.kkorolyov.pancake.input.jfx.system.InputSystem
import dev.kkorolyov.pancake.platform.GameEngine
import dev.kkorolyov.pancake.platform.GameLoop
import dev.kkorolyov.pancake.platform.Resources
import dev.kkorolyov.pancake.platform.action.Action
import dev.kkorolyov.pancake.platform.entity.EntityPool
import dev.kkorolyov.pancake.platform.event.EventLoop
import dev.kkorolyov.pancake.platform.math.Vectors
import dev.kkorolyov.pancake.platform.plugin.DeferredConverterFactory
import dev.kkorolyov.pancake.platform.plugin.Plugins
import dev.kkorolyov.pancake.platform.registry.Registry
import dev.kkorolyov.pancake.platform.registry.ResourceReader
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color

val pane = FlowPane(Orientation.HORIZONTAL).apply {
	onMouseClicked = EventHandler { requestFocus() }
}

val actions by lazy {
	Resources.inStream("actions.yaml").use {
		Registry<String, Action>().apply {
			load(ResourceReader(Plugins.deferredConverter(DeferredConverterFactory.ActionStrat::class.java)).fromYaml(it))
		}
	}
}

val events = EventLoop.Broadcasting().apply {
	enqueue(AddCamera(Camera(Vectors.create(0.0, 0.0), Vectors.create(32.0, 32.0))))
}
val entities = EntityPool(events)
val gameEngine = GameEngine(
	events,
	entities,
	listOf(
		ActionSystem(),
		InputSystem(listOf(pane)),
		AccelerationSystem(),
		MovementSystem(),
		DampingSystem(),
		DrawSystem(pane)
	)
)
val gameLoop = GameLoop(
	gameEngine
)

val player = entities.create().apply {
	put(
		Mass(0.01),
		Force(Vectors.create(0.0, 0.0, 0.0)),
		Velocity(Vectors.create(0.0, 0.0, 0.0)),
		Damping(Vectors.create(0.0, 0.0, 0.0)),
		Transform(Vectors.create(0.0, 0.0, 0.0)),
		Graphic(Rectangle(Vectors.create(1.0, 1.0), Color.AQUA)),
		Input(
			Reaction.matchType(
				Reaction.whenCode(
					KeyCode.W to Reaction.keyToggle(Compensated(actions["forceUp"], actions["forceDown"])),
					KeyCode.S to Reaction.keyToggle(Compensated(actions["forceDown"], actions["forceUp"])),
					KeyCode.A to Reaction.keyToggle(Compensated(actions["forceLeft"], actions["forceRight"])),
					KeyCode.D to Reaction.keyToggle(Compensated(actions["forceRight"], actions["forceLeft"]))
				)
			)
		),
		ActionQueue()
	)
}

fun main() {
	Platform.startup {
		Demo(Scene(pane), gameLoop::stop)
	}
	Thread(gameLoop::start).start()
}
