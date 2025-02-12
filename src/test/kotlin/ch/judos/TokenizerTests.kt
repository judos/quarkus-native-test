package ch.judos

import ch.judos.model.TokenType
import ch.judos.model.TokenType.*
import ch.judos.model.TokenType.Boolean
import ch.judos.model.TokenType.Number
import ch.judos.service.Tokenizer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TokenizerTests {
	
	@Test
	fun testTokenize() {
		assertEquals(listOf(Boolean, LogicalOr, Boolean), tokenize("true || false"))
		assertEquals(listOf(Number, AdditiveOperator, Number), tokenize("1 +  2"))
	}
	
	@Test
	fun testTokenizeSkip() {
		assertEquals(null, getNextToken("     	"))
		assertEquals(null, getNextToken("     // test comment	"))
		assertEquals(null, getNextToken("//"))
		assertEquals(null, getNextToken(""" // test
			"""))
		assertEquals(Boolean, getNextToken("\nfalse"))
		assertEquals(Boolean, getNextToken("/* ignor * 3 */false"))
		assertEquals(Number, getNextToken("1 + 2"))
		assertEquals(null, getNextToken("/* test comment * asdf */"))
		assertEquals(Number, getNextToken("/* //wtf */1"))
		assertEquals(Boolean, getNextToken("""
			// 1 //wtf */ "nice"
			true
		""".trimIndent()))
	}
	
	@Test
	fun testCorrectNextType() {
		assertEquals(null, getNextToken(""))
		assertEquals(null, getNextToken(" "))
		assertEquals(Boolean, getNextToken("true"))
		assertEquals(Boolean, getNextToken("false"))
		assertEquals(AdditiveOperator, getNextToken("-"))
		assertEquals(Assign, getNextToken("="))
		assertEquals(Comma, getNextToken(","))
		assertEquals(RB, getNextToken(")"))
		assertEquals(LB, getNextToken("("))
		assertEquals(Negate, getNextToken("!"))
		assertEquals(Comparison, getNextToken("<="))
		assertEquals(LogicalOr, getNextToken("||"))
		assertEquals(LogicalAnd, getNextToken("&&"))
		assertEquals(Number, getNextToken("123"))
		assertEquals(AdditiveOperator, getNextToken("-123"))
		assertEquals(TokenType.String, getNextToken("\"hello\""))
		assertEquals(Identifier, getNextToken("a1"))
		assertEquals(Identifier, getNextToken("_a1"))
		assertEquals(MultiplicativeOperator, getNextToken("*"))
		assertEquals(MultiplicativeOperator, getNextToken("/"))
		assertEquals(Potency, getNextToken("^"))
		
	}
	
	private fun getNextToken(str: String): TokenType? {
		val (token, _) = Tokenizer.getNextToken(str, 0) ?: (null to 0)
		return token?.type
	}
	
	private fun tokenize(str: String): List<TokenType> {
		return Tokenizer.tokenize(str).map { it.type }
	}
}
