package homework.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Создание карт студентов и выборки по ним.
 */
public final class StudentRegistry {

    private StudentRegistry() {
    }

    /**
     * Создаёт {@link HashMap}, в которой ключом служит идентификатор студента.
     *
     * @param students студенты с уникальными идентификаторами
     * @return новая карта
     * @throws NullPointerException     если коллекция или её элемент равны null
     * @throws IllegalArgumentException если идентификаторы повторяются
     */
    public static HashMap<Integer, Student> createHashMap(Collection<Student> students) {
        HashMap<Integer, Student> result = new HashMap<>();
        putAllById(result, students);
        return result;
    }

    /**
     * Создаёт {@link TreeMap} с сортировкой идентификаторов по убыванию.
     *
     * @param students студенты с уникальными идентификаторами
     * @return новая карта
     * @throws NullPointerException     если коллекция или её элемент равны null
     * @throws IllegalArgumentException если идентификаторы повторяются
     */
    public static TreeMap<Integer, Student> createDescendingTreeMap(Collection<Student> students) {
        TreeMap<Integer, Student> result = new TreeMap<>(Comparator.reverseOrder());
        putAllById(result, students);
        return result;
    }

    /**
     * Возвращает студентов с оценкой в замкнутом диапазоне [minGrade, maxGrade].
     * Карта не изменяется, порядок результата совпадает с порядком её обхода.
     *
     * @param students карта студентов
     * @param minGrade нижняя граница, входит в диапазон
     * @param maxGrade верхняя граница, входит в диапазон
     * @return новый изменяемый список
     * @throws NullPointerException     если карта равна null
     * @throws IllegalArgumentException если minGrade больше maxGrade, либо граница
     *                                  равна NaN или лежит вне диапазона оценок
     */
    public static List<Student> findStudentsByGradeRange(
            Map<Integer, Student> students,
            double minGrade,
            double maxGrade) {
        Objects.requireNonNull(students, "students");
        if (!Student.isValidGrade(minGrade) || !Student.isValidGrade(maxGrade)) {
            throw new IllegalArgumentException(
                    "grade bounds are out of range: " + minGrade + ", " + maxGrade);
        }
        if (minGrade > maxGrade) {
            throw new IllegalArgumentException(
                    "minGrade is greater than maxGrade: " + minGrade + " > " + maxGrade);
        }

        List<Student> result = new ArrayList<>();
        for (Student student : students.values()) {
            if (student.grade() >= minGrade && student.grade() <= maxGrade) {
                result.add(student);
            }
        }
        return result;
    }

    /**
     * Возвращает первые n значений карты, то есть не более n студентов с наибольшими
     * идентификаторами в порядке убывания id. Карта не изменяется.
     *
     * @param students карта с сортировкой ключей по убыванию
     * @param n        наибольшее число студентов в результате
     * @return новый изменяемый список, при n == 0 пустой
     * @throws NullPointerException     если карта равна null
     * @throws IllegalArgumentException если n отрицательно или ключи карты идут по возрастанию
     */
    public static List<Student> getTopNStudentsById(NavigableMap<Integer, Student> students, int n) {
        Objects.requireNonNull(students, "students");
        if (n < 0) {
            throw new IllegalArgumentException("n must not be negative: " + n);
        }
        if (students.size() > 1 && students.firstKey() < students.lastKey()) {
            throw new IllegalArgumentException("map keys must be sorted in descending order");
        }

        List<Student> result = new ArrayList<>();
        for (Student student : students.values()) {
            if (result.size() == n) {
                break;
            }
            result.add(student);
        }
        return result;
    }

    private static void putAllById(Map<Integer, Student> target, Collection<Student> students) {
        Objects.requireNonNull(students, "students");
        for (Student student : students) {
            Objects.requireNonNull(student, "student");
            if (target.putIfAbsent(student.id(), student) != null) {
                throw new IllegalArgumentException("duplicate id: " + student.id());
            }
        }
    }
}
