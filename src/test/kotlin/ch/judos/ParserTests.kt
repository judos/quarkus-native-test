package ch.judos

import ch.judos.model.BasicEvaluationEnvironment
import ch.judos.model.EvaluationEnvironment
import ch.judos.model.FunctionDefinition
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
		
		env.runAssignment(Parser(Tokenizer.tokenize("x = 1")).parseAssignment())
		assertEquals(2.0, Parser(Tokenizer.tokenize("2 * x")).parseExpression().evaluate(env))
		
		
		env.saveFunction(Parser(Tokenizer.tokenize("f(x) = x + 1")).parseFunction())
		assertEquals(6.0, Parser(Tokenizer.tokenize("f(5)")).parseExpression().evaluate(env))
	}
	
	@Test
	fun testParsingBools() {
		val env = EvaluationEnvironment()
		assertEquals(true, Parser(Tokenizer.tokenize("true || false")).parseExpression().evaluate(env))
		assertEquals(false, Parser(Tokenizer.tokenize("true && false")).parseExpression().evaluate(env))
	}
	
	@Test
	fun testBaseFunctions() {
		val env = BasicEvaluationEnvironment()
		env.saveFunction(Parser(Tokenizer.tokenize("f(x) = WENN(x,3,2)")).parseFunction())
		assertEquals(3.0, Parser(Tokenizer.tokenize("f(true)")).parseExpression().evaluate(env))
		assertEquals(2.0, Parser(Tokenizer.tokenize("f(false)")).parseExpression().evaluate(env))
	}
	
	@Test
	fun testLambdas() {
		val env = BasicEvaluationEnvironment()
		assertEquals(FunctionDefinition::class, Parser(Tokenizer.tokenize("f() = { x -> x + 1 }")).parseFunction().evaluate(env)::class)
		assertEquals(1.0, Parser(Tokenizer.tokenize("linint(0.5, { x -> 2*x })")).parseExpression().evaluate(env))
		assertEquals(2.5, Parser(Tokenizer.tokenize("linint(1.5, { x -> x^2 })")).parseExpression().evaluate(env))
		assertEquals(45.0, Parser(Tokenizer.tokenize("sum(0, 9, { x -> x })")).parseExpression().evaluate(env))
	}
	
	@Test
	fun testRoundingErrors() {
		val env = BasicEvaluationEnvironment()
		assertEquals(2.0, Parser(Tokenizer.tokenize("sum(1+1 / 10 * 10, 2, { x -> x })")).parseExpression().evaluate(env))
	}
	
	@Test
	fun testOperatorOrder() {
		val env = BasicEvaluationEnvironment()
		assertEquals(10.0, Parser(Tokenizer.tokenize("10 / 10 * 10")).parseExpression().evaluate(env))
	}
}
