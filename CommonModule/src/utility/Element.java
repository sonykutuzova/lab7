package utility;
/**
 * Абстрактный класс элементов
 */

public abstract class Element implements Comparable<Element>, Validate {
    /**
     * Функция получения id
     *
     * @return id элемента
     */
    abstract public long getId();
}
