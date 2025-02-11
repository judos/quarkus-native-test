package ch.judos.model

enum class TokenType {
	
	Number, // -999, 0, 1, 999, any size numbers with optional -
	String, // "example"
	Boolean, // true, false
	AdditiveOperator, // +, -
	MultiplicativeOperator, // *, /, %
	Comma,      // ,
	Assign,     // =
	RB, // right bracket
	LB, // left bracket
	Comparison,      // ==, !=, >=, <=, >, <
	LogicalOperator, // &&, ||
	Negate, // !
	Identifier, // asdf
}
