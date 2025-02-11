package ch.judos.model

val TokenPatterns = mapOf(
	"""\s+""" to null, // Whitespace
	"true|false" to TokenType.Boolean,
	"[+-]" to TokenType.AdditiveOperator,
	"=" to TokenType.Assign,
	"," to TokenType.Comma,
	"""\(""" to TokenType.RB,
	"""\)""" to TokenType.LB,
	"!" to TokenType.Negate,
	"==|!=|>=|<=|>|<" to TokenType.Comparison,
	"""&&|\|\|""" to TokenType.LogicalOperator,
	"""\d+""" to TokenType.Number,
	""""[^"]*"""" to TokenType.String,
	"""[a-zA-Z_]\w*""" to TokenType.Identifier,
	"""\/\*(\*(?!\/)|[^*])*\*\/""" to null, // Block comment
	"""\/\/[^\n\r]*[\n\r]|$""" to null, // Line comment
	"[*/%]" to TokenType.MultiplicativeOperator,
)
