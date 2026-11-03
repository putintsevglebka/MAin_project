package homework.experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Сравнивает время работы {@link ArrayList} и {@link LinkedList} на одинаковых операциях.
 * Для каждой операции выполняются прогревочные запуски и серия замеров, в таблицу попадает медиана.
 * Каждый запуск получает новый список, а его предварительное заполнение в замер не входит.
 */
public final class CollectionPerformanceExperiment {

    private static final int ELEMENT_COUNT = 10_000;
    private static final int MIN_WARMUP_RUNS = 5;
    // Короткие сценарии за несколько запусков не успевают пройти JIT-компиляцию.
    private static final long MIN_WARMUP_NANOS = 1_000_000_000L;
    private static final int MEASURED_RUNS = 9;

    private static final String HEADER_FORMAT = "| %-38s | %9s | %13s | %14s |%n";
    private static final String SEPARATOR =
            "| -------------------------------------- | --------: | ------------: | -------------: |";
    private static final String ROW_FORMAT = "| %-38s | %9d | %13.3f | %14.3f |%n";

    // Значения упакованы заранее, чтобы упаковка Integer не попадала в замер.
    private static final Integer[] VALUES = createValues();

    private CollectionPerformanceExperiment() {
    }

    /**
     * Запускает эксперимент и печатает результаты в виде Markdown-таблицы.
     *
     * @param args аргументы командной строки, не используются
     * @throws IllegalStateException если контрольные суммы двух реализаций не совпали
     */
    public static void main(String[] args) {
        List<Scenario> scenarios = List.of(
                new Scenario("Добавление в конец", false, CollectionPerformanceExperiment::addToEnd),
                new Scenario("Добавление в начало", false, CollectionPerformanceExperiment::addToStart),
                new Scenario("Вставка в середину (в список из " + ELEMENT_COUNT + ")", true,
                        CollectionPerformanceExperiment::insertIntoMiddle),
                new Scenario("Доступ по индексу", true, CollectionPerformanceExperiment::readByIndex),
                new Scenario("Удаление из начала", true, CollectionPerformanceExperiment::removeFromStart),
                new Scenario("Удаление из конца", true, CollectionPerformanceExperiment::removeFromEnd));

        System.out.printf(Locale.ROOT, HEADER_FORMAT, "Операция", "Элементов", "ArrayList, мс", "LinkedList, мс");
        System.out.println(SEPARATOR);

        long totalChecksum = 0;
        for (Scenario scenario : scenarios) {
            totalChecksum += measureAndPrintRow(scenario);
        }

        System.out.println();
        System.out.println("Контрольная сумма: " + totalChecksum);
        System.out.println("Выводы по результатам приведены в README.md.");
    }

    private static long measureAndPrintRow(Scenario scenario) {
        long arrayChecksum = 0;
        long linkedChecksum = 0;
        long warmupStart = System.nanoTime();
        int warmupRuns = 0;
        while (warmupRuns < MIN_WARMUP_RUNS || System.nanoTime() - warmupStart < MIN_WARMUP_NANOS) {
            arrayChecksum += runOnce(scenario, new ArrayList<>()).checksum();
            linkedChecksum += runOnce(scenario, new LinkedList<>()).checksum();
            warmupRuns++;
        }
        // Число прогревов зависит от скорости машины, поэтому в итоговую сумму входят только замеры.
        long warmupChecksum = arrayChecksum;

        long[] arrayTimes = new long[MEASURED_RUNS];
        long[] linkedTimes = new long[MEASURED_RUNS];
        for (int round = 0; round < MEASURED_RUNS; round++) {
            RunResult arrayRun;
            RunResult linkedRun;
            // Порядок чередуется, чтобы ни одна реализация не шла всегда первой.
            if (round % 2 == 0) {
                arrayRun = runOnce(scenario, new ArrayList<>());
                linkedRun = runOnce(scenario, new LinkedList<>());
            } else {
                linkedRun = runOnce(scenario, new LinkedList<>());
                arrayRun = runOnce(scenario, new ArrayList<>());
            }
            arrayTimes[round] = arrayRun.nanos();
            linkedTimes[round] = linkedRun.nanos();
            arrayChecksum += arrayRun.checksum();
            linkedChecksum += linkedRun.checksum();
        }

        if (arrayChecksum != linkedChecksum) {
            throw new IllegalStateException("Контрольные суммы не совпали: " + scenario.name());
        }

        System.out.printf(Locale.ROOT, ROW_FORMAT, scenario.name(), ELEMENT_COUNT,
                medianMillis(arrayTimes), medianMillis(linkedTimes));
        return arrayChecksum - warmupChecksum;
    }

    private static RunResult runOnce(Scenario scenario, List<Integer> list) {
        if (scenario.prefilled()) {
            addToEnd(list);
        }

        long start = System.nanoTime();
        long result = scenario.operation().run(list);
        long nanos = System.nanoTime() - start;

        long checksum = result;
        for (Integer value : list) {
            checksum = 31 * checksum + value;
        }
        return new RunResult(nanos, checksum);
    }

    private static double medianMillis(long[] nanos) {
        long[] sorted = nanos.clone();
        Arrays.sort(sorted);
        return sorted[sorted.length / 2] / 1_000_000.0;
    }

    private static Integer[] createValues() {
        Integer[] values = new Integer[ELEMENT_COUNT];
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            values[i] = i;
        }
        return values;
    }

    private static long addToEnd(List<Integer> list) {
        for (Integer value : VALUES) {
            list.add(value);
        }
        return list.size();
    }

    private static long addToStart(List<Integer> list) {
        for (Integer value : VALUES) {
            list.add(0, value);
        }
        return list.size();
    }

    private static long insertIntoMiddle(List<Integer> list) {
        for (Integer value : VALUES) {
            list.add(list.size() / 2, value);
        }
        return list.size();
    }

    private static long readByIndex(List<Integer> list) {
        long sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        return sum;
    }

    private static long removeFromStart(List<Integer> list) {
        long sum = 0;
        while (!list.isEmpty()) {
            sum += list.remove(0);
        }
        return sum;
    }

    private static long removeFromEnd(List<Integer> list) {
        long sum = 0;
        while (!list.isEmpty()) {
            sum += list.remove(list.size() - 1);
        }
        return sum;
    }

    @FunctionalInterface
    private interface Operation {
        long run(List<Integer> list);
    }

    private record Scenario(String name, boolean prefilled, Operation operation) {
    }

    private record RunResult(long nanos, long checksum) {
    }
}
