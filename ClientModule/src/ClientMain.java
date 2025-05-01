import commands.CommandTypes;
import managers.*;
import utility.Runner;
import utility.StandardConsole;

import java.util.HashMap;
import java.util.Map;

public class ClientMain {
    public static void main(String[] args) {
        var console = new StandardConsole();
        NetworkManager networkManager = new NetworkManager("127.0.0.1", 31145, console);

        Map<CommandTypes,String[]> commands = new HashMap<>();
        commands.put(CommandTypes.Add,new String[]{"add {element}", "добавить новый элемент в коллекцию"});
        commands.put(CommandTypes.Clear, new String[]{"clear", "очистить коллекцию"});
        commands.put(CommandTypes.Exit, new String[]{"exit", "завершить программу"});
        commands.put(CommandTypes.Help,new String[]{"help", "вывести справку по доступным командам"});
        commands.put(CommandTypes.Info,new String[]{"info", "вывести информацию о коллекции"});
        commands.put(CommandTypes.RemoveById,new String[]{"remove_by_id <ID>", "удалить элемент из коллекции по ID"});
        commands.put(CommandTypes.RemoveLower,new String[]{"remove_lower {element}", "удалить из коллекции все элементы, меньшие, чем заданный"});
        commands.put(CommandTypes.Show,new String[]{"show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении"});
        commands.put(CommandTypes.Update,new String[]{"update <ID> {element}", "обновить значение элемента коллекции по ID"});
        commands.put(CommandTypes.ExecuteScript,new String[]{"execute_script <file_name>", "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме."});
        commands.put(CommandTypes.Reorder,new String[]{"reorder", "отсортировать коллекцию в порядке, обратном нынешнему"});
        commands.put(CommandTypes.FilterByMetersAboveSeaLevel,new String[]{"filter_by_meters_above_sea_level <metersAboveSeaLevel>", "вывести элементы, значение поля metersAboveSeaLevel которых равно заданному"});
        commands.put(CommandTypes.MaxByPopulation,new String[]{"max_by_population", "вывести любой объект из коллекции, значение поля population которого является максимальным"});
        commands.put(CommandTypes.PrintFieldDescendingMetersAboveSeaLevel,new String[]{"print_field_descending_meters_above_sea_level", "вывести значения поля metersAboveSeaLevel всех элементов в порядке убывания"});
        commands.put(CommandTypes.RemoveFirst,new String[]{"remove_first", "удалить первый элемент из коллекции"});
        console.println(networkManager.init().getMassage());
        Runner runner = new Runner(networkManager,console, commands);
        runner.run();
        runner.interactiveMode();




    }
}