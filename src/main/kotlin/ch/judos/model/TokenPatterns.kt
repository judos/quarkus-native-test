package ch.judos.model

val TokenPatterns = mapOf(
	"""\s+""" to null, // Whitespace
	"true|false" to TokenType.Boolean,
	"->" to TokenType.Arrow,
	"[+-]" to TokenType.AdditiveOperator,
	"=" to TokenType.Assign,
	"," to TokenType.Comma,
	"""\(""" to TokenType.LB,
	"""\)""" to TokenType.RB,
	"""\{""" to TokenType.LC,
	"""\}""" to TokenType.RC,
	"!" to TokenType.Negate,
	"""\^""" to TokenType.Potency,
	"==|!=|>=|<=|>|<" to TokenType.Comparison,
	"""&&""" to TokenType.LogicalAnd,
	"""\|\|""" to TokenType.LogicalOr,
	"""\d+(\.\d+)?""" to TokenType.Number,
	""""[^"]*"""" to TokenType.String,
	"""[a-zA-Z_]\w*""" to TokenType.Identifier,
	"""\/\*(\*(?!\/)|[^*])*\*\/""" to null, // Block comment
	"""\/\/[^\n\r]*([\n\r]|$)""" to null, // Line comment
	"[*/%]" to TokenType.MultiplicativeOperator,
)
