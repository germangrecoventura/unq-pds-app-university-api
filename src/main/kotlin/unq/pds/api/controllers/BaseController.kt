package unq.pds.api.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.function.Consumer

@RestControllerAdvice
class BaseController {
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
        MethodArgumentNotValidException::class
    )
    fun handleValidateException(ex: MethodArgumentNotValidException): MutableMap<String, String?> {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.forEach(Consumer { error: ObjectError ->
            val fieldName = (error as FieldError).field
            val message = error.getDefaultMessage()
            errors[fieldName] = message
        })
        return errors
    }

    @ExceptionHandler(EmailAlreadyRegisteredException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleEmailAddressAlreadyRegisteredException(): ResponseEntity<*> {
        return ResponseEntity.badRequest().body(
            "{\n" +
                    "  \"email\": \"The email is already registered\"\n" +
                    "}"
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<*> {
        var messageTotal = ex.message!!.split("problem")
        var finalMessage = messageTotal[1].substring(2, 50)
        while (finalMessage.contains(";".single())) {
            finalMessage = finalMessage.dropLast(1)
        }
        return ResponseEntity.badRequest().body(
            "{\n" +
                    "  \"message\": \"${finalMessage}\"\n" +
                    "}"
        )
    }
}

class EmailAlreadyRegisteredException : RuntimeException() {
    override val message: String?
        get() = "The email is already registered"
}