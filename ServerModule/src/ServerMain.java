import commands.*;
import managers.CollectionManager;
import managers.CommandManager;
import managers.DbManager;
import managers.DumpManager;
import utility.ServerRunner;
import utility.StandardConsole;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.*;

public class ServerMain {
    static {
        try (FileInputStream ins = new FileInputStream("log.config")) { // полный путь до файла с конфигами
            LogManager.getLogManager().readConfiguration(ins);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }
        public static final Logger logger = Logger.getLogger(ServerMain.class.getName());

        public static void main(String[] args) {
        StandardConsole console = new StandardConsole();
        String filename;

        if (args.length == 0) {
            console.printError("Не было передано имя файла");
            filename = "";
        } else {
            filename = args[0];
        }


            CollectionManager collectionManager = new CollectionManager(new DbManager(filename, "jdbc:postgresql://db:5432/studs"), console);// jdbc:postgresql://db:5432/studs
            CommandManager commandManager = new CommandManager() {{
                register("add", new Add(console, collectionManager));
                register("show", new Show(console, collectionManager));
                register("update", new Update(console, collectionManager));
                register("remove_by_id", new RemoveById(console, collectionManager));
                register("clear", new Clear(console, collectionManager));
                //register("save", new Save(console, collectionManager));
                register("filter_by_meters_above_sea_level", new FilterByMetersAboveSeaLevel(console, collectionManager));
                register("info", new Info(console, collectionManager));
                register("reorder", new Reorder(console, collectionManager));
                register("max_by_population", new MaxByPopulation(console, collectionManager));
                register("print_field_descending_meters_above_sea_level", new PrintFieldDescendingMetersAboveSeaLevel(console, collectionManager));
                register("remove_lower", new RemoveLower(console, collectionManager));
                register("remove_first", new RemoveFirst(console, collectionManager));
            }};
            if (!collectionManager.init()) System.exit(0);
            logger.log(Level.INFO,"Создание сервера");
            new ServerRunner(commandManager, collectionManager, console, 31145, logger).run(31145);

        }
    }