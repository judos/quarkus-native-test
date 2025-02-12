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
		assertEquals(TokenType.Boolean, getNextToken("\nfalse"))
		assertEquals(TokenType.Boolean, getNextToken("/* ignor * 3 */false"))
		assertEquals(Number, getNextToken("1 + 2"))
		assertEquals(null, getNextToken("/* test comment * asdf */"))
		assertEquals(Number, getNextToken("/* //wtf */1"))
		assertEquals(TokenType.Boolean, getNextToken("""
			// 1 //wtf */ "nice"
			true
		""".trimIndent()))
	}
	
	@Test
	fun testCorrectNextType() {
		assertEquals(null, getNextToken(""))
		assertEquals(null, getNextToken(" "))
		assertEquals(TokenType.Boolean, getNextToken("true"))
		assertEquals(TokenType.Boolean, getNextToken("false"))
		assertEquals(TokenType.AdditiveOperator, getNextToken("-"))
		assertEquals(TokenType.Assign, getNextToken("="))
		assertEquals(TokenType.Comma, getNextToken(","))
		assertEquals(TokenType.RB, getNextToken(")"))
		assertEquals(TokenType.LB, getNextToken("("))
		assertEquals(TokenType.Negate, getNextToken("!"))
		assertEquals(TokenType.Comparison, getNextToken("<="))
		assertEquals(TokenType.LogicalOr, getNextToken("||"))
		assertEquals(TokenType.LogicalAnd, getNextToken("&&"))
		assertEquals(Number, getNextToken("123"))
		assertEquals(TokenType.AdditiveOperator, getNextToken("-123"))
		assertEquals(TokenType.String, getNextToken("\"hello\""))
		assertEquals(TokenType.Identifier, getNextToken("a1"))
		assertEquals(TokenType.Identifier, getNextToken("_a1"))
		assertEquals(TokenType.MultiplicativeOperator, getNextToken("*"))
		assertEquals(TokenType.MultiplicativeOperator, getNextToken("/"))
		assertEquals(TokenType.Potency, getNextToken("^"))
		
	}
	
	fun getNextToken(str: String): TokenType? {
		val (token, _) = Tokenizer.getNextToken(str, 0) ?: (null to 0)
		return token?.type
	}
	
	fun tokenize(str: String): List<TokenType> {
		return Tokenizer.tokenize(str).map { it.type }
	}
}
