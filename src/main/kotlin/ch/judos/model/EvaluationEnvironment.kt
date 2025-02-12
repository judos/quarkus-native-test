package ch.judos.model

class EvaluationEnvironment(
	val parent: EvaluationEnvironment? = null
) {
	
	val variables = mutableMapOf<String, Any>()
	val functions = mutableMapOf<String, FunctionDefinition>()
	
	fun evaluateVar(name: String): Any {
		return variables[name]
			?: parent?.evaluateVar(name)
			?: throw Exception("Variable $name not found")
	}
	
	fun evaluateFunction(name: String, arguments: List<Any>): Any {
		val function = functions[name]
			?: parent?.evaluateFunction(name, arguments)?.let { return it }
			?: throw Exception("Function $name not found")
		val env = EvaluationEnvironment(this)
		for (i in function.parameters.indices) {
			env.variables[function.parameters[i]] = arguments[i]
		}
		return function.body.evaluate(env)
	}
	
}
