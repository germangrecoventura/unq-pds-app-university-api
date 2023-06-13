package unq.pds.model.exceptions

class GroupWithEmptyMemberException : RuntimeException() {
    override val message: String
        get() = "The group cannot run out of students"
}