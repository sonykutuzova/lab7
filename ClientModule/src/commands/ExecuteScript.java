package commands;

import utility.*;

/**
 * Класс команды для выполнения скрипта из файла
 */
public class ExecuteScript extends Command {
    /** Консоль */
    private final Console console;

    /**
     * Конструктор
     * @param console консоль
     */
    public ExecuteScript(Console console) {
        super("execute_script <file_name>", "исполнить скрипт из указанного файла");
        this.console = console;
    }

    /**
     * Исполнение команды
     *
     * @param arguments строка с аргументами
     * @return возвращает ответ о выполнении команды
     */

    public ExecutionResponse apply(String arguments) {
        String[] args = arguments.split(" ");
        if (args.length >2 && args[1].isEmpty() || args.length <=1) return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");

        return new ExecutionResponse("Выполнение скрипта '" + args[1] + "'...");
    }
}