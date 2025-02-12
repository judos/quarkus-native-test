package ch.judos.model

import kotlin.reflect.KClass

enum class ExpressionType(val kClass: KClass<*>) {
	String(kotlin.String::class),
	Boolean(kotlin.Boolean::class),
	Double(kotlin.Double::class);
	
	fun isMatch(value: Any): kotlin.Boolean {
		return kClass.isInstance(value)
	}
}
