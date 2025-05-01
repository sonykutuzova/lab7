package utility;

import data.User;
import managers.*;
import commands.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс исполнения программы
 */
public class Runner {
    /**
     * консоль
     */
    private Console console;
    /**
     * команды
     */
    private Map<CommandTypes,String[]> commands;
    /**
     * связыватель с сетью
     */
    private NetworkManager networkManager;
    /**
     * стек скрипта
     */
    private final ArrayList<String> scriptStack = new ArrayList<>();
    /**
     * максимальная глубина рекурсии(устанавливается пользователем)
     */
    private int lengthRecursion = -1;
    private User user;
    /**
     * Конструктор
     * @param console консоль
     * @param commands словарь команд
     */
    public Runner(NetworkManager networkManager, Console console, Map<CommandTypes,String[]> commands) {
        this.console = console;
        this.networkManager = networkManager;
        this.commands=commands;
    }
    public void run() {
        ExecutionResponse ans = new ExecutionResponse(false, "");
        ExecutionResponse response;
        //networkManager.init();
        if (user == null){
            console.println("Сначала авторизируйтесь");
            try {
                user = AskManager.askUser(console);
            }catch (AskManager.AskBreak e) {
                console.println("Что то пошло не так");
            }
        }
        ans = networkManager.sendData(NetworkManager.serializer(new Container(user)));
        response = networkManager.receiveData();
        while(!response.getExitCode()){
            console.println(response.getMassage());
            if (!response.getExitCode() && !response.getMassage().isEmpty()) {
                console.println(response.getMassage());
                try{
                    user = AskManager.askUser(console);
                    ans = networkManager.sendData(NetworkManager.serializer(new Container(user)));
                    response = networkManager.receiveData();
                }
                catch( AskManager.AskBreak e){
                    console.println("отмена...");
                    break;
                }
            }
        }
        console.println(response.getMassage());
        }
    /**
     * Интерактивный режим
     */
    public void interactiveMode() {
        try {
            ExecutionResponse commandStatus;
            while (true) {
                console.prompt();
                String[] userCommand = (console.readln().trim() + " ").split(" ");
                if(userCommand.length>2){
                    console.println("Перепроверьте кол-во аргументов");
                    continue;
                }
                if (userCommand.length==2) userCommand[1] = userCommand[1].trim();
                if (userCommand.length==0) userCommand = new String[]{""};

                commandStatus = launchCommand(userCommand);

                if (commandStatus.getMassage().equals("exit")) break;
                console.println(commandStatus.getMassage());
            }
        } catch (NoSuchElementException exception) {
            console.printError("Пользовательский ввод не обнаружен!");
        } catch (IllegalStateException exception) {
            console.printError("Непредвиденная ошибка!");
        }
    }

    /**
     * Проверяет рекурсивность выполнения скриптов.
     * @param argument Название запускаемого скрипта
     * @return можно ли выполнять скрипт.
     */
    private boolean checkRecursion(String argument, Scanner scriptScanner) {
        var recStart = -1;
        var i = 0;
        for (String script : scriptStack) {
            i++;
            if (argument.equals(script)) {
                if (recStart < 0) recStart = i;
                if (lengthRecursion < 0) {
                    console.selectConsoleScanner();
                    console.println("Была замечена рекурсия! Введите максимальную глубину рекурсии (0..500)");
                    while (lengthRecursion < 0 || lengthRecursion > 500) {
                        try { console.print("> "); lengthRecursion = Integer.parseInt(console.readln().trim()); } catch (NumberFormatException e) { console.println("длина не распознана"); }
                    }
                    console.selectFileScanner(scriptScanner);
                }
                if (i > recStart + lengthRecursion || i > 500)
                    return false;
            }
        }
        return true;
    }

    /**
     * Режим для запуска скрипта.
     * @param argument Аргумент скрипта
     * @return Код завершения.
     */
    private ExecutionResponse scriptMode(String argument) {
        String[] userCommand = {"", ""};
        argument = argument.split(" ")[0];
        StringBuilder executionOutput = new StringBuilder();
        System.out.println(argument);
        if (!new File(argument).exists()) return new ExecutionResponse(false, "Файл не существет!");
        if (!Files.isReadable(Paths.get(argument))) return new ExecutionResponse(false, "Прав для чтения нет!");

        scriptStack.add(argument);
        try (Scanner scriptScanner = new Scanner(new File(argument))) {

            ExecutionResponse commandStatus;

            if (!scriptScanner.hasNext()) throw new NoSuchElementException();
            console.selectFileScanner(scriptScanner);
            do {
                userCommand = (console.readln().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                while (console.isCanReadln() && userCommand[0].isEmpty()) {
                    userCommand = (console.readln().trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();
                }
                executionOutput.append(console.getPrompt() + String.join(" ", userCommand) + "\n");
                var needLaunch = true;
                if (userCommand[0].equals("execute_script")) {
                    needLaunch = checkRecursion(userCommand[1], scriptScanner);
                }

                commandStatus = needLaunch ? launchCommand(userCommand) : new ExecutionResponse("Превышена максимальная глубина рекурсии");
                if (userCommand[0].equals("execute_script")) console.selectFileScanner(scriptScanner);
                executionOutput.append(commandStatus.getMassage()+"\n");
            } while (commandStatus.getExitCode() && !commandStatus.getMassage().equals("exit") && console.isCanReadln());

            console.selectConsoleScanner();
            if (!commandStatus.getExitCode() && !(userCommand[0].equals("execute_script") && !userCommand[1].isEmpty())) {
                executionOutput.append("Проверьте скрипт на корректность введенных данных!\n");
            }

            return new ExecutionResponse(commandStatus.getExitCode(), executionOutput.toString());
        } catch (FileNotFoundException exception) {
            return new ExecutionResponse(false, "Файл со скриптом не найден!");
        } catch (NoSuchElementException exception) {
            return new ExecutionResponse(false, "Файл со скриптом пуст!");
        } catch (IllegalStateException exception) {
            console.printError("Непредвиденная ошибка!");
            System.exit(0);
        } finally {
            scriptStack.remove(scriptStack.size() - 1);
        }
        return new ExecutionResponse("");
    }
//    Проверяет, существует ли файл.
//    Добавляет его в стек scriptStack.
//    Читает команды из файла и выполняет их в do-while.
//    Если обнаружена рекурсия, предлагает пользователю ввести максимальную глубину.
//    В конце удаляет файл из scriptStack

    /**
     * Функиция загрузки команды
     * @param userCommand Команда для запуска
     * @return Код завершения.
     */
    private ExecutionResponse launchCommand(String[] userCommand) {

        ExecutionResponse response;
        if (userCommand[0].equals("")) return new ExecutionResponse("");
        var command = CommandTypes.getByString(userCommand[0]);
        if(!commands.containsKey(command)) {
            command=null;
        }

        if (command == null)
            return new ExecutionResponse(false, "Команда '" + userCommand[0] + "' не найдена. Наберите 'help' для справки");
        StringBuilder sb = new StringBuilder();
        for(var e: userCommand) {
            sb.append(e + " ");
        }
        switch (userCommand[0]) {

            case "execute_script" -> {
                ExecutionResponse tmp = new ExecuteScript(console).apply(sb.toString().trim());
                if (!tmp.getExitCode()) return tmp;
                ExecutionResponse tmp2 = scriptMode(userCommand[1]);
                return new ExecutionResponse(tmp2.getExitCode(), tmp.getMassage() + "\n" + tmp2.getMassage().trim());
            }
            default -> {

                byte[] bytes = new byte[userCommand.length];
                if (command == CommandTypes.Add) {
                    try {
                        bytes = NetworkManager.serializer(new Container(command, AskManager.askCity(console, 0L).toStr(), user));
                    } catch (AskManager.AskBreak e) {
                        return new ExecutionResponse(false, "Отмена...");
                    }
                } else if (command == CommandTypes.Update) {
                    try {
                        bytes = NetworkManager.serializer(new Container(command, AskManager.askCity(console, Long.parseLong(userCommand[1])).toStr(), user));
                    } catch (AskManager.AskBreak e) {
                        return new ExecutionResponse(false, "Отмена...");
                    }
                } else if (command == CommandTypes.Help) {
                    console.println(new Help(console,commands).apply(String.join(" ", userCommand).trim()).getMassage());

                } else if (command == CommandTypes.Exit) {
                    bytes = NetworkManager.serializer(new Container(CommandTypes.Save, "", user));
                    networkManager.sendData(bytes);
                    return new Exit(console).apply(String.join(" ", userCommand).trim());

                } else if (command == CommandTypes.RemoveById | command == CommandTypes.RemoveLower) {
                    bytes = NetworkManager.serializer(new Container(command, userCommand[1], user));

                } else if (command == CommandTypes.FilterByMetersAboveSeaLevel) {
                    bytes = NetworkManager.serializer(new Container(command, userCommand[1], user));
                } else {
                    bytes = NetworkManager.serializer(new Container(command, "", user));
                }
                if (command != CommandTypes.Help) {
                    ExecutionResponse ans = networkManager.sendData(bytes);
                    response = networkManager.receiveData();
                    return response;
                }
                else return new ExecutionResponse(false,"");
            }
        }
    }
}
