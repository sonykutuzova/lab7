package commands;

import data.*;
import managers.CollectionManager;
import utility.*;


/**
 * Класс команды для сортировки коллекции
 */
public class Reorder extends Command {
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
    public Reorder(Console console, CollectionManager collectionManager) {
        super("reorder", "отсортировать коллекцию в порядке, обратном нынешнему");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @param arguments строка с аргументами
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse apply(String arguments, User user) {
        String[] args = arguments.split(" ");
        if (args.length>1) {
            return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }

        //collectionManager.add("reorder", true);
        collectionManager.setIsAscendingSort( true);
        collectionManager.update();
        collectionManager.setIsAscendingSort(false);
        return new ExecutionResponse(true, "Отсортирлвано в обратном порядке");


//        return true;
    }
}