package commands;

import data.*;
import managers.CollectionManager;
import utility.*;


/**
 * Класс команды для сохранения коллекции в файл
 */
public class Save extends Command {
    /**
     * Консоль
     */
    private final Console console;
    /**
     * Менеджер коллекции
     */
    private final CollectionManager collectionManager;
    /**
     * Конструктор
     *
     * @param console           консоль
     * @param collectionManager менеджер коллекции
     */
    public Save(Console console, CollectionManager collectionManager) {
        super("save", "сохранить коллекцию в файл");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    /**
     * Исполнение команды
     *
     * @param arguments массив с аргументами
     * @return возвращает ответ о выполнении команды
     */
    @Override
    public ExecutionResponse apply(String arguments, User user) {
        String[] args = arguments.split(" ");
        if (args.length > 1 && !args[1].isEmpty())
            return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");

        //collectionManager.saveCollection();
        return new ExecutionResponse(true, "");
    }
}
