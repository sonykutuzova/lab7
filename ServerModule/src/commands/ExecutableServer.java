package commands;

import data.User;
import utility.ExecutionResponse;

/**
 * Интерфейс, предназначенный для исполнения
 */
public interface ExecutableServer {
    ExecutionResponse apply(String arguments, User user);
}
