package unq.pds.api.controller

import org.springframework.http.*
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.*
import org.springframework.web.bind.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import unq.pds.api.dtos.MessageDTO
import unq.pds.model.exceptions.AlreadyRegisteredException
import unq.pds.model.exceptions.ProjectAlreadyHasAnOwnerException
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

    @ExceptionHandler(AlreadyRegisteredException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleAlreadyRegisteredException(ex: AlreadyRegisteredException): ResponseEntity<MessageDTO> {
        return ResponseEntity.badRequest().body(MessageDTO(ex.message))
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
        val errorMessage = "Required request parameter '" + ex.name + "' for method parameter type " + ex.requiredType + " is not present"
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

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<MessageDTO> {
        return ResponseEntity(MessageDTO(ex.message!!), HttpStatus.NOT_FOUND)
    }
}