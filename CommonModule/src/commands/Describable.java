package commands;

/**
 * Интерфейс, содержащий методы для получения названия команды и ее описания
 */
public interface Describable {
    /**
     * Получить имя.
     * @return имя
     */
    String getName();

    /**
     * Получить описание.
     * @return описание
     */
    String getDescription();
}

