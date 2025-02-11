package ch.judos.endpoints

import ch.judos.model.Formula
import jakarta.annotation.security.PermitAll
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType

@Path("/formula")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
class FormulaResource {
	
	@Inject
	lateinit var em: EntityManager
	
	@PUT
	@Path("")
	@Transactional
	fun create(formula: Formula): Long {
		em.persist(formula)
		return formula.id
	}
	
	@GET
	@Path("{id}")
	fun get(@PathParam("id") id: Long): Formula {
		return em.find(Formula::class.java, id)
			?: throw NotFoundException("Object not found")
	}
	
	@GET
	@Path("")
	fun getAll(): List<Formula> {
		return em.createQuery("SELECT f FROM Formula f", Formula::class.java).resultList
	}
	
	@DELETE
	@Path("{id}")
	@Transactional
	fun delete(id: Long) {
		em.find(Formula::class.java, id)?.let { em.remove(it) }
			?: throw NotFoundException("Object not found")
	}
	
	@POST
	@Path("")
	@Transactional
	fun update(formula: Formula) {
		if (formula.id == 0L) {
			throw BadRequestException("ID must be set")
		}
		em.merge(formula)
	}
	
}
