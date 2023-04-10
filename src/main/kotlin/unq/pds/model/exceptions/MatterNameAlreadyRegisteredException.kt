package unq.pds.model.exceptions

class MatterNameAlreadyRegisteredException : RuntimeException() {
    override val message: String
        get() = "The matter name is already registered"
}