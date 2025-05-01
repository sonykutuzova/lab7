package utility;

import commands.CommandTypes;
import commands.Container;
import managers.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import data.User;
import managers.CollectionManager;
import utility.ExecutionResponse;
import utility.StandardConsole;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerRunner {
    private Logger logger;
    /**
     * Менеджр коллекции
     */
    private CollectionManager collectionManager;
    /**
     * Менеджр комманд
     */
    private CommandManager commandManager;
    private NetworkManager networkManager;

    private static ExecutorService fixedThreadPool;
    /**
     * Конструктор
     *
     * @param filename имя файла
     */
    private StandardConsole console;
    private int port;
    public ServerRunner(CommandManager commandManager, CollectionManager collectionManager, StandardConsole console, int port, Logger logger) {
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
        this.console = console;
        this.port = port;
        this.fixedThreadPool= Executors.newFixedThreadPool(100);
        this.logger = logger;
        networkManager = new NetworkManager(port, logger);
    }

    /**
     * Запуск команды на сервере, если у команды есть аргументы
     *
     * @param message команда
     * @param arguments аргумент
     * @return возвращает ответ о выполнении команды
     */
    public ExecutionResponse launchCommand(CommandTypes message, String arguments, User user) {
        if (message!=null){
            var command = commandManager.getCommands().get(message.Type());
            ExecutionResponse response;
            if (message.Type().equals("")) response = new ExecutionResponse( false, "Перепроверьте корректность данных");
            if (command==null) response = new ExecutionResponse(false, "Команда не найдена, перепроверьте данные");
            else if (arguments.equals("")) response = new ExecutionResponse(false, "Перепроверьте аргумент");
            else response = command.apply(arguments, user);
            return response;
        }
        return new ExecutionResponse(false, "Перепроверьте корректность данных");
    }

    /**
     * Запуск команды на сервере, если у команды нет аргументов
     *
     * @param message команда
     * @return возвращает ответ о выполнении команды
     */
    public ExecutionResponse launchCommand(CommandTypes message, User user) {
        if (message!=null){
            var command = commandManager.getCommands().get(message.Type());
            ExecutionResponse response;
            if (message.Type().equals("")) response = new ExecutionResponse(false, "Перепроверьте корректность данных");
            if (command==null) response = new ExecutionResponse(false, "Команда не найдена, перепроверьте данные");
            else response = command.apply("", user);
            logger.log(Level.INFO, "Успешно выполнена команда: " + message.Type());
            return response;
        }
        logger.log(Level.WARNING, "Ошибка при выполнении команды: " + message.Type());
        return new ExecutionResponse(false, "Перепроверьте корректность данных");
    }

    public void launch(Container container) {

        ExecutionResponse answer = null;

        // Проверка состояния контейнера
        if (container.getCommandType() == null && container.getUser() != null) {
            System.out.println("4");

            answer = collectionManager.checkUser(container.getUser());
            //System.out.println("Ответ проверки пользователя: " + (answer != null ? answer.message() : "null"));
            logger.log(Level.INFO, "Новое подключение " + answer.getMassage());
        } else if (container.getCommandType() != null && !container.getArgs().isEmpty()) {
            System.out.println("3");
            if (collectionManager.isUserSigned(container.getUser())) {
                answer = launchCommand(container.getCommandType(), container.getArgs(), container.getUser());
            } else {
                answer = new ExecutionResponse(false, "Вы не были авторизованы");
            }
        } else if (container.getCommandType() != null && container.getArgs().isEmpty()) {
            System.out.println("1");
            if (collectionManager.isUserSigned(container.getUser())) {
                answer = launchCommand(container.getCommandType(), container.getUser());
            } else {
                answer = new ExecutionResponse(false, "Вы не были авторизованы");
            }
        }
        System.out.println("2");

        ExecutionResponse finalAnswer = answer;

        if (finalAnswer != null) {
            System.out.println("Ответ финального ответа: " + finalAnswer.getMassage());
        } else {
            System.out.println("finalAnswer составляет null.");
        }

        fixedThreadPool.submit(() -> {
            networkManager.sendPacket(new Container(finalAnswer));
        });
    }
    public void run(int port){
        logger.log(Level.INFO,"Развертывание сервера");
        var answer = networkManager.startPolling();
        if (!answer.getExitCode()){
            console.printError(answer.getMassage());
            System.exit(0);
        }
        logger.log(Level.INFO,answer.getMassage());
        while (true){
            Container container = networkManager.getRequest();
            System.out.println(container.getCommandType());
            fixedThreadPool.submit(() -> {  try {
                launch(container);
            } catch (Exception e) {
                e.printStackTrace();
            }});
        }
    }

}
