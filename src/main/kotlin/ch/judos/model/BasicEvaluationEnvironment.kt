package ch.judos.model

import kotlin.math.floor

class BasicEvaluationEnvironment : EvaluationEnvironment() {
	
	override fun evaluateFunction(name: String, arguments: List<Any>): Any {
		return when (name.lowercase()) {
			"wenn", "if" -> wenn(name, arguments)
			"linint" -> linint(name, arguments)
			"summe", "sum" -> summe(name, arguments)
			else -> super.evaluateFunction(name, arguments)
		}
	}
	
	private fun summe(name: String, arguments: List<Any>): Double {
		checkArgs(name, arguments, ExpressionType.Double, ExpressionType.Double, ExpressionType.Lambda)
		val start = arguments[0] as Double
		val end = arguments[1] as Double
		val lamda = arguments[2] as FunctionDefinition
		if (lamda.parameters.size != 1) {
			throw Exception(
				"Function $name expected lambda with 1 argument, got ${lamda.parameters.size}")
		}
		val env = EvaluationEnvironment(this)
		var sum = 0.0
		for (i in start.toInt()..end.toInt()) {
			env.variables[lamda.parameters[0]] = i.toDouble()
			sum += lamda.body.evaluate(env) as Double
		}
		return sum
	}
	
	private fun wenn(name: String, arguments: List<Any>): Any {
		checkArgs(name, arguments, ExpressionType.Boolean, ExpressionType.Double, ExpressionType.Double)
		return if (arguments[0] as Boolean) arguments[1] else arguments[2]
	}
	
	private fun linint(name: String, arguments: List<Any>): Double {
		checkArgs(name, arguments, ExpressionType.Double, ExpressionType.Lambda)
		val x = arguments[0] as Double
		val lamda = arguments[1] as FunctionDefinition
		if (lamda.parameters.size != 1) {
			throw Exception(
				"Function $name expected lambda with 1 argument, got ${lamda.parameters.size}")
		}
		val env = EvaluationEnvironment(this)
		val x0 = floor(x)
		val x1 = floor(x) + 1
		env.variables[lamda.parameters[0]] = x0
		val y0 = lamda.body.evaluate(env) as Double
		env.variables[lamda.parameters[0]] = x1
		val y1 = lamda.body.evaluate(env) as Double
		return y0 + (y1 - y0) / (x1 - x0) * (x - x0)
	}
	
	fun checkArgs(name: String, args: List<Any>, vararg expected: ExpressionType) {
		if (args.size != expected.size) {
			throw Exception("Function $name expected ${expected.size} arguments, got ${args.size}")
		}
		for (i in expected.indices) {
			if (!expected[i].isMatch(args[i])) {
				throw Exception(
					"Function $name expected argument $i to be of type ${expected[i]}, got ${args[i]}")
			}
		}
	}
}
