package ch.judos.service

import ch.judos.model.*

class Parser(
	val tokens: List<Token>
) {
	
	private val lookahead: Token?
		get() = tokens.getOrNull(tokenCursor)
	private val lookahead2: Token?
		get() = tokens.getOrNull(tokenCursor + 1)
	private var tokenCursor = 0
	
	/** FunctionDefinition
	 * : <Identifier>(<Parameters>) = <Expression>
	 */
	fun parseFunction(): FunctionDefinition {
		val name = eat(TokenType.Identifier)
		eat(TokenType.LB)
		val parameters = parameters()
		eat(TokenType.RB)
		eat(TokenType.Assign)
		val body = expression()
		if (tokenCursor < tokens.size) {
			throw Exception("Unexpected tokens after function definition ${lookahead!!.type}(\"${lookahead!!.value}\")")
		}
		return FunctionDefinition(name, parameters, body)
	}
	
	/** Parameters
	 * : <Identifier>? (, <Identifier>)*
	 */
	fun parameters(): List<String> {
		val parameters = mutableListOf<String>()
		if (lookahead?.type == TokenType.Identifier)
			parameters.add(eat(TokenType.Identifier))
		else return parameters
		while (lookahead?.type == TokenType.Comma) {
			eat(TokenType.Comma)
			parameters.add(eat(TokenType.Identifier))
		}
		return parameters
	}
	
	/** Assignment
	 * : <Variable> = <Expression>
	 */
	fun parseAssignment(): Assignment {
		val variable = eat(TokenType.Identifier)
		eat(TokenType.Assign)
		val expression = expression()
		if (tokenCursor < tokens.size) {
			throw Exception("Unexpected tokens after assignment ${lookahead!!.type}(\"${lookahead!!.value}\")")
		}
		return Assignment(variable, expression)
	}
	
	fun parseExpression(): Expression {
		val expression = disjunction()
		if (tokenCursor < tokens.size) {
			throw Exception("Unexpected tokens after expression ${lookahead!!.type}(\"${lookahead!!.value}\")")
		}
		return expression
	}
	
	private fun expression(): Expression {
		return disjunction()
	}
	
	/** Disjunction
	 * : <Conjunction> (|| <Disjunction>)*
	 */
	fun disjunction(): Expression {
		var left = conjunction()
		while (lookahead?.type == TokenType.LogicalOr) {
			val operator = eat(TokenType.LogicalOr)
			val right = disjunction()
			left = BinaryBooleanExpression(left, operator, right)
		}
		return left
	}
	
	/** Conjunction
	 * : <Comparison> (&& <Conjunction>)*
	 */
	fun conjunction(): Expression {
		var left = comparison()
		while (lookahead?.type == TokenType.LogicalAnd) {
			val operator = eat(TokenType.LogicalAnd)
			val right = conjunction()
			left = BinaryBooleanExpression(left, operator, right)
		}
		return left
	}
	
	/** Comparison
	 * : <AdditiveExpression> (< > <= >= == != <AdditiveExpression>)?
	 */
	fun comparison(): Expression {
		var left = additiveExpression()
		if (lookahead?.type == TokenType.Comparison) {
			val operator = eat(TokenType.Comparison)
			val right = additiveExpression()
			left = NumberComparisonExpression(left, operator, right)
		}
		return left
	}
	
	/** AdditiveExpression
	 * : <MultiplicativeExpression> (+ - <Expression>)*
	 */
	fun additiveExpression(): Expression {
		var left = multiplicativeExpression()
		while (lookahead?.type == TokenType.AdditiveOperator) {
			val operator = eat(TokenType.AdditiveOperator)
			val right = expression()
			left = BinaryNumberExpression(left, operator, right)
		}
		return left
	}
	
	
	/** MultiplicativeExpression
	 * : <PotencyExpression> (* / <Expression>)*
	 */
	fun multiplicativeExpression(): Expression {
		var left = potencyExpression()
		while (lookahead?.type == TokenType.MultiplicativeOperator) {
			val operator = eat(TokenType.MultiplicativeOperator)
			val right = expression()
			left = BinaryNumberExpression(left, operator, right)
		}
		return left
	}
	
	/** PotencyExpression
	 * : <PrefixExpression> (^ <Expression>)*
	 */
	fun potencyExpression(): Expression {
		var left = primaryExpression()
		while (lookahead?.type == TokenType.Potency) {
			val op = eat(TokenType.Potency)
			val right = expression()
			left = BinaryNumberExpression(left, op, right)
		}
		return left
	}
	
	/** PrefixExpression
	 * : <Negate>? <PrimaryExpression>
	 */
	fun prefixExpression(): Expression {
		if (lookahead?.type == TokenType.Negate) {
			val op = eat(TokenType.Negate)
			val right = primaryExpression()
			return UnaryBooleanExpression(op, right)
		}
		return primaryExpression()
	}
	
	/** PrimaryExpression
	 * : <String> | <Number> | <Boolean> | <Function>'('<Arguments>')' | <Variable> |
	 * '(' <Expression> ')' |
	 * '{' <Parameter> (',' <Parameter>)* -> <Expression> '}'
	 */
	fun primaryExpression(): Expression {
		if (lookahead?.type == TokenType.String) {
			return stringLiteral()
		}
		if (lookahead?.type == TokenType.Number) {
			return numericLiteral()
		}
		if (lookahead?.type == TokenType.Boolean) {
			return booleanLiteral()
		}
		if (lookahead?.type == TokenType.Identifier && lookahead2?.type == TokenType.LB) {
			val name = eat(TokenType.Identifier)
			eat(TokenType.LB)
			val arguments = arguments()
			eat(TokenType.RB)
			return FunctionCall(name, arguments)
		}
		if (lookahead?.type == TokenType.Identifier) {
			val name = eat(TokenType.Identifier)
			return VariableLiteral(name)
		}
		if (lookahead?.type == TokenType.LB) {
			eat(TokenType.LB)
			val expression = expression()
			eat(TokenType.RB)
			return expression
		}
		if (lookahead?.type == TokenType.LC) {
			eat(TokenType.LC)
			val parameters = parameters()
			eat(TokenType.Arrow)
			val expression = expression()
			eat(TokenType.RC)
			return FunctionDefinition("lambda", parameters, expression)
		}
		throw Exception("Unexpected type $lookahead")
	}
	
	/** Arguments
	 * : <Expression> (, <Expression>)*
	 */
	fun arguments(): List<Expression> {
		val arguments = mutableListOf<Expression>()
		arguments.add(expression())
		while (lookahead?.type == TokenType.Comma) {
			eat(TokenType.Comma)
			arguments.add(expression())
		}
		return arguments
	}
	
	fun stringLiteral(): StringLiteral {
		val token = eat(TokenType.String)
		return StringLiteral(token)
	}
	
	fun numericLiteral(): NumberLiteral {
		val token = eat(TokenType.Number)
		return NumberLiteral(token)
	}
	
	fun booleanLiteral(): BooleanLiteral {
		val token = eat(TokenType.Boolean)
		return BooleanLiteral(token)
	}
	
	fun eat(tokenType: TokenType): String {
		val token = lookahead
			?: throw Exception("Unexpected end of input, expected $tokenType")
		if (token.type != tokenType) {
			throw Exception("Unexpected token $token, expected $tokenType")
		}
		tokenCursor++
		return token.value
	}
	
}
