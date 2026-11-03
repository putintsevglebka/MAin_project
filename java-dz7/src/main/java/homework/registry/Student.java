package homework.registry;

/**
 * Неизменяемая модель студента.
 *
 * @param id    положительный идентификатор
 * @param name  непустое имя
 * @param grade оценка от {@link #MIN_GRADE} до {@link #MAX_GRADE} включительно
 */
public record Student(int id, String name, double grade) {

    /** Минимально допустимая оценка. */
    public static final double MIN_GRADE = 0.0;

    /** Максимально допустимая оценка. */
    public static final double MAX_GRADE = 10.0;

    /**
     * Создаёт студента и проверяет значения компонентов.
     *
     * @throws IllegalArgumentException если id не положителен, имя равно null или пустое,
     *                                  либо оценка лежит вне допустимого диапазона
     */
    public Student {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be positive: " + id);
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        if (!isValidGrade(grade)) {
            throw new IllegalArgumentException("grade is out of range: " + grade);
        }
    }

    // Для NaN оба сравнения дают false, поэтому такое значение тоже отклоняется.
    static boolean isValidGrade(double grade) {
        return grade >= MIN_GRADE && grade <= MAX_GRADE;
    }
}
