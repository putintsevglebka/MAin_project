package homework.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class StudentRegistryTest {

    private static final Student ANNA = new Student(1, "Anna", 4.0);
    private static final Student BORIS = new Student(2, "Boris", 6.5);
    private static final Student VERA = new Student(3, "Vera", 8.0);
    private static final Student DMITRY = new Student(4, "Dmitry", 10.0);
    private static final Student EVA = new Student(5, "Eva", 0.0);

    private static final List<Student> ALL = List.of(BORIS, EVA, ANNA, DMITRY, VERA);
    private static final List<Student> ALL_BY_DESCENDING_ID = List.of(EVA, DMITRY, VERA, BORIS, ANNA);

    static List<Map<Integer, Student>> maps() {
        return List.of(
                StudentRegistry.createHashMap(ALL),
                StudentRegistry.createDescendingTreeMap(ALL));
    }

    private static void assertSameStudents(Set<Student> expected, List<Student> actual) {
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, new HashSet<>(actual));
    }

    @Test
    void createHashMapUsesIdAsKey() {
        HashMap<Integer, Student> map = StudentRegistry.createHashMap(ALL);

        assertEquals(ALL.size(), map.size());
        for (Student student : ALL) {
            assertEquals(student, map.get(student.id()));
        }
    }

    @Test
    void createDescendingTreeMapUsesIdAsKey() {
        TreeMap<Integer, Student> map = StudentRegistry.createDescendingTreeMap(ALL);

        assertEquals(ALL.size(), map.size());
        for (Student student : ALL) {
            assertEquals(student, map.get(student.id()));
        }
    }

    @Test
    void createDescendingTreeMapIteratesInDescendingIdOrder() {
        TreeMap<Integer, Student> map = StudentRegistry.createDescendingTreeMap(ALL);

        assertEquals(List.of(5, 4, 3, 2, 1), new ArrayList<>(map.keySet()));
        assertEquals(ALL_BY_DESCENDING_ID, new ArrayList<>(map.values()));
    }

    @Test
    void createHashMapRejectsDuplicateId() {
        List<Student> students = List.of(ANNA, new Student(1, "Other", 5.0));

        assertThrows(IllegalArgumentException.class, () -> StudentRegistry.createHashMap(students));
    }

    @Test
    void createDescendingTreeMapRejectsDuplicateId() {
        List<Student> students = List.of(ANNA, new Student(1, "Other", 5.0));

        assertThrows(IllegalArgumentException.class,
                () -> StudentRegistry.createDescendingTreeMap(students));
    }

    @Test
    void createHashMapRejectsNullCollection() {
        assertThrows(NullPointerException.class, () -> StudentRegistry.createHashMap(null));
    }

    @Test
    void createDescendingTreeMapRejectsNullCollection() {
        assertThrows(NullPointerException.class, () -> StudentRegistry.createDescendingTreeMap(null));
    }

    @Test
    void createHashMapRejectsNullElement() {
        Collection<Student> students = Arrays.asList(ANNA, null);

        assertThrows(NullPointerException.class, () -> StudentRegistry.createHashMap(students));
    }

    @Test
    void createDescendingTreeMapRejectsNullElement() {
        Collection<Student> students = Arrays.asList(ANNA, null);

        assertThrows(NullPointerException.class,
                () -> StudentRegistry.createDescendingTreeMap(students));
    }

    @ParameterizedTest
    @MethodSource("maps")
    void findStudentsByGradeRangeReturnsStudentsInsideRange(Map<Integer, Student> students) {
        List<Student> result = StudentRegistry.findStudentsByGradeRange(students, 5.0, 9.0);

        assertSameStudents(Set.of(BORIS, VERA), result);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void findStudentsByGradeRangeIncludesBothBounds(Map<Integer, Student> students) {
        List<Student> result = StudentRegistry.findStudentsByGradeRange(students, 4.0, 8.0);

        assertSameStudents(Set.of(ANNA, BORIS, VERA), result);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void findStudentsByGradeRangeWorksWhenMinEqualsMax(Map<Integer, Student> students) {
        List<Student> result = StudentRegistry.findStudentsByGradeRange(students, 6.5, 6.5);

        assertSameStudents(Set.of(BORIS), result);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void findStudentsByGradeRangeReturnsEveryoneForFullRange(Map<Integer, Student> students) {
        List<Student> result = StudentRegistry.findStudentsByGradeRange(students, 0.0, 10.0);

        assertSameStudents(new HashSet<>(ALL), result);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void findStudentsByGradeRangeReturnsEmptyListWhenNothingMatches(Map<Integer, Student> students) {
        List<Student> result = StudentRegistry.findStudentsByGradeRange(students, 1.0, 3.0);

        assertTrue(result.isEmpty());
    }

    @Test
    void findStudentsByGradeRangeReturnsEmptyListForEmptyMap() {
        List<Student> result = StudentRegistry.findStudentsByGradeRange(new HashMap<>(), 0.0, 10.0);

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
        "8.0, 5.0",
        "-0.1, 5.0",
        "5.0, 10.1",
        "NaN, 5.0",
        "5.0, NaN"
    })
    void findStudentsByGradeRangeRejectsInvalidBounds(double minGrade, double maxGrade) {
        Map<Integer, Student> students = StudentRegistry.createHashMap(ALL);

        assertThrows(IllegalArgumentException.class,
                () -> StudentRegistry.findStudentsByGradeRange(students, minGrade, maxGrade));
    }

    @Test
    void findStudentsByGradeRangeRejectsNullMap() {
        assertThrows(NullPointerException.class,
                () -> StudentRegistry.findStudentsByGradeRange(null, 0.0, 10.0));
    }

    @ParameterizedTest
    @MethodSource("maps")
    void findStudentsByGradeRangeLeavesMapUnchanged(Map<Integer, Student> students) {
        Map<Integer, Student> before = new HashMap<>(students);

        List<Student> result = StudentRegistry.findStudentsByGradeRange(students, 0.0, 10.0);
        assertInstanceOf(ArrayList.class, result);
        result.clear();
        result.add(new Student(99, "Other", 5.0));

        assertEquals(before, students);
    }

    @Test
    void getTopNStudentsByIdReturnsFirstStudentsInDescendingOrder() {
        NavigableMap<Integer, Student> students = StudentRegistry.createDescendingTreeMap(ALL);

        List<Student> result = StudentRegistry.getTopNStudentsById(students, 3);

        assertEquals(List.of(EVA, DMITRY, VERA), result);
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 6, Integer.MAX_VALUE})
    void getTopNStudentsByIdReturnsEveryoneWhenNIsNotLessThanSize(int n) {
        NavigableMap<Integer, Student> students = StudentRegistry.createDescendingTreeMap(ALL);

        List<Student> result = StudentRegistry.getTopNStudentsById(students, n);

        assertEquals(ALL_BY_DESCENDING_ID, result);
    }

    @Test
    void getTopNStudentsByIdReturnsEmptyListWhenNIsZero() {
        NavigableMap<Integer, Student> students = StudentRegistry.createDescendingTreeMap(ALL);

        assertTrue(StudentRegistry.getTopNStudentsById(students, 0).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, Integer.MIN_VALUE})
    void getTopNStudentsByIdRejectsNegativeN(int n) {
        NavigableMap<Integer, Student> students = StudentRegistry.createDescendingTreeMap(ALL);

        assertThrows(IllegalArgumentException.class,
                () -> StudentRegistry.getTopNStudentsById(students, n));
    }

    @Test
    void getTopNStudentsByIdReturnsEmptyListForEmptyMap() {
        NavigableMap<Integer, Student> students = StudentRegistry.createDescendingTreeMap(List.of());

        assertTrue(StudentRegistry.getTopNStudentsById(students, 3).isEmpty());
    }

    @Test
    void getTopNStudentsByIdRejectsAscendingMap() {
        NavigableMap<Integer, Student> ascending = new TreeMap<>(StudentRegistry.createHashMap(ALL));

        assertThrows(IllegalArgumentException.class,
                () -> StudentRegistry.getTopNStudentsById(ascending, 2));
    }

    @Test
    void getTopNStudentsByIdRejectsNullMap() {
        assertThrows(NullPointerException.class, () -> StudentRegistry.getTopNStudentsById(null, 2));
    }

    @Test
    void getTopNStudentsByIdLeavesMapUnchanged() {
        NavigableMap<Integer, Student> students = StudentRegistry.createDescendingTreeMap(ALL);
        Map<Integer, Student> before = new HashMap<>(students);

        List<Student> result = StudentRegistry.getTopNStudentsById(students, 2);
        assertInstanceOf(ArrayList.class, result);
        result.clear();
        result.add(new Student(99, "Other", 5.0));

        assertEquals(before, students);
    }
}
