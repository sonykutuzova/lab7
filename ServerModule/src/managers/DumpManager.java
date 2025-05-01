package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import data.*;
import utility.*;
import utility.Console;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * Класс файлового менеджера команд
 */
public class DumpManager {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    /**
     * название файла
     */
    private final String fileName;
    /**
     * консоль
     */
    private final Console console;
    /**
     * конструктор
     *
     * @param fileName название файла
     * @param console  консоль
     */
    public DumpManager(String fileName, Console console) {
        this.fileName = fileName;
        this.console = console;
    }

    /**
     * Записывает коллекцию в файл.
     * @param collection коллекция
     */
    public void writeCollection(Collection<City> collection) {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName))) {
            String json = gson.toJson(collection);
            bos.write(json.getBytes(StandardCharsets.UTF_8));
            bos.flush();
            console.println("Коллекция успешно сохранена в файл!");
        } catch (IOException exception) {
            console.printError("Загрузочный файл не может быть открыт!");
        }
    }
    /**
     * Считывает коллекцию из файл.
     * @return Считанная коллекция
     */
    public ArrayList<City> readCollection() {
        if (fileName != null && !fileName.isEmpty()) {
            try (FileReader fileReader = new FileReader(fileName)) {
                Type collectionType = new TypeToken<ArrayList<City>>() {}.getType();
                BufferedReader reader = new BufferedReader(fileReader);

                StringBuilder jsonString = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.equals("")) {
                        jsonString.append(line);
                    }
                }

                if (jsonString.length() == 0) {
                    jsonString = new StringBuilder("[]");
                }

                ArrayList<City> collection = gson.fromJson(jsonString.toString(),
                        collectionType);

                console.println("Коллекция успешна загружена!");
                return collection;

            } catch (FileNotFoundException exception) {
                console.printError("Загрузочный файл не найден!");
            } catch (NoSuchElementException exception) {
                console.printError("Загрузочный файл пуст!");
            } catch (JsonParseException exception) {
                console.printError("В загрузочном файле не обнаружена необходимая коллекция!");
            } catch (IllegalStateException | IOException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        } else {
            console.printError("Аргумент командной строки с загрузочным файлом не найден!");
        }
        return new ArrayList<>();
    }
}
