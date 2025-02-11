package ch.judos.service

import ch.judos.model.TokenPatterns
import ch.judos.model.TokenType

object Tokenizer {
	
	fun tokenize(str: String): List<TokenType> {
		val result = mutableListOf<TokenType>()
		var cursor = 0
		while (cursor < str.length) {
			val (token, newCursor) = getNextToken(str, cursor) ?: continue
			result.add(token)
			cursor = newCursor
		}
		return result
	}
	
	fun getNextToken(str: String, cursor_: Int): Pair<TokenType, Int>? {
		var cursor = cursor_
		wh@ while (cursor < str.length){
			for ((pattern, type) in TokenPatterns) {
				val match = "^$pattern".toRegex().find(str.substring(cursor))
				if (match != null && type != null) {
					return Pair(type, cursor + match.range.last + 1)
				}
				if (match != null) {
					cursor += match.range.last + 1
					continue@wh
				}
			}
			throw Exception("Unknown token at position $cursor \"${str.substring(cursor, (cursor+5).coerceAtMost(str.length))}\"")
		}
		return null
	}
	
}
