package ch.judos.model

import ch.judos.service.Parser
import ch.judos.service.Tokenizer
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.ws.rs.NotFoundException

@RequestScoped
class ResourceEvaluationEnvironment : BasicEvaluationEnvironment() {
	
	@Inject
	lateinit var em: EntityManager
	
	override fun evaluateFunction(name: String, arguments: List<Any>): Any {
		try {
			return super.evaluateFunction(name, arguments)
		} catch (e: NotFoundException) {
			// find formula with name in database
			val formula = em.createQuery("SELECT f FROM Formula f WHERE f.name = :name",
				Formula::class.java).setParameter("name", name.lowercase()).resultList.firstOrNull()
				?: throw NotFoundException("Function $name not found")
			val functionDefinition = Parser(Tokenizer.tokenize(formula.formula)).parseFunction()
			saveFunction(functionDefinition)
			return super.evaluateFunction(name, arguments)
		}
	}
}
