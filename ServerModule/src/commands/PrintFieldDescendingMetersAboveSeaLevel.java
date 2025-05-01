package commands;

import data.*;
import managers.CollectionManager;
import utility.*;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Класс для вывода значений поля
 */
public class PrintFieldDescendingMetersAboveSeaLevel extends Command {
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
    public PrintFieldDescendingMetersAboveSeaLevel(Console console, CollectionManager collectionManager) {
        super("print_field_descending_meters_above_sea_level", "вывести значения поля metersAboveSeaLevel всех элементов в порядке убывания");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse apply(String arguments, User user) {
        String[] args = arguments.split(" ");
        if (args.length>1) {
            return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }

        StringBuilder sb = new StringBuilder();
        ArrayList<Float> sea = new ArrayList<>();
        for (var e: collectionManager.getCollection()) {
            sea.add(e.getMetersAboveSeaLevel());
        }
        Collections.sort(sea, Collections.reverseOrder());
        for (var e: sea) {
            sb.append(e.toString()+ "\n");
        }
        return new ExecutionResponse(true, sb.toString());



    }
}
