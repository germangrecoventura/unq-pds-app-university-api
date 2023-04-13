package unq.pds.model.exceptions

class AlreadyRegisteredException(private val entity: String) : RuntimeException() {
    override val message: String
        get() = "The $entity is already registered"
}