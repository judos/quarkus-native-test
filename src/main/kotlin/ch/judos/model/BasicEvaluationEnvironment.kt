package ch.judos.model

class BasicEvaluationEnvironment : EvaluationEnvironment() {
	
	init {
	
	}
	
	override fun evaluateFunction(name: String, arguments: List<Any>): Any {
		when (name.lowercase()) {
			"wenn", "if" -> {
				checkArgs(name, arguments, ExpressionType.Boolean, ExpressionType.Double, ExpressionType.Double)
				return if (arguments[0] as Boolean) arguments[1] else arguments[2]
			}
		}
		return super.evaluateFunction(name, arguments)
	}
	
	fun checkArgs(name: String, args: List<Any>, vararg expected: ExpressionType) {
		if (args.size != expected.size) {
			throw Exception("Function $name expected ${expected.size} arguments, got ${args.size}")
		}
		for (i in expected.indices) {
			if (!expected[i].isMatch(args[i])) {
				throw Exception("Function $name expected argument $i to be of type ${expected[i]}, got ${args[i]}")
			}
		}
	}
}
