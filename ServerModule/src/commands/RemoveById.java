package commands;

import data.*;
import managers.CollectionManager;
import utility.*;

import java.util.Map;


/**
 * Класс команды для удаления элемента из коллекции по его id
 */
public class RemoveById extends Command {
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
    public RemoveById(Console console, CollectionManager collectionManager) {
        super("remove_by_id <ID>", "удалить элемент из коллекции по ID");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    /**
     * Исполнение команды
     *
     * @param arguments строка с аргументами
     * @return возвращает ответ о выполнении команды
     */
    @Override
    public ExecutionResponse apply(String arguments, User user) {
        String[] args = arguments.split(" ");
        if (args.length+1 >2 && args[0].isEmpty() || args.length+1 <=1)
            return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        long id = -1;
        try {
            System.out.println(args[0]);
            id = Long.parseLong(args[0].trim());
        } catch (NumberFormatException e) {
            return new ExecutionResponse(false, "ID не распознан");
        }

        if (collectionManager.byId(id) == null || !collectionManager.getCollection().contains(collectionManager.byId(id)))
            return new ExecutionResponse(false, "Не существующий ID");
        Map<Long, Integer> usersElement = collectionManager.getUsersElements();
        if (usersElement.get(id).equals(collectionManager.getUserId(user))) {
            collectionManager.remove(id);
            collectionManager.update();
            return new ExecutionResponse("Город успешно удален!");
        }
        return new ExecutionResponse("Вы не можете удалить этот город!");
    }
}