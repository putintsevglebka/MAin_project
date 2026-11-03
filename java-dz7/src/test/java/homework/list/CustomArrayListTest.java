package homework.list;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class CustomArrayListTest {

    @Test
    void newListIsEmpty() {
        CustomArrayList<String> list = new CustomArrayList<>();

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addOneElement() {
        CustomArrayList<String> list = new CustomArrayList<>();

        list.add("a");

        assertFalse(list.isEmpty());
        assertContent(List.of("a"), list);
    }

    @Test
    void addSeveralElementsKeepsOrder() {
        CustomArrayList<String> list = new CustomArrayList<>();

        list.add("a");
        list.add("b");
        list.add("c");

        assertContent(List.of("a", "b", "c"), list);
    }

    @Test
    void addMoreThanTenElementsKeepsAllData() {
        CustomArrayList<Integer> list = new CustomArrayList<>();

        addUntilSize(list, 100);

        assertEquals(100, list.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(i, list.get(i));
        }
    }

    @ParameterizedTest
    @MethodSource("firstMiddleAndLastElements")
    void getReturnsFirstMiddleAndLastElement(int index, String expected) {
        CustomArrayList<String> list = listOf("a", "b", "c", "d", "e");

        assertEquals(expected, list.get(index));
    }

    static List<Arguments> firstMiddleAndLastElements() {
        return List.of(
                Arguments.of(0, "a"),
                Arguments.of(2, "c"),
                Arguments.of(4, "e"));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 5})
    void getRejectsNegativeIndexAndIndexEqualToSize(int index) {
        CustomArrayList<String> list = listOf("a", "b", "c", "d", "e");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(index));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 5})
    void removeRejectsNegativeIndexAndIndexEqualToSize(int index) {
        CustomArrayList<String> list = listOf("a", "b", "c", "d", "e");

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(index));
        assertContent(List.of("a", "b", "c", "d", "e"), list);
    }

    @Test
    void getAndRemoveRejectIndexZeroOnEmptyList() {
        CustomArrayList<String> list = new CustomArrayList<>();

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
    }

    @Test
    void indexFreedByRemoveIsNoLongerAvailable() {
        CustomArrayList<String> list = listOf("a", "b", "c");

        list.remove(2);

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));
    }

    @ParameterizedTest
    @MethodSource("removalCases")
    void removeReturnsFirstMiddleAndLastElement(int index, String expected, List<String> remaining) {
        CustomArrayList<String> list = listOf("a", "b", "c", "d", "e");

        assertEquals(expected, list.remove(index));
        assertContent(remaining, list);
    }

    static List<Arguments> removalCases() {
        return List.of(
                Arguments.of(0, "a", List.of("b", "c", "d", "e")),
                Arguments.of(2, "c", List.of("a", "b", "d", "e")),
                Arguments.of(4, "e", List.of("a", "b", "c", "d")));
    }

    @Test
    void addNullThrowsNullPointerException() {
        CustomArrayList<String> list = new CustomArrayList<>();

        assertThrows(NullPointerException.class, () -> list.add(null));
        assertTrue(list.isEmpty());
    }

    @Test
    void iteratorWalksInListOrder() {
        CustomArrayList<String> list = listOf("a", "b", "c");

        Iterator<String> iterator = list.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("a", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("b", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("c", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void nextOnExhaustedIteratorThrowsNoSuchElementException() {
        CustomArrayList<String> list = listOf("a");
        Iterator<String> iterator = list.iterator();
        iterator.next();

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void nextOnEmptyListIteratorThrowsNoSuchElementException() {
        Iterator<String> iterator = new CustomArrayList<String>().iterator();

        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorDoesNotVisitRemovedElements() {
        CustomArrayList<String> list = listOf("a", "b", "c");
        list.remove(0);

        Iterator<String> iterator = list.iterator();

        assertEquals("b", iterator.next());
        assertEquals("c", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void forEachVisitsAllElementsInOrder() {
        CustomArrayList<String> list = listOf("a", "b", "c");
        List<String> visited = new ArrayList<>();

        for (String element : list) {
            visited.add(element);
        }

        assertEquals(List.of("a", "b", "c"), visited);
    }

    @Test
    void capacityGrowsFromTenToFifteenToTwentyTwo() throws ReflectiveOperationException {
        CustomArrayList<Integer> list = new CustomArrayList<>();
        assertEquals(10, backingArray(list).length);

        addUntilSize(list, 10);
        assertEquals(10, backingArray(list).length);

        addUntilSize(list, 11);
        assertEquals(15, backingArray(list).length);

        addUntilSize(list, 15);
        assertEquals(15, backingArray(list).length);

        addUntilSize(list, 16);
        assertEquals(22, backingArray(list).length);
    }

    @Test
    void customCapacityIsUsedForBackingArray() throws ReflectiveOperationException {
        assertEquals(3, backingArray(new CustomArrayList<String>(3)).length);
    }

    @Test
    void zeroCapacityGrows() throws ReflectiveOperationException {
        CustomArrayList<Integer> list = new CustomArrayList<>(0);
        assertEquals(0, backingArray(list).length);

        list.add(0);

        assertEquals(1, backingArray(list).length);
    }

    @Test
    void capacityOneGrows() throws ReflectiveOperationException {
        CustomArrayList<Integer> list = new CustomArrayList<>(1);

        addUntilSize(list, 1);
        assertEquals(1, backingArray(list).length);

        addUntilSize(list, 2);
        assertEquals(2, backingArray(list).length);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void smallCapacityGrowsWithoutDataLoss(int initialCapacity) {
        CustomArrayList<Integer> list = new CustomArrayList<>(initialCapacity);

        addUntilSize(list, 30);

        assertEquals(30, list.size());
        for (int i = 0; i < 30; i++) {
            assertEquals(i, list.get(i));
        }
    }

    @Test
    void negativeCapacityIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new CustomArrayList<String>(-1));
    }

    @Test
    void removeShiftsTailAndClearsFreedCell() throws ReflectiveOperationException {
        CustomArrayList<String> list = listOf("a", "b", "c", "d");

        list.remove(1);

        Object[] firstCells = Arrays.copyOf(backingArray(list), 4);
        assertArrayEquals(new Object[] {"a", "c", "d", null}, firstCells);
    }

    @Test
    void removeLastElementClearsItsCell() throws ReflectiveOperationException {
        CustomArrayList<String> list = listOf("a", "b");

        list.remove(1);

        assertEquals("a", backingArray(list)[0]);
        assertNull(backingArray(list)[1]);
    }

    @Test
    void removeFromFullArrayClearsLastCell() throws ReflectiveOperationException {
        CustomArrayList<Integer> list = new CustomArrayList<>();
        addUntilSize(list, 10);

        list.remove(0);

        Object[] cells = backingArray(list);
        assertEquals(9, cells[8]);
        assertNull(cells[9]);
    }

    @Test
    void addAfterRemoveAppendsToEnd() {
        CustomArrayList<String> list = listOf("a", "b", "c");

        list.remove(1);
        list.add("d");

        assertContent(List.of("a", "c", "d"), list);
    }

    @Test
    void removingOnlyElementLeavesListEmpty() {
        CustomArrayList<String> list = listOf("a");

        assertEquals("a", list.remove(0));

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertFalse(list.iterator().hasNext());
    }

    private static CustomArrayList<String> listOf(String... values) {
        CustomArrayList<String> list = new CustomArrayList<>();
        for (String value : values) {
            list.add(value);
        }
        return list;
    }

    private static void addUntilSize(CustomArrayList<Integer> list, int targetSize) {
        for (int i = list.size(); i < targetSize; i++) {
            list.add(i);
        }
    }

    private static void assertContent(List<String> expected, CustomArrayList<String> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    private static Object[] backingArray(CustomArrayList<?> list) throws ReflectiveOperationException {
        Field field = CustomArrayList.class.getDeclaredField("elements");
        field.setAccessible(true);
        return (Object[]) field.get(list);
    }
}
