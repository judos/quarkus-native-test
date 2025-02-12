package ch.judos.model

import jakarta.ws.rs.NotFoundException

open class EvaluationEnvironment(
	val parent: EvaluationEnvironment? = null
) {
	
	val variables = mutableMapOf<String, Any>()
	val functions = mutableMapOf<String, FunctionDefinition>()
	
	fun evaluateVar(name: String): Any {
		return variables[name]
			?: parent?.evaluateVar(name)
			?: throw NotFoundException("Variable $name not found")
	}
	
	open fun evaluateFunction(name: String, arguments: List<Any>): Any {
		val function = functions[name]
			?: parent?.evaluateFunction(name, arguments)?.let { return it }
			?: throw NotFoundException("Function $name not found")
		return evaluateFunction(function, arguments)
	}
	
	protected fun evaluateFunction(func: FunctionDefinition, arguments: List<Any>): Any {
		val env = EvaluationEnvironment(this)
		for (i in func.parameters.indices) {
			env.variables[func.parameters[i]] = arguments[i]
		}
		return func.body.evaluate(env)
	}
	
	fun saveFunction(function: FunctionDefinition) {
		functions[function.name] = function
	}
	
	fun runAssignment(assignment: Assignment) {
		variables[assignment.variable] = assignment.expression.evaluate(this)
	}
	
}
