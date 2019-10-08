package dev.kkorolyov.pancake.core.serialization.string.action

import dev.kkorolyov.pancake.core.action.ForceAction
import dev.kkorolyov.pancake.platform.math.Vector
import dev.kkorolyov.pancake.platform.specbase.BaseSerializerSpec

import static dev.kkorolyov.pancake.platform.SpecUtilities.bigDecimal
import static dev.kkorolyov.simplespecs.SpecUtilities.randDouble 

class ForceActionSerializerSpec extends BaseSerializerSpec<ForceAction, String> {
	double x = randDouble(), y = randDouble(), z = randDouble()
	Vector force = new Vector(x, y, z)

	def setup() {
		reps << [(new ForceAction(force)): "FORCE{(${bigDecimal(x)},${bigDecimal(y)},${bigDecimal(z)})}"]

		serializer = new ForceActionSerializer()
	}
}