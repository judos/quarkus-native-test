package ch.judos.model

import kotlin.math.pow


class FunctionDefinition(
		var name: String,
		var parameters: List<String>,
		var body: Expression
) {
	fun evaluate(env: EvaluationEnvironment) {
		env.functions[name] = this
	}
}

class Assignment(
		val variable: String,
		val expression: Expression
) {
	fun evaluate(env: EvaluationEnvironment) {
		env.variables[variable] = expression.evaluate(env) as Double
	}
}

class UnaryBooleanExpression(
		val operator: String,
		val expression: Expression
) : Expression() {
	override fun getType(): ExpressionType {
		if (expression.getType() != ExpressionType.Boolean) {
			throw Exception("BooleanExpression requires value to be Boolean")
		}
		return ExpressionType.Boolean
	}
	
	override fun evaluate(env: EvaluationEnvironment): Any {
		if (operator == "!") {
			return !(expression.evaluate(env) as Boolean)
		}
		throw Exception("Unknown operator $operator")
	}
}

class BinaryBooleanExpression(
		val left: Expression,
		val operator: String,
		val right: Expression
) : Expression() {
	override fun getType(): ExpressionType {
		if (left.getType() != ExpressionType.Boolean || right.getType() != ExpressionType.Boolean) {
			throw Exception("BooleanExpression requires both sides to be Boolean")
		}
		return ExpressionType.Boolean
	}
	
	override fun evaluate(env: EvaluationEnvironment): Any {
		if (operator == "||") {
			return (left.evaluate(env) as Boolean) || (right.evaluate(env) as Boolean)
		}
		if (operator == "&&") {
			return (left.evaluate(env) as Boolean) && (right.evaluate(env) as Boolean)
		}
		throw Exception("Unknown operator $operator")
	}
}

class BinaryBooleanComparisonExpression(
		val left: Expression,
		val operator: String,
		val right: Expression
) : Expression() {
	override fun getType(): ExpressionType {
		if (left.getType() != ExpressionType.Double || right.getType() != ExpressionType.Double) {
			throw Exception("ComparisonExpression requires both sides to be Double")
		}
		return ExpressionType.Boolean
	}
	
	override fun evaluate(env: EvaluationEnvironment): Any {
		val l = left.evaluate(env) as Double
		val r = right.evaluate(env) as Double
		return when (operator) {
			"==" -> l == r
			"!=" -> l != r
			">=" -> l >= r
			"<=" -> l <= r
			">" -> l > r
			"<" -> l < r
			else -> throw Exception("Unknown operator $operator")
		}
	}
}

class BinaryNumberExpression(
		val left: Expression,
		val operator: String,
		val right: Expression
) : Expression() {
	override fun getType(): ExpressionType {
		if (left.getType() != ExpressionType.Double || right.getType() != ExpressionType.Double) {
			throw Exception("NumberExpression requires both sides to be Double")
		}
		return ExpressionType.Double
	}
	
	override fun evaluate(env: EvaluationEnvironment): Any {
		val l = left.evaluate(env) as Double
		val r = right.evaluate(env) as Double
		return when (operator) {
			"+" -> l + r
			"-" -> l - r
			"*" -> l * r
			"/" -> l / r
			"%" -> l % r
			"^" -> l.pow(r)
			else -> throw Exception("Unknown operator $operator")
		}
	}
}

class FunctionCall(
		val name: String,
		val arguments: List<Expression>
) : Expression() {
	override fun getType(): ExpressionType {
		// TODO: implement
		return ExpressionType.Double
	}
	
	override fun evaluate(env: EvaluationEnvironment): Any {
		val args = arguments.map { it.evaluate(env) }
		return env.evaluateFunction(name, args)
	}
	
}

class VariableLiteral(
		val name: String
) : Expression() {
	override fun getType(): ExpressionType {
		// TODO: implement
		return ExpressionType.Double
	}
	
	override fun evaluate(env: EvaluationEnvironment): Any {
		return env.evaluateVar(name)
	}
	
}

class StringLiteral(
		value: String,
) : Expression() {
	val value = value.substring(1, value.length - 1)
	override fun getType(): ExpressionType {
		return ExpressionType.String
	}
	
	override fun evaluate(env: EvaluationEnvironment): Any {
		return value
	}
}

class BooleanLiteral(
		value: String,
) : Expression() {
	val value = value.toBoolean()
	override fun getType(): ExpressionType {
		return ExpressionType.Boolean
	}
	
	override fun evaluate(env: EvaluationEnvironment): Any {
		return value
	}
}

class NumberLiteral(
		value: String,
) : Expression() {
	val value = value.toDouble()
	override fun getType(): ExpressionType {
		return ExpressionType.Double
	}
	
	override fun evaluate(env: EvaluationEnvironment): Any {
		return value
	}
}

abstract class Expression(
) {
	abstract fun getType(): ExpressionType
	abstract fun evaluate(env: EvaluationEnvironment): Any
}
