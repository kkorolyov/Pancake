package dev.kkorolyov.pancake.core.serialization.action

import dev.kkorolyov.pancake.core.action.TransformAction
import dev.kkorolyov.pancake.platform.action.ActionRegistry
import dev.kkorolyov.pancake.platform.math.Vector
import dev.kkorolyov.pancake.platform.specbase.BaseContextualSerializerSpec

import static dev.kkorolyov.pancake.platform.SpecUtilities.bigDecimal
import static dev.kkorolyov.pancake.platform.SpecUtilities.randFloat

class TransformActionSerializerSpec extends BaseContextualSerializerSpec<TransformAction, String, ActionRegistry> {
	float x = randFloat(), y = randFloat(), z = randFloat()
	Vector position = new Vector(x, y, z)
	float rotation = randFloat()

	def setup() {
		reps += [
				(new TransformAction(position, rotation)): "TRANSFORM{(${bigDecimal(x)},${bigDecimal(y)},${bigDecimal(z)}), ${bigDecimal(rotation)}}",
				(new TransformAction(position)): "TRANSFORM{(${bigDecimal(x)},${bigDecimal(y)},${bigDecimal(z)})}"
		]
		context = null

		serializer = new TransformActionSerializer()
	}
}