package ch.judos

import ch.judos.model.EvaluationEnvironment
import ch.judos.service.Parser
import ch.judos.service.Tokenizer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ParserTests {
	
	@Test
	fun testParsing() {
		val env = EvaluationEnvironment()
		
		assertEquals(7.0, Parser(Tokenizer.tokenize("1 + 2 * 3")).expression().evaluate(env))
		assertEquals(10.0, Parser(Tokenizer.tokenize("2 * (2 + 3)")).expression().evaluate(env))
		assertEquals(18.0, Parser(Tokenizer.tokenize("2 * (3^2)")).expression().evaluate(env))
		assertEquals(2.0, Parser(Tokenizer.tokenize("2 * (3%2)")).expression().evaluate(env))
		assertEquals(2.0, Parser(Tokenizer.tokenize("2 / (3%2)")).expression().evaluate(env))
		
		env.variables["x"] = 5.0
		assertEquals(10.0, Parser(Tokenizer.tokenize("2 * x")).expression().evaluate(env))
		
	}
}
