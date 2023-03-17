package api.dtos

import io.javalin.core.validation.ValidationError

data class ErrorDTO(val message: String) {
    companion object {
        fun fromErrors(errors: List<ValidationError<Any>>): ErrorDTO {
            val messages = errors.map { it.message }
            val message = messages.joinToString(", ")
            return ErrorDTO(message)
        }
    }
}
