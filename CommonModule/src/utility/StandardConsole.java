package utility;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Консоль
 */
public class StandardConsole implements Console {
    private static final String P = "$ ";
    private static Scanner fileScanner = null;
    private static Scanner defScanner = new Scanner(System.in);


    /**
     * Выводит obj.toString() в консоль
     *
     * @param obj Объект для печати
     */
    public void print(Object obj) {
        System.out.print(obj);
    }

    /**
     * Выводит obj.toString() + \n в консоль
     *
     * @param obj Объект для печати
     */
    public void println(Object obj) {
        System.out.println(obj);
    }

    /**
     * Выводит ошибка: obj.toString() в консоль
     *
     * @param obj Ошибка для печати
     */
    public void printError(Object obj) {
        System.err.println("Error: " + obj);
    }
    /**
     * Чтение из файла
     *
     * @return возвращает следующую строку
     */
    public String readln() throws NoSuchElementException, IllegalStateException {
        return (fileScanner!=null?fileScanner:defScanner).nextLine();
    }
    /**
     * Можно ли прочитать
     */
    public boolean isCanReadln() throws IllegalStateException {
        return (fileScanner!=null?fileScanner:defScanner).hasNextLine();
    }


    /**
     * Выводит prompt1 текущей консоли
     */
    public void prompt() {
        print(P);
    }

    /**
     * @return prompt1 текущей консоли
     */

    public String getPrompt() {
        return P;
    }
    /**
     * заменяет сканер на введенный
     *
     * @param scanner новый сканер
     */
    public void selectFileScanner(Scanner scanner) {
        this.fileScanner = scanner;
    }
    /**
     * Возврашение к сканеру из консоли
     */
    public void selectConsoleScanner() {
        this.fileScanner = null;
    }
}
