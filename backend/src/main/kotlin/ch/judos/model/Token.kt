package ch.judos.model

class Token(
	val type: TokenType,
	val value: String,
) {
	override fun toString(): String {
		return "$type: $value"
	}
}
