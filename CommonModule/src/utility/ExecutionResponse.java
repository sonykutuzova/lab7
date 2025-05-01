package utility;

import java.io.Serializable;

/**
 * Класс ответа о выполнении
 */
public class ExecutionResponse implements Serializable {
    /**
     * код завершения
     */
    private boolean exitCode;
    /**
     * сообщение
     */
    private String message;
    /**
     * Конструктор
     *
     * @param code код завершения
     * @param s    сообщение
     */

    public ExecutionResponse(boolean code, String s) {
        exitCode = code;
        message = s;
    }
    /**
     * Конструктор
     *
     * @param s сообщение
     */
    public ExecutionResponse(String s) {
        this(true, s);
    }
    /**
     * Функция получения кода завершения
     *
     * @return возвращает код завершения
     */
    public boolean getExitCode() { return exitCode; }

    /**
     * Функция получения сообщения
     *
     * @return возвращает сообщение
     */
    public String getMassage() { return message; }
    /**
     * @return возвращает объект, переведенный в строковое представление
     */
     public String toString() {
        return String.valueOf(exitCode) + ";" + message;
    }
}
