package unq.pds.api.controller

import io.jsonwebtoken.ExpiredJwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import unq.pds.api.dtos.MessageDTO
import unq.pds.model.exceptions.*
import java.sql.SQLIntegrityConstraintViolationException
import java.util.function.Consumer
import javax.management.InvalidAttributeValueException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RestControllerAdvice
class BaseController {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): MutableMap<String, String?> {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.forEach(Consumer { error: ObjectError ->
            val fieldName = (error as FieldError).field
            val message = error.getDefaultMessage()
            errors[fieldName] = message
        })
        return errors
    }

    @ExceptionHandler(AlreadyRegisteredException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleAlreadyRegisteredException(ex: AlreadyRegisteredException): ResponseEntity<MessageDTO> {
        return ResponseEntity.badRequest().body(MessageDTO(ex.message))
    }

    @ExceptionHandler(GroupWithEmptyMemberException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleGroupWithEmptyMemberExceptionException(ex: GroupWithEmptyMemberException): ResponseEntity<MessageDTO> {
        return ResponseEntity.badRequest().body(MessageDTO(ex.message))
    }

    @ExceptionHandler(StudentsOfTheGroupNotEnrolledInTheCommissionException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleStudentsOfTheGroupNotEnrolledInTheCommissionExceptionException(ex: StudentsOfTheGroupNotEnrolledInTheCommissionException): ResponseEntity<MessageDTO> {
        return ResponseEntity.badRequest().body(MessageDTO(ex.message))
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun userNotFound(ex: UsernameNotFoundException): ResponseEntity<MessageDTO> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageDTO(ex.message!!))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<MessageDTO> {
        val messageTotal = ex.message!!.split("problem")
        var finalMessage = messageTotal[1].substring(2, 50)
        while (finalMessage.contains(";".single())) {
            finalMessage = finalMessage.dropLast(1)
        }
        return ResponseEntity.badRequest().body(MessageDTO(finalMessage))
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingParameterException(ex: MissingServletRequestParameterException): ResponseEntity<MessageDTO> {
        return ResponseEntity.badRequest().body(MessageDTO(ex.message!!))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatchException(ex: MethodArgumentTypeMismatchException): ResponseEntity<MessageDTO> {
        val errorMessage =
            "Required request parameter '" + ex.name + "' for method parameter type " + ex.requiredType + " is not present"
        return ResponseEntity.badRequest().body(MessageDTO(errorMessage))
    }

    @ExceptionHandler(InvalidAttributeValueException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidAttributeValueException(ex: InvalidAttributeValueException): ResponseEntity<MessageDTO> {
        return ResponseEntity.badRequest().body(MessageDTO(ex.message!!))
    }

    @ExceptionHandler(CloneNotSupportedException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleCloneNotSupportedException(ex: CloneNotSupportedException): ResponseEntity<MessageDTO> {
        return ResponseEntity.badRequest().body(MessageDTO(ex.message!!))
    }

    @ExceptionHandler(ProjectAlreadyHasAnOwnerException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleProjectAlreadyHasAnOwnerException(ex: ProjectAlreadyHasAnOwnerException): ResponseEntity<MessageDTO> {
        return ResponseEntity.badRequest().body(MessageDTO(ex.message))
    }

    @ExceptionHandler(RepositoryHasAlreadyBeenAddedException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRepositoryHasAlreadyBeenAddedException(ex: RepositoryHasAlreadyBeenAddedException): ResponseEntity<MessageDTO> {
        return ResponseEntity.badRequest().body(MessageDTO(ex.message))
    }

    @ExceptionHandler(NotAuthenticatedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleNotAuthenticatedException(ex: NotAuthenticatedException): ResponseEntity<MessageDTO> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MessageDTO(ex.message))
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleSQLIntegrityConstraintViolationException(): ResponseEntity<MessageDTO> {
        return ResponseEntity.badRequest()
            .body(MessageDTO("The entity cannot be deleted because it is related to another entity"))
    }

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<MessageDTO> {
        return ResponseEntity(MessageDTO(ex.message!!), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ExpiredJwtException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleExpiredJwtException(response: HttpServletResponse): ResponseEntity<MessageDTO> {
        val cookie = Cookie("jwt", null)
        cookie.isHttpOnly = true
        cookie.maxAge = 0
        response.addCookie(cookie)
        return ResponseEntity(MessageDTO("Your token expired"), HttpStatus.UNAUTHORIZED)
    }
}