package ch.judos.endpoints

import ch.judos.GiftService
import jakarta.annotation.security.PermitAll
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.jwt.Claim
import org.eclipse.microprofile.jwt.Claims

@Path("/gift")
@RequestScoped
class GiftResource {
	@Inject
	@Claim(standard = Claims.birthdate)
	var birthdate: String? = null
	
	@Inject
	lateinit var giftService: GiftService
	
	@GET
	@Path("add")
	@PermitAll
	@Produces(MediaType.TEXT_PLAIN)
	fun add(@Context ctx: SecurityContext) {
		giftService.createGift("Birthday gift $birthdate")
	}
}
