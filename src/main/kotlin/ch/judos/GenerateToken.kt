package ch.judos

import io.smallrye.jwt.build.Jwt
import org.eclipse.microprofile.jwt.Claims

/**
 * A simple utility class to generate and print a JWT token string to stdout.
 */
object GenerateToken {
	/**
	 * Generate JWT token
	 */
	@JvmStatic
	fun main(args: Array<String>) {
		val token = Jwt.issuer("https://example.com/issuer")
			.upn("jdoe@quarkus.io")
			.groups(HashSet(mutableListOf("Test")))
			.claim(Claims.birthdate.name, "1989-10-01")
			.sign()
		println(token)
	}
}
