package commands;

import utility.*;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс команды для вывода справки по всем доступным командам
 */
public class Help extends Command {
    /**
     * Консоль
     */
    private final Console console;
    /**
     * Менеджер комманд
     */
    private Map<CommandTypes,String[]> commands;

    /**
     * Конструктор
     *
     * @param console        консоль
     * @param commands менеджер команд
     */
    public Help(Console console, Map<CommandTypes, String[]> commands) {
        super("help", "вывести справку по доступным командам");
        this.console = console;
        this.commands = commands;
    }
    /**
     * Исполнение команды
     *
     * @param arguments строка с аргументами
     * @return возвращает ответ о выполнении команды
     */

    public ExecutionResponse apply(String arguments) {
        String[] args = arguments.split(" ");
        if (args.length > 1 && !args[1].isEmpty())
            return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");

        return new ExecutionResponse(commands.keySet().stream().map(command -> String.format(" %-35s%-1s%n", commands.get(command)[0], commands.get(command)[1])).collect(Collectors.joining("\n")));
    }
}
