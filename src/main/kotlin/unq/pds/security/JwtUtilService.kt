package unq.pds.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.HashMap

@Component
class JwtUtilService {
    fun generateToken(authentication: Authentication): String {
        val claim = HashMap<String,Any>()
        claim["role"] = authentication.authorities.stream().map(GrantedAuthority::getAuthority).toList()
        return Jwts
            .builder()
            .setSubject(authentication.name)
            .addClaims(claim)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
            .signWith(SignatureAlgorithm.HS512, JWT_SECRET_KEY)
            .compact()
    }

    fun extractUsername(token: String): String {
        val claims = Jwts.parser().setSigningKey(JWT_SECRET_KEY).parseClaimsJws(token).body
        return claims.subject
    }

    fun isTokenValid(token: String): Boolean {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET_KEY).parseClaimsJws(token)
            return true
        } catch (e: Exception) {
            throw AuthenticationCredentialsNotFoundException("JWT expired or is incorrect")
        }
    }


    companion object {
        private const val JWT_SECRET_KEY = "TExBVkVfTVVZX1NFQ1JFVEE="
        const val JWT_TOKEN_VALIDITY = 86400000L
    }
}