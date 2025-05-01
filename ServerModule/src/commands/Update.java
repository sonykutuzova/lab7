package commands;

import data.*;
import managers.CollectionManager;
import utility.*;

import java.util.Map;

/**
 * Класс команды для обновления элемента коллекции
 */
public class Update extends Command {
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

    public Update(Console console, CollectionManager collectionManager) {
        super("update <ID> {element}", "обновить значение элемента коллекции по ID");
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
        City d = City.fromArray(arguments.split("/"));
        Map<Long, Integer> userElements = collectionManager.getUsersElements();

        try{
            if (d!=null || d.validate()){
                if(userElements.get(d.getId()).equals(collectionManager.getUserId(user))){ collectionManager.update(d);
                return new ExecutionResponse(true, "Молодец, город пересоздан");}
                return new ExecutionResponse(false, "Вы не можете обновить этот город");
            }
            return new ExecutionResponse(false, "Поля города не валидны!\nГород не пересоздан");
        }
        catch (NullPointerException e){
            return new ExecutionResponse(false, "Поля города не валидны!\nГород не пересоздан");
        }
    }
}