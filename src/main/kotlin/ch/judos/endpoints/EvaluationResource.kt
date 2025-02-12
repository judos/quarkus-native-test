package ch.judos.endpoints

import ch.judos.model.EvaluationEnvironment
import ch.judos.model.Formula
import ch.judos.model.ResourceEvaluationEnvironment
import ch.judos.service.Parser
import ch.judos.service.Tokenizer
import jakarta.annotation.security.PermitAll
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.UriInfo

@Path("/evaluate")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
class EvaluationResource {
	
	@Inject
	lateinit var resourceEvaluationEnvironment: ResourceEvaluationEnvironment
	
	@GET
	fun evaluate(@Context uriInfo: UriInfo, @QueryParam("evaluate") evaluate: String): Any {
		
		val env = EvaluationEnvironment(resourceEvaluationEnvironment)
		uriInfo.queryParameters.filter { it.key != "evaluate" }.forEach { (key, value) ->
			env.variables[key] = value.first().toDouble()
		}
		
		val expr = Parser(Tokenizer.tokenize(evaluate)).parseExpression()
		val result = expr.evaluate(env)
		return result
	}
	
}
