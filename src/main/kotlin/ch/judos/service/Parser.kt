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
	
	fun expression(): Expression {
		return additiveExpression()
	}
	
	/** AdditiveExpression
	 * : <MultiplicativeExpression> (+ - <Expression>)*
	 */
	fun additiveExpression(): Expression {
		var left = multiplicativeExpression()
		while (lookahead?.type == TokenType.AdditiveOperator) {
			val operator = eat(TokenType.AdditiveOperator)
			val right = expression()
			left = BinaryExpression(left, operator, right)
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
			left = BinaryExpression(left, operator, right)
		}
		return left
	}
	
	/** PotencyExpression
	 * : <PrimaryExpression> (^ <Expression>)*
	 */
	fun potencyExpression(): Expression {
		var left = primaryExpression()
		while (lookahead?.type == TokenType.Potency) {
			val op = eat(TokenType.Potency)
			val right = expression()
			left = BinaryExpression(left, op, right)
		}
		return left
	}
	
	/** PrimaryExpression
	 * : <String> | <Number> | <Boolean> | <Variable>(<Arguments>) | <Variable> | ( <Expression> )
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
