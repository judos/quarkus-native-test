package ch.judos

import ch.judos.model.EvaluationEnvironment
import ch.judos.service.Parser
import ch.judos.service.Tokenizer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ParserTests {
	
	@Test
	fun testParsingNumbers() {
		val env = EvaluationEnvironment()
		
		assertEquals(7.0, Parser(Tokenizer.tokenize("1 + 2 * 3")).parseExpression().evaluate(env))
		assertEquals(10.0, Parser(Tokenizer.tokenize("2 * (2 + 3)")).parseExpression().evaluate(env))
		assertEquals(18.0, Parser(Tokenizer.tokenize("2 * (3^2)")).parseExpression().evaluate(env))
		assertEquals(2.0, Parser(Tokenizer.tokenize("2 * (3%2)")).parseExpression().evaluate(env))
		assertEquals(2.0, Parser(Tokenizer.tokenize("2 / (3%2)")).parseExpression().evaluate(env))
		
		env.variables["x"] = 5.0
		assertEquals(10.0, Parser(Tokenizer.tokenize("2 * x")).parseExpression().evaluate(env))
		
		Parser(Tokenizer.tokenize("x = 1")).parseAssignment().evaluate(env)
		assertEquals(2.0, Parser(Tokenizer.tokenize("2 * x")).parseExpression().evaluate(env))
		
		
		Parser(Tokenizer.tokenize("f(x) = x + 1")).parseFunction().evaluate(env)
		assertEquals(6.0, Parser(Tokenizer.tokenize("f(5)")).parseExpression().evaluate(env))
		
		Parser(Tokenizer.tokenize("f(x) = f(x-1) + 1")).parseFunction().evaluate(env)
	}
	
	@Test
	fun testParsingBools() {
		val env = EvaluationEnvironment()
		assertEquals(true, Parser(Tokenizer.tokenize("true || false")).parseExpression().evaluate(env))
		assertEquals(false, Parser(Tokenizer.tokenize("true && false")).parseExpression().evaluate(env))
		
	}
}
