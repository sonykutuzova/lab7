package commands;

import data.*;
import managers.CollectionManager;
import utility.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Класс команды для очищения коллекции
 */
public class Clear extends Command {
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
    public Clear(Console console, CollectionManager collectionManager) {
        super("clear", "очистить коллекцию");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    /**
     * Исполнение команды
     *
     * @param arguments строка с аргументами команды
     * @return возвращает ответ о выполнении команды
     */
    @Override
    public ExecutionResponse apply(String arguments, User user) {
        String[] args = arguments.split(" ");
        if (args.length > 1 && !args[1].isEmpty())
            return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        Map<Long, Integer> usersElement = collectionManager.getUsersElements();
        Iterator<City> iterator = collectionManager.getCollection().iterator();
        List<Long> ids = new ArrayList<>();
        while (iterator.hasNext()) {
            City city = iterator.next();
            if (usersElement.get(city.getId()).equals(collectionManager.getUserId(user))) {ids.add(city.getId());}
        }
        for (var c: ids){
            collectionManager.remove(c);
        }
        collectionManager.update();

        collectionManager.update();
        return new ExecutionResponse("Коллекция очищена!");
    }
}
