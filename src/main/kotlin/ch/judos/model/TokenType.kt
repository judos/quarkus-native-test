package ch.judos.model

enum class TokenType {
	
	Number, // -999, 0, 1, 999, any size numbers with optional -
	String, // "example"
	Boolean, // true, false
	AdditiveOperator, // +, -
	MultiplicativeOperator, // *, /, %
	Potency, // ^
	Comma,      // ,
	Assign,     // =
	RB, // right bracket
	LB, // left bracket
	Comparison,      // ==, !=, >=, <=, >, <
	LogicalOr, // ||
	LogicalAnd, // &&
	Negate, // !
	Identifier, // asdf
}
