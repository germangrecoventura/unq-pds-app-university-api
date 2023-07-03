package unq.pds.api.controller

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import unq.pds.api.dtos.MessageDTO
import unq.pds.security.JwtUtilService
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest

open class ControllerHelper {

    private val logger = LoggerFactory.getLogger(ControllerHelper::class.java)

    protected val messageNotAuthenticated = MessageDTO("It is not authenticated. Please log in")
    protected val messageNotAccess = MessageDTO("You do not have permissions to access this resource")
    private val passwordEncrypt = JwtUtilService.JWT_SECRET_KEY
    private lateinit var currentHeader: String

    protected fun jwtDoesNotExistInTheHeader(request: HttpServletRequest): Boolean {
        currentHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        return !existJWT(currentHeader)
    }

    protected fun bodyOfTheCurrentHeader(): Claims {
        val header = currentHeader.substring(7, currentHeader.length)
        return Jwts.parser().setSigningKey(passwordEncrypt).parseClaimsJws(header).body
    }

    private fun existJWT(jwt: String?): Boolean {
        return StringUtils.hasText(jwt) &&
                jwt!!.startsWith("Bearer ")
                && !jwt.substring(7, jwt.length).isNullOrEmpty()
    }

    @Suppress("UNCHECKED_CAST")
    protected fun isNotAdmin(body: Claims): Boolean {
        val role = body["role"] as List<String>
        return !role.contains("ADMIN")
    }

    @Suppress("UNCHECKED_CAST")
    protected fun isStudent(body: Claims): Boolean {
        val role = body["role"] as List<String>
        return role.contains("STUDENT")
    }

    @Suppress("UNCHECKED_CAST")
    protected fun isTeacher(body: Claims): Boolean {
        val role = body["role"] as List<String>
        return role.contains("TEACHER")
    }

    protected fun showLogger(method: String, arguments: List<String>) {
        val authentication = SecurityContextHolder.getContext().authentication
        val userName = authentication.name
        val timeStamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val customizedArguments = getArguments(arguments)

        logger.info(
            "\n ///////// \n {} \n User: {} \n Method: {} \n with parameters: \n{} /////////",
            timeStamp,
            userName,
            method,
            customizedArguments
        )
    }

    private fun getArguments(arguments: List<String>): String {
        val sb = StringBuilder()

        arguments.forEach { argument ->
            sb.append("  $argument \n")
        }

        return sb.toString()
    }
}