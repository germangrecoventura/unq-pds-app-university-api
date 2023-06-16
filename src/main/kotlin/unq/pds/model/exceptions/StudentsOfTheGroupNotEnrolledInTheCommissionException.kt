package unq.pds.model.exceptions

class StudentsOfTheGroupNotEnrolledInTheCommissionException : RuntimeException() {
    override val message: String
        get() = "There are students who are not enrolled in the commission"
}