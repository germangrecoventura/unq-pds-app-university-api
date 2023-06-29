package unq.pds.arch

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Service

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArchUnitTest {
    private lateinit var classesUnderTest: JavaClasses

    @BeforeEach
    fun setUp() {
        val basePackage = "unq.pds"
        classesUnderTest = ClassFileImporter().importPackages(basePackage)
    }

    @Test
    fun `should be true that test class names end with test`() {
        classes().that().resideInAPackage("..")
            .and().areAnnotatedWith(SpringBootTest::class.java)
            .should().haveSimpleNameEndingWith("Test")
            .check(classesUnderTest)
    }

    @Test
    fun `should be true that the names of the test methods begin with should`() {
        methods().that().areDeclaredInClassesThat().resideInAPackage("..")
            .and().areAnnotatedWith(Test::class.java)
            .should().haveNameStartingWith("should")
            .check(classesUnderTest)
    }

    @Test
    fun `should be true that class names with the annotation service end with Impl`() {
        classes().that().resideInAPackage("..services.impl..")
            .and().areAnnotatedWith(Service::class.java)
            .should().haveSimpleNameEndingWith("Impl")
            .check(classesUnderTest)
    }
}