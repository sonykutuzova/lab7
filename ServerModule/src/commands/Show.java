package commands;

import data.*;
import managers.CollectionManager;
import utility.*;

import java.util.Map;


/**
 * Класс команды для вывода коллекции
 */
public class Show extends Command {

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
    public Show(Console console, CollectionManager collectionManager) {
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
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
        if (arguments.split(" ").length != 1 || !arguments.equals("")) return new ExecutionResponse(false, "Неправильное кол-во аргументов!\nИспользование '"+getName()+"'");
        var collection = collectionManager.showCollection();
        Map<Long, Integer> userElements = collectionManager.getUsersElements();
        Integer userId = collectionManager.getUserId(user);
        if (collection.size()==0) return new ExecutionResponse(true, "Коллекция пуста!");
        StringBuilder sb = new StringBuilder();
        for(City e: collection){
            if (userElements.get(e.getId()).equals(userId)) sb.append("Your element -> " + e.toString() + "\n");
            else sb.append(e.toString()+"\n");
        }
        return new ExecutionResponse(true, sb.toString());
    }
}
