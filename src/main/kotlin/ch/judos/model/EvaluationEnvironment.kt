package ch.judos.model

class EvaluationEnvironment {
	
	val variables = mutableMapOf<String, Any>()
	
	
	fun evaluateVar(name: String): Any {
		return variables[name] ?: throw Exception("Variable $name not found")
	}
	
	fun evaluateFunction(name: String, arguments: List<Any>): Any {
		// TODO: implement
		return variables[name] ?: throw Exception("Function $name not found")
	}
	
}
