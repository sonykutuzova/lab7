package commands;


/**
 * Абстрактный класс команды
 */
public abstract class Command implements Executable, Describable {
    /**
     * Название команды
     */
    private final String name;
    /**
     * Описанее команды
     */
    private final String description;
    /**
     * Конструктор
     *
     * @param name        название команды
     * @param description описание команды
     */
    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Функция для получения названия команды
     *
     * @return возвращает название команды
     */
    public String getName() {
        return name;
    }

    /**
     * Функция для получения описания команды
     *
     * @return возвращает описание команды
     */
    public String getDescription() {
        return description;
    }
    /**
     * Переопределение метода эквивалентности объект
     *
     * @param obj сравниваемый объект
     * @return true, если объекты эквивалентны
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Command command = (Command) obj;
        return name.equals(command.name) && description.equals(command.description);
    }
    /**
     * @return возвращает Хэш-код объекта
     */
    @Override
    public int hashCode() {
        return name.hashCode() + description.hashCode();
    }
    /**
     * @return возвращает объект, переведенный в строковое представление
     */
    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

