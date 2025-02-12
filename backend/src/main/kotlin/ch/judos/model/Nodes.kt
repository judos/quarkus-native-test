package ch.judos.model

import kotlin.math.pow


class FunctionDefinition(
		var name: String,
		var parameters: List<String>,
		var body: Expression
): Expression() {
	override fun evaluate(env: EvaluationEnvironment): FunctionDefinition {
		return this
	}
}

class Assignment(
		val variable: String,
		val expression: Expression
)

class UnaryBooleanExpression(
		val operator: String,
		val expression: Expression
) : Expression() {
	
	override fun evaluate(env: EvaluationEnvironment): Any {
		val e = expression.evaluate(env)
		if (e !is Boolean) {
			throw Exception("BooleanExpression requires value to be Boolean")
		}
		return when (operator) {
			"!" -> !e
			else -> throw Exception("Unknown operator $operator")
		}
	}
}

class BinaryBooleanExpression(
		val left: Expression,
		val operator: String,
		val right: Expression
) : Expression() {
	
	override fun evaluate(env: EvaluationEnvironment): Any {
		val l = left.evaluate(env)
		val r = right.evaluate(env)
		if (l !is Boolean || r !is Boolean) {
			throw Exception("BooleanExpression requires both sides to be Boolean")
		}
		return when(operator) {
			"||" -> l || r
			"&&" -> l && r
			else -> throw Exception("Unknown operator $operator")
		}
	}
}

class NumberComparisonExpression(
		val left: Expression,
		val operator: String,
		val right: Expression
) : Expression() {
	override fun evaluate(env: EvaluationEnvironment): Any {
		val l = left.evaluate(env)
		val r = right.evaluate(env)
		if (l !is Double || r !is Double) {
			throw Exception("NumberComparison requires both sides to be Double")
		}
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
	override fun evaluate(env: EvaluationEnvironment): Any {
		val l = left.evaluate(env)
		val r = right.evaluate(env)
		if (l !is Double || r !is Double) {
			throw Exception("NumberExpression requires both sides to be Double")
		}
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
	override fun evaluate(env: EvaluationEnvironment): Any {
		val args = arguments.map { it.evaluate(env) }
		return env.evaluateFunction(name, args)
	}
	
}

class VariableLiteral(
		val name: String
) : Expression() {
	override fun evaluate(env: EvaluationEnvironment): Any {
		return env.evaluateVar(name)
	}
	
}

class StringLiteral(
		value: String,
) : Expression() {
	val value = value.substring(1, value.length - 1)
	override fun evaluate(env: EvaluationEnvironment): Any {
		return value
	}
}

class BooleanLiteral(
		value: String,
) : Expression() {
	val value = value.toBoolean()
	override fun evaluate(env: EvaluationEnvironment): Any {
		return value
	}
}

class NumberLiteral(
		value: String,
) : Expression() {
	val value = value.toDouble()
	override fun evaluate(env: EvaluationEnvironment): Any {
		return value
	}
}

abstract class Expression(
) {
	abstract fun evaluate(env: EvaluationEnvironment): Any
}
