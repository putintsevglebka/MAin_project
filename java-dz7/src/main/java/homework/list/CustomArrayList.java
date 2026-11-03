package homework.list;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Динамический список, хранящий элементы в массиве Object[].
 * Когда массив заполнен, его ёмкость увеличивается в 1.5 раза.
 * Значения null не допускаются.
 *
 * @param <E> тип элементов списка
 */
public class CustomArrayList<E> implements CustomList<E> {

    private static final int DEFAULT_CAPACITY = 10;

    private Object[] elements;
    private int size;

    /**
     * Создаёт пустой список с начальной ёмкостью 10.
     */
    public CustomArrayList() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Создаёт пустой список с указанной начальной ёмкостью.
     *
     * @param initialCapacity начальная ёмкость, допускается 0
     * @throws IllegalArgumentException если ёмкость отрицательная
     */
    public CustomArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Negative capacity: " + initialCapacity);
        }
        elements = new Object[initialCapacity];
    }

    /**
     * Добавляет элемент в конец списка, при необходимости расширяя массив.
     *
     * @param element добавляемый элемент, не может быть null
     * @throws NullPointerException если element равен null
     */
    @Override
    public void add(E element) {
        Objects.requireNonNull(element, "element must not be null");
        if (size == elements.length) {
            grow();
        }
        elements[size] = element;
        size++;
    }

    /**
     * Возвращает элемент по индексу.
     *
     * @param index индекс из диапазона [0, size)
     * @return элемент с указанным индексом
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    @Override
    public E get(int index) {
        Objects.checkIndex(index, size);
        return elementAt(index);
    }

    /**
     * Удаляет элемент по индексу, сдвигает хвост влево и очищает освободившуюся ячейку.
     *
     * @param index индекс из диапазона [0, size)
     * @return удалённый элемент
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    @Override
    public E remove(int index) {
        Objects.checkIndex(index, size);
        E removed = elementAt(index);
        System.arraycopy(elements, index + 1, elements, index, size - index - 1);
        size--;
        elements[size] = null;
        return removed;
    }

    /**
     * Возвращает количество элементов в списке.
     *
     * @return количество элементов
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Проверяет, пуст ли список.
     *
     * @return true, если в списке нет элементов
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Возвращает итератор, обходящий элементы от индекса 0 до size - 1.
     * Удаление через итератор не поддерживается.
     *
     * @return итератор по элементам списка
     */
    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator();
    }

    private void grow() {
        int oldCapacity = elements.length;
        // При ёмкости 0 и 1 сдвиг ничего не прибавляет, поэтому растём минимум на единицу.
        int newCapacity = Math.max(oldCapacity + 1, oldCapacity + (oldCapacity >> 1));
        elements = Arrays.copyOf(elements, newCapacity);
    }

    @SuppressWarnings("unchecked")
    private E elementAt(int index) {
        return (E) elements[index];
    }

    private class ArrayIterator implements Iterator<E> {

        private int cursor;

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E element = elementAt(cursor);
            cursor++;
            return element;
        }
    }
}
