package unq.pds.model

class CommissionTest {
    /*
        @Test
        fun `should throw an exception when the year of the commission is less than 2000`() {
            try {
                aCommission().withYear(1999).build()
            } catch (e: InvalidAttributeValueException) {
                Assertions.assertEquals("Year should be greater than or equal to 2000", e.message)
            }
        }

        @Test
        fun `should add a student when the student was not previously added`() {
            val commission = aCommission().build()
            Assertions.assertEquals(0, commission.students.size)
            commission.addStudent(aStudent().build())
            Assertions.assertEquals(1, commission.students.size)
        }

        @Test
        fun `should throw an exception when trying to add the same student to the commission twice`() {
            val commission = aCommission().build()
            commission.addStudent(aStudent().build())
            try {
                commission.addStudent(aStudent().build())
            } catch (e: CloneNotSupportedException) {
                Assertions.assertEquals("The student is already in the commission", e.message)
            }
        }

        @Test
        fun `should remove a student when it has been previously added`() {
            val commission = aCommission().build()
            val student = aStudent().build()
            Assertions.assertEquals(0, commission.students.size)
            commission.addStudent(student)
            Assertions.assertEquals(1, commission.students.size)
            commission.removeStudent(student)
            Assertions.assertEquals(0, commission.students.size)
        }

        @Test
        fun `should throw an exception when trying to remove a student who does not belong to a commission`() {
            val commission = aCommission().build()
            try {
                commission.removeStudent(aStudent().build())
            } catch (e: NoSuchElementException) {
                Assertions.assertEquals("The student is not in the commission", e.message)
            }
        }

        @Test
        fun `should add a teacher when the teacher was not previously added`() {
            val commission = aCommission().build()
            Assertions.assertEquals(0, commission.teachers.size)
            commission.addTeacher(aTeacher().build())
            Assertions.assertEquals(1, commission.teachers.size)
        }

        @Test
        fun `should throw an exception when trying to add the same teacher to the commission twice`() {
            val commission = aCommission().build()
            commission.addTeacher(aTeacher().build())
            try {
                commission.addTeacher(aTeacher().build())
            } catch (e: CloneNotSupportedException) {
                Assertions.assertEquals("The teacher is already in the commission", e.message)
            }
        }

        @Test
        fun `should remove a teacher when it has been previously added`() {
            val commission = aCommission().build()
            val teacher = aTeacher().build()
            Assertions.assertEquals(0, commission.teachers.size)
            commission.addTeacher(teacher)
            Assertions.assertEquals(1, commission.teachers.size)
            commission.removeTeacher(teacher)
            Assertions.assertEquals(0, commission.teachers.size)
        }

        @Test
        fun `should throw an exception when trying to remove a teacher who does not belong to a commission`() {
            val commission = aCommission().build()
            try {
                commission.removeTeacher(aTeacher().build())
            } catch (e: NoSuchElementException) {
                Assertions.assertEquals("The teacher is not in the commission", e.message)
            }
        }

        @Test
        fun `should add a group when the group was not previously added`() {
            val commission = aCommission().build()
            Assertions.assertEquals(0, commission.groupsStudents.size)
            commission.addGroup(aGroup().build())
            Assertions.assertEquals(1, commission.groupsStudents.size)
        }

        @Test
        fun `should throw an exception when trying to add the same group to the commission twice`() {
            val commission = aCommission().build()
            commission.addGroup(aGroup().build())
            try {
                commission.addGroup(aGroup().build())
            } catch (e: CloneNotSupportedException) {
                Assertions.assertEquals("The group is already in the commission", e.message)
            }
        }

        @Test
        fun `should remove a group when it has been previously added`() {
            val commission = aCommission().build()
            val group = aGroup().build()
            Assertions.assertEquals(0, commission.groupsStudents.size)
            commission.addGroup(group)
            Assertions.assertEquals(1, commission.groupsStudents.size)
            commission.removeGroup(group)
            Assertions.assertEquals(0, commission.groupsStudents.size)
        }

        @Test
        fun `should throw an exception when trying to remove a group who does not belong to a commission`() {
            val commission = aCommission().build()
            try {
                commission.removeGroup(aGroup().build())
            } catch (e: NoSuchElementException) {
                Assertions.assertEquals("The group is not in the commission", e.message)
            }
        }*/
}