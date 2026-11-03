package homework.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StudentTest {

    @Test
    void createsStudentWithValidValues() {
        Student student = new Student(1, "Anna", 7.5);

        assertEquals(1, student.id());
        assertEquals("Anna", student.name());
        assertEquals(7.5, student.grade());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 10.0})
    void acceptsBoundaryGrades(double grade) {
        Student student = new Student(1, "Anna", grade);

        assertEquals(grade, student.grade());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 10.1, Double.NaN})
    void rejectsInvalidGrade(double grade) {
        assertThrows(IllegalArgumentException.class, () -> new Student(1, "Anna", grade));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejectsNonPositiveId(int id) {
        assertThrows(IllegalArgumentException.class, () -> new Student(id, "Anna", 7.5));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  \t"})
    void rejectsNullOrBlankName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new Student(1, name, 7.5));
    }

    @Test
    void equalStudentsHaveSameHashCode() {
        Student first = new Student(1, "Anna", 7.5);
        Student second = new Student(1, "Anna", 7.5);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @ParameterizedTest
    @MethodSource("studentsDifferentFromAnna")
    void differentStudentsAreNotEqual(Student other) {
        Student anna = new Student(1, "Anna", 7.5);

        assertNotEquals(anna, other);
    }

    static List<Student> studentsDifferentFromAnna() {
        return List.of(
                new Student(2, "Anna", 7.5),
                new Student(1, "Boris", 7.5),
                new Student(1, "Anna", 8.0));
    }
}
