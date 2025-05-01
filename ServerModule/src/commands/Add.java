package commands;

import data.*;
import managers.CollectionManager;
import utility.*;

/**
 * Класс команды для добавления нового элемента в коллекцию
 */
public class Add extends Command {
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
    public Add(Console console, CollectionManager collectionManager) {
        super("add {element}", "добавить новый элемент в коллекцию");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    /**
     * Исполнение команды
     *
     * @param arguments с аргументами команды
     * @return возвращает ответ о выполнении команды
     */

    public ExecutionResponse apply(String arguments, User user) {
        if (arguments.isEmpty()) return new ExecutionResponse(false, "Неправильное кол-во аргументов\nИспользование '"+getName()+"'");
        City d = City.fromArray(arguments.split("/"));
        //Integer userId = collectionManager.get(user);
        try{
            if (d!=null || d.validate()){

                //d.setId(collectionManager.getFreeId());
                collectionManager.add(d, user);

                return new ExecutionResponse(true, "Молодец, город создан");
            }
            return new ExecutionResponse(false, "Поля города не валидны!\nГород не создан");
        }
        catch (NullPointerException e){

            return new ExecutionResponse(false, "Поля города не валидны!\nГород не создан");
        }
    }
}



