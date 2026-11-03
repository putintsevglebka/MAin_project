package homework.list;

/**
 * Список элементов с доступом по индексу.
 *
 * @param <E> тип элементов списка
 */
public interface CustomList<E> extends Iterable<E> {

    /**
     * Добавляет элемент в конец списка.
     *
     * @param element добавляемый элемент, не может быть null
     * @throws NullPointerException если element равен null
     */
    void add(E element);

    /**
     * Возвращает элемент по индексу.
     *
     * @param index индекс из диапазона [0, size)
     * @return элемент с указанным индексом
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    E get(int index);

    /**
     * Удаляет элемент по индексу и сдвигает следующие за ним элементы влево.
     *
     * @param index индекс из диапазона [0, size)
     * @return удалённый элемент
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    E remove(int index);

    /**
     * Возвращает количество элементов в списке.
     *
     * @return количество элементов
     */
    int size();

    /**
     * Проверяет, пуст ли список.
     *
     * @return true, если в списке нет элементов
     */
    boolean isEmpty();
}
