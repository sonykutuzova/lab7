package managers;

import data.*;
import utility.Console;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.NoSuchElementException;

/**
 * Класс менеджера  по запросам данных из командной строки
 */
public class AskManager implements Serializable {
    /**
     * исключение для выхода из цикла опроса
     */
    public static class AskBreak extends Exception {
    }
    /**
     * Функция опроса города
     *
     * @param console консоль
     * @param id      уникальный номер учебной группы
     * @return возвращает город
     * @throws AskBreak исключение, возникающее при принудительном выходе из опроса
     */
    public static City askCity(Console console, long id) throws AskBreak {
        try {
            String name;
            while (true) {
                console.print("name: ");
                name = console.readln().trim();
                if (name.equals("exit")) throw new AskBreak();
                if (!name.isEmpty()) break;
            }
            var coordinates = askCoordinates(console);
            Float area;
            while (true) {
                console.print("area: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {try {
                    area = Float.parseFloat(line);
                    if (area>0) break; }
                catch(NumberFormatException e) { }
            }}
            Integer population;
            while (true) {
                console.print("population: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                try { population = Integer.parseInt(line);
                    if (population>0) break;
                    }
                catch(NumberFormatException e) { }
            }}
            float metersAboveSeaLevel;
            while (true) {
                console.print("metersAboveSeaLevel: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try { metersAboveSeaLevel = Float.parseFloat(line);
                        break;
                    }
                    catch(NumberFormatException e) { }}}
            LocalDate establishmentDate;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            while (true) {
                console.print("establishmentDate: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        Date date = formatter.parse(line);
                        establishmentDate = date.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        break;
                    }
                    catch(ParseException e) { }}}
            Government government;
            console.println(Government.names());
            while (true) {
                console.print("government: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        government = Government.valueOf(line.toUpperCase());
                        break;
                    }
                    catch(IllegalArgumentException e) { }}}
            StandardOfLiving standardOfLiving;
            console.println(StandardOfLiving.names());
            while (true) {
                console.print("standardOfLiving: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        standardOfLiving = StandardOfLiving.valueOf(line.toUpperCase());
                        break;
                    }
                    catch(IllegalArgumentException e) { }}}
            var governor = askHuman(console);
            return new City(id,  name,  coordinates, area,  population,  metersAboveSeaLevel,  establishmentDate,  government,  standardOfLiving,  governor);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }
    /**
     * Функция опроса координат
     *
     * @param console консоль
     * @return возвращает координаты
     * @throws AskBreak исключение, возникающее при принудительном выходе из опроса
     */
    public static Coordinates askCoordinates(Console console) throws AskBreak {
        try {
            double x;
            while (true) {
                console.print("coordinates.x: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.equals("")) {
                    try { x = Double.parseDouble(line); if (x <= 613) break; }
                    catch(NumberFormatException e) { }
                }
            }
            Double y;
            while (true) {
                console.print("coordinates.y: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.equals("")) {
                    try { y = Double.parseDouble(line); break; }
                    catch(NumberFormatException e) { }
                }
            }
            return new Coordinates(x, y);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }
    /**
     * Функция опроса человека
     *
     * @param console консоль
     * @return возвращает форму обучения
     * @throws AskBreak исключение, возникающее при принудительном выходе из опроса
     */
    public static Human askHuman(Console console) throws AskBreak {
        try {
            long age;
            while (true) {
                console.print("governor.age: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.equals("")) {
                    try { age = Long.parseLong(line); if (age>0) break; }
                    catch(NumberFormatException e) { }
                }
            }
            long height;
            while (true) {
                console.print("governor.height: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.equals("")) {
                    try { height = Long.parseLong(line); if (height>0) break; }
                    catch(NumberFormatException e) { }
                }
            }
            LocalDate birthday;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            while (true) {
                console.print("governor.birthday: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.equals("")) {
                    try {Date date1 = formatter.parse(line);
                        birthday = date1.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(); break; }
                    catch(ParseException e) { }
                }
            }
            return new Human(age, height, birthday);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }
    public static User askUser(Console console) throws AskBreak{
        String name=null;
        while(name==null || name.isEmpty()){
            console.print("Введите имя пользователя:");
            name = console.readln().trim();
            if (name.toLowerCase().equals("exit")) throw new AskBreak();
        }
        String password=null;
        while (password==null || password.isEmpty()) {
            console.print("Введите пароль:");
            password = console.readln().trim();
            if (password.toLowerCase().equals("exit")) throw new AskBreak();
        }
        return new User(name, password);
    }

}
