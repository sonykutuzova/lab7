package commands;

import data.*;
import managers.CollectionManager;
import utility.*;


import java.util.Iterator;

/**
 * Класс для вывода элементов
 */
public class FilterByMetersAboveSeaLevel extends Command {
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
    public FilterByMetersAboveSeaLevel(Console console, CollectionManager collectionManager) {
        super("filter_by_meters_above_sea_level metersAboveSeaLevel", "вывести элементы, значение поля metersAboveSeaLevel которых равно заданному");
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
        float meters;
        try{
            meters = Float.parseFloat(args[0]);
        }catch (NumberFormatException e){return new ExecutionResponse(false,"Неправильный тип данных, надо флоат");}
        Iterator<City> iterator = collectionManager.getCollection().iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()){
            City city = iterator.next();
            if (meters == city.getMetersAboveSeaLevel()) sb.append(city.toString()+ "\n");
        }
        return new ExecutionResponse(true, sb.toString());

    }
}



