package ch.judos.model

enum class TokenType {
	
	Number, // 999, 0, 1, 999, any size numbers
	String, // "example"
	Boolean, // true, false
	Arrow, // ->
	AdditiveOperator, // +, -
	MultiplicativeOperator, // *, /, %
	Potency, // ^
	Comma,      // ,
	Assign,     // =
	RB, // right bracket
	LB, // left bracket
	LC, // left curly bracket
	RC, // right curly bracket
	Comparison,      // ==, !=, >=, <=, >, <
	LogicalOr, // ||
	LogicalAnd, // &&
	Negate, // !
	Identifier, // asdf
}
