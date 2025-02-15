package ch.judos.endpoints

import jakarta.annotation.security.DenyAll
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.InternalServerErrorException
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.jwt.Claim
import org.eclipse.microprofile.jwt.Claims
import org.eclipse.microprofile.jwt.JsonWebToken

@Path("/secured")
@RequestScoped
class TokenSecuredResource {
	@Inject
	lateinit var jwt: JsonWebToken
	@Inject
	@Claim(standard = Claims.birthdate)
	lateinit var birthdate: String
	
	@GET
	@Path("permit-all")
	@PermitAll
	@Produces(MediaType.TEXT_PLAIN)
	fun hello(@Context ctx: SecurityContext): String {
		return getResponseString(ctx)
	}
	
	@GET
	@Path("roles-allowed")
	@RolesAllowed("User", "Admin")
	@Produces(MediaType.TEXT_PLAIN)
	fun helloRolesAllowed(@Context ctx: SecurityContext): String {
		return getResponseString(ctx) + ", birthdate: " + jwt.getClaim<Any>("birthdate").toString()
	}
	
	@GET
	@Path("roles-allowed-admin")
	@RolesAllowed("Admin")
	@Produces(MediaType.TEXT_PLAIN)
	fun helloRolesAllowedAdmin(@Context ctx: SecurityContext): String {
		return getResponseString(ctx) + ", birthdate: " + birthdate
	}
	
	@GET
	@Path("deny-all")
	@DenyAll
	@Produces(MediaType.TEXT_PLAIN)
	fun helloShouldDeny(@Context ctx: SecurityContext?): String {
		throw InternalServerErrorException("This method must not be invoked")
	}
	
	private fun getResponseString(ctx: SecurityContext): String {
		val name = if (ctx.userPrincipal == null) {
			"anonymous"
		}
		else if (ctx.userPrincipal.name != jwt.name) {
			throw InternalServerErrorException("Principal and JsonWebToken names do not match")
		}
		else {
			ctx.userPrincipal.name
		}
		return String.format(("hello + %s,"
				+ " isHttps: %s,"
				+ " authScheme: %s,"
				+ " hasJWT: %s"),
			name, ctx.isSecure, ctx.authenticationScheme, hasJwt())
	}
	
	private fun hasJwt(): Boolean {
		return jwt.claimNames != null
	}
}
