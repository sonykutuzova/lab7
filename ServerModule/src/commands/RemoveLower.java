package commands;

import data.*;
import managers.CollectionManager;
import utility.*;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Класс команды для удаления элементов при условии
 */
public class RemoveLower extends Command {
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

    public RemoveLower(Console console, CollectionManager collectionManager) {
        super("remove_lower {element}", "удалить из коллекции все элементы, меньшие, чем заданный");
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
            id = Long.parseLong(args[0].trim());
        } catch (NumberFormatException e) {
            return new ExecutionResponse(false, "ID не распознан");
        }

        if (collectionManager.byId(id) == null || !collectionManager.getCollection().contains(collectionManager.byId(id)))
            return new ExecutionResponse(false, "Не существующий ID");
        Iterator<City> iterator = collectionManager.getCollection().iterator();
        Map<Long, Integer> userElements = collectionManager.getUsersElements();
        List<Long> arr = new ArrayList<>();
        while (iterator.hasNext()) {
            var e = iterator.next();
            if (e.getId() == id) break;
            else if (userElements.get(e.getId()).equals(collectionManager.getUserId(user))) {
                arr.add(e.getId());
            }
        }
        for (var e : arr) {
            collectionManager.remove(e);
        }

        collectionManager.update();
        return new ExecutionResponse("Все меньшие города удалены!");
    }
}