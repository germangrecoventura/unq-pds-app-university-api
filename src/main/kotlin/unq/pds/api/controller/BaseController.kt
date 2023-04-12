package unq.pds.api.controller

import org.springframework.http.*
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.*
import org.springframework.web.bind.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import unq.pds.api.dtos.ErrorDTO
import unq.pds.model.exceptions.EmailAlreadyRegisteredException
import unq.pds.model.exceptions.MatterNameAlreadyRegisteredException
import java.util.function.Consumer
import javax.management.InvalidAttributeValueException

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

    @ExceptionHandler(EmailAlreadyRegisteredException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleEmailAddressAlreadyRegisteredException(ex: EmailAlreadyRegisteredException): ResponseEntity<ErrorDTO> {
        return ResponseEntity.badRequest().body(ErrorDTO(ex.message))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<ErrorDTO> {
        val messageTotal = ex.message!!.split("problem")
        var finalMessage = messageTotal[1].substring(2, 50)
        while (finalMessage.contains(";".single())) {
            finalMessage = finalMessage.dropLast(1)
        }
        return ResponseEntity.badRequest().body(ErrorDTO(finalMessage))
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingParameterException(ex: MissingServletRequestParameterException): ResponseEntity<ErrorDTO> {
        return ResponseEntity.badRequest().body(ErrorDTO(ex.message!!))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatchException(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorDTO> {
        val errorMessage = "Required request parameter '" + ex.name + "' for method parameter type " + ex.requiredType + " is not present"
        return ResponseEntity.badRequest().body(ErrorDTO(errorMessage))
    }

    @ExceptionHandler(MatterNameAlreadyRegisteredException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMatterNameAlreadyRegisteredException(ex: MatterNameAlreadyRegisteredException): ResponseEntity<ErrorDTO> {
        return ResponseEntity.badRequest().body(ErrorDTO(ex.message))
    }

    @ExceptionHandler(InvalidAttributeValueException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidAttributeValueException(ex: InvalidAttributeValueException): ResponseEntity<ErrorDTO> {
        return ResponseEntity.badRequest().body(ErrorDTO(ex.message!!))
    }
}