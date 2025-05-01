package managers;

import data.*;
import utility.ExecutionResponse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;


public class DbManager {

    private Connection connection;

    /// конструктор

    public DbManager(String filename, String url) {
        Properties info = new Properties();//работа с файлами конфигуряций
        try {
            info.load(new FileInputStream(filename));//загружаю сюда свой конфиг (my.cfg)
            System.out.println("Файл загружен");
        }
        catch (FileNotFoundException e){ }//файл не найден
        catch (IOException e){ }//ошибки чтения файла (нет прав доступа)

        //"jdbc:postgresql://db:5432/studs"  ----- подключение к этой базе данных (url)
        try {
            Class.forName("org.postgresql.Driver"); //попытка загрузить драйвер
        }catch (ClassNotFoundException e){
            System.out.println("Не получается загрузить драйвер, он не найден");
            e.printStackTrace();
        }
        try{
            System.out.println(info.getProperty("name") + info.getProperty("password"));
            connection = DriverManager.getConnection(url, info.getProperty("name"), info.getProperty("password"));
            System.out.println("Законнекчено с базой данных");
            connection.setAutoCommit(false);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Ошибка при законнекчивании с базой данных"+ e);
        }
    }

    /// завершение подключения

    public ExecutionResponse closeConnection(){
        try {
            connection.close();
            return new ExecutionResponse(true, "Подключение закрыто");
        }catch (Exception e){
            return new ExecutionResponse(false, "Ошибка при завершении подключения к бд"+e.getMessage());
        }
    }

    /// проверка пользователя

    public Integer checkUser(User user){
        String req = """
select * from "Users" where name = ?""";// достаем пользователя
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, user.getName().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {// значит такой пользователь есть
                    String storedSalt = rs.getString("salt");// достаем его соль и хэш
                    byte[] storedHash = rs.getBytes("hash");
                    if (checkPassword(user.getPassword(), storedHash, storedSalt)) {// проверяем пароль
                        return rs.getInt("id"); // Успех, возвращаем ид
                    }
                    return -1; // Неверный пароль
                }return -2; // Пользователь не найден
            }
        }catch (SQLException e){
            e.printStackTrace();
            return -3;// Ошибка при проверке пользователя в бд
        }
    }

    /// регистрация пользователя

    public ExecutionResponse registerUser(User user){
        // Проверяем, свободно ли имя
        if (checkUser(user) != -2) {
            return new ExecutionResponse(false, "Имя пользователя занято");
        }

        String req = """
insert into "Users" (name, hash, salt) values (?, ?, ?)""";

        try{
            //connection.setAutoCommit(false);
            // автоматически сгенерированный ид пользователя
            try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
                String salt = generateSalt(); // делаем соль
                byte[] hash = hashPassword(user.getPassword(), salt); // Хэш
                ps.setString(1, user.getName().toLowerCase());
                ps.setBytes(2, hash);
                ps.setString(3, salt);
                // проверка что 1 строка извинилась
                if (ps.executeUpdate() > 0) {
                    ResultSet keys = ps.getGeneratedKeys();// возвращает сет с сгенерированными ключами ( с одним )
                    if(keys.next()){
                        connection.commit();// сохраняем
                        return new ExecutionResponse(true, String.valueOf(keys.getInt(1)));// возвращаем id добавленного
                    }
                }
                connection.rollback();// откатываем назад
                return new ExecutionResponse(false, "Ошибка регистрации");
            }
            }catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Ошибка отката транзакции: " + ex.getMessage());
            }
            return new ExecutionResponse(false, "Ошибка при сохранении в базы данных: " + e.getMessage());
        }
    }

    /// чтение коллекции

    public ExecutionResponse readCollection(ArrayList<City> collection, Map<Long, Integer> usersElements){
        String sql = """
        SELECT *
        FROM "City" l
        LEFT JOIN "Coordinates" c ON l.coordinates_id = c.id
        LEFT JOIN "Human" d ON l.governor_id = d.id
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql);// подключение к бд и выполнение запроса
             ResultSet rs = ps.executeQuery()) {
            // чтение данных построчно
            while (rs.next()) {
                // Создаём Coordinates (если есть)
                Coordinates coordinates = null;
                if (rs.getDouble("x")!=0) {  // Проверка на NULL
                    coordinates = new Coordinates(
                        rs.getDouble("x"),
                        rs.getDouble("y")
                    );
                }

                // Создаём человека если есть
                Human governor = null;
                if (rs.getLong("age") != 0) {
                    governor = new Human(
                            rs.getLong("age"),
                            rs.getLong("height"),
                            rs.getDate("birthday").toLocalDate()
                    );
                }

                // Создаём город
                City city = new City(
                        rs.getLong("id"),
                        rs.getString("name"),
                        coordinates,
                        rs.getDate("creation_date").toLocalDate(),  // LocalDate вместо java.sql.Date
                        rs.getFloat("area"),
                        rs.getInt("population"),
                        rs.getFloat("metersAboveSeaLevel"),
                        rs.getDate("establishmentDate").toLocalDate(),
                        Government.valueOf(rs.getString("government")),
                        StandardOfLiving.valueOf(rs.getString("standardOfLiving")),
                        governor
                        );

                collection.add(city);
                usersElements.put(city.getId(), rs.getInt("owner_id"));

            }
            return new ExecutionResponse(true, "Коллекция загружена в коллекция");

        } catch (SQLException e) {
            System.err.println("Ошибка загрузки коллекции: " + e.getMessage());
            return new ExecutionResponse(false, "Ошибка чтения");
        }
    }

    /// добавление элемента в коллекцию

    public ExecutionResponse addElement(City city, Long ownerId) {
        // SQL-запросы для вставки в связанные таблицы
        String reqCoordinates = """
INSERT INTO "Coordinates" (x, y) VALUES (?, ?)""";
        String reqHuman = """
        INSERT INTO "Human" (age, height, birthday) VALUES (?, ?, ?)""";
        String reqCity = """
                INSERT INTO "City" (name, coordinates_id, creation_date, area, population, "metersAboveSeaLevel", "establishmentDate", government, "standardOfLiving", governor_id, owner_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?::government, ?::"standardOfLiving", ?, ?)""";

        // ID для связанных записей
        Long coordinatesId = -1L;
        Long governorId = -1L;

        try {
            // Отключаем авто-коммит для управления транзакцией вручную
            //connection.setAutoCommit(false);

            // 1. Вставка координат
            try (PreparedStatement psCoordinates = connection.prepareStatement(reqCoordinates, Statement.RETURN_GENERATED_KEYS)) {
                psCoordinates.setDouble(1, city.getCoordinates().getX());
                psCoordinates.setDouble(2, city.getCoordinates().getY());

                int affectedRows = psCoordinates.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = psCoordinates.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            coordinatesId = generatedKeys.getLong(1);
                        } else {
                            throw new SQLException("Не удалось получить ID координат");
                        }
                    }
                } else {
                    throw new SQLException("Ошибка при вставке координат");
                }
            }

            // 2. Вставка губернатора (если есть)
            if (city.getGovernor() != null) {
                try (PreparedStatement psHuman = connection.prepareStatement(reqHuman, Statement.RETURN_GENERATED_KEYS)) {
                    Human governor = city.getGovernor();
                    psHuman.setLong(1, governor.getAge());
                    psHuman.setLong(2, governor.getHeight());
                    psHuman.setDate(3, java.sql.Date.valueOf(governor.getBirthday()));

                    int affectedRows = psHuman.executeUpdate();
                    if (affectedRows > 0) {
                        try (ResultSet generatedKeys = psHuman.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                governorId = generatedKeys.getLong(1);
                            } else {
                                throw new SQLException("Не удалось получить ID губернатора");
                            }
                        }
                    } else {
                        throw new SQLException("Ошибка при вставке губернатора");
                    }
                }
            }

            // 3. Вставка города
            try (PreparedStatement psCity = connection.prepareStatement(reqCity, Statement.RETURN_GENERATED_KEYS)) {
                // Установка параметров
                psCity.setString(1, city.getName());
                psCity.setLong(2, coordinatesId);
                psCity.setDate(3, java.sql.Date.valueOf(city.getCreationDate()));
                psCity.setFloat(4, city.getArea());
                psCity.setInt(5, city.getPopulation());
                psCity.setFloat(6, city.getMetersAboveSeaLevel());
                psCity.setDate(7, city.getEstablishmentDate() != null ?
                        java.sql.Date.valueOf(city.getEstablishmentDate()) : null);
                psCity.setString(8, city.getGovernment().name());
                psCity.setString(9, city.getStandardOfLiving().name());
                psCity.setObject(10, governorId > 0 ? governorId : null); // Может быть NULL
                psCity.setLong(11, ownerId);

                int affectedRows = psCity.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = psCity.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            connection.commit(); // Фиксируем всю транзакцию
                            return new ExecutionResponse(true, String.valueOf(generatedKeys.getLong(1)));
                        }
                    }
                }
                throw new SQLException("Ошибка при вставке города");
            }
        } catch (SQLException e) {
            try {
                connection.rollback(); // Откатываем при ошибке
            } catch (SQLException rollbackEx) {
                System.err.println("Ошибка при откате транзакции: " + rollbackEx.getMessage());
            }
            return new ExecutionResponse(false, "Ошибка при добавлении в бд: " + e.getMessage());
        }
    }


    public ExecutionResponse updateElement(City city) {
        // SQL-запросы для обновления связанных данных
        String sqlUpdateCoordinates = """
                UPDATE "Coordinates" SET x=?, y=? WHERE id = 
                (SELECT coordinates_id FROM "City" WHERE id=?)""";
        String sqlUpdateHuman = """
                UPDATE "Human" SET age=?, height=?, birthday=? WHERE id = 
                (SELECT governor_id FROM "City" WHERE id=?)""";
        String sqlUpdateCity = """
                UPDATE "City" SET name=?, creation_date=?, area=?, population=?, 
                 "metersAboveSeaLevel"=?, "establishmentDate"=?, government=?::government,
                "standardOfLiving"=?::"standardOfLiving" WHERE id=?""";

        try {
            // Отключаем авто-коммит для управления транзакцией
            connection.setAutoCommit(false);

            // 1. Обновляем координаты
            try (PreparedStatement psCoordinates = connection.prepareStatement(sqlUpdateCoordinates)) {
                psCoordinates.setDouble(1, city.getCoordinates().getX());
                psCoordinates.setDouble(2, city.getCoordinates().getY());
                psCoordinates.setLong(3, city.getId());

                int affectedRows = psCoordinates.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Координаты не найдены для обновления");
                }
            }

            // 2. Обновляем губернатора (если есть)
            if (city.getGovernor() != null) {
                try (PreparedStatement psHuman = connection.prepareStatement(sqlUpdateHuman)) {
                    Human governor = city.getGovernor();
                    psHuman.setLong(1, governor.getAge());
                    psHuman.setLong(2, governor.getHeight());
                    psHuman.setDate(3, java.sql.Date.valueOf(governor.getBirthday()));
                    psHuman.setLong(4, city.getId());

                    int affectedRows = psHuman.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Губернатор не найден для обновления");
                    }
                }
            }

            // 3. Обновляем сам город
            try (PreparedStatement psCity = connection.prepareStatement(sqlUpdateCity)) {
                psCity.setString(1, city.getName());
                psCity.setDate(2, java.sql.Date.valueOf(city.getCreationDate()));
                psCity.setFloat(3, city.getArea());
                psCity.setInt(4, city.getPopulation());
                psCity.setFloat(5, city.getMetersAboveSeaLevel());
                psCity.setDate(6, city.getEstablishmentDate() != null ?
                        java.sql.Date.valueOf(city.getEstablishmentDate()) : null);
                psCity.setString(7, city.getGovernment().name());
                psCity.setString(8, city.getStandardOfLiving().name());
                psCity.setLong(9, city.getId());

                int affectedRows = psCity.executeUpdate();
                if (affectedRows > 0) {
                    connection.commit(); // Фиксируем транзакцию
                    return new ExecutionResponse(true, "Город успешно обновлен");
                }
                throw new SQLException("Город не найден для обновления");
            }
        } catch (SQLException e) {
            try {
                connection.rollback(); // Откатываем при ошибке
            } catch (SQLException rollbackEx) {
                System.err.println("Ошибка при откате: " + rollbackEx.getMessage());
            }
            return new ExecutionResponse(true, "Ошибка обновления: " + e.getMessage());
        }

    }

    public ExecutionResponse removeElement(Long id) {
//        // SQL-запросы для удаления связанных данных
//        String deleteCoordinatesSql = """
//                DELETE FROM "Coordinates" WHERE "Coordinates".id =
//                (SELECT coordinates_id FROM "City" WHERE "City".id = ?)""";
//        String deleteHumanSql = """
//                DELETE FROM "Human" WHERE "Human".id =
//                (SELECT governor_id FROM "City" WHERE "City".id = ?)""";
//        String deleteCitySql = """
//DELETE FROM "City" WHERE id = ?""";
//
//        try {
//            // Отключаем авто-коммит для управления транзакцией
//            //connection.setAutoCommit(false);
//
//            // 1. Удаляем координаты города
//            try (PreparedStatement deleteCoordinatesStmt = connection.prepareStatement(deleteCoordinatesSql)) {
//                deleteCoordinatesStmt.setLong(1, id);
//                int affectedRows = deleteCoordinatesStmt.executeUpdate();
//                // Не бросаем исключение если координатов нет (может быть NULL)
//            }
//
//            // 2. Удаляем губернатора (если есть)
//            try (PreparedStatement deleteHumanStmt = connection.prepareStatement(deleteHumanSql)) {
//                deleteHumanStmt.setLong(1, id);
//                deleteHumanStmt.executeUpdate(); // Может быть 0 если губернатора нет
//            }
//
//            // 3. Удаляем сам город
//            try (PreparedStatement deleteCityStmt = connection.prepareStatement(deleteCitySql)) {
//                deleteCityStmt.setLong(1, id);
//                int affectedRows = deleteCityStmt.executeUpdate();
//
//                if (affectedRows > 0) {
//                    connection.commit(); // Фиксируем транзакцию
//                    return new ExecutionResponse(true, "Город успешно удален");
//                } else {
//                    throw new SQLException("Город с указанным ID не найден");
//                }
//            }
//        } catch (SQLException e) {
//            try {
//                connection.rollback(); // Откатываем при ошибке
//            } catch (SQLException rollbackEx) {
//                System.err.println("Ошибка при откате транзакции: " + rollbackEx.getMessage());
//            }
//            return new ExecutionResponse(false, "Ошибка удаления: " + e.getMessage());
//        }
        // Единый SQL-запрос с использованием каскадного удаления
        String deleteCitySql = "DELETE FROM \"City\" WHERE id = ?";


            try (PreparedStatement deleteCityStmt = connection.prepareStatement(deleteCitySql)) {
                deleteCityStmt.setLong(1, id);
                int affectedRows = deleteCityStmt.executeUpdate();

                if (affectedRows > 0) {
                    connection.commit(); // Фиксируем транзакцию
                    return new ExecutionResponse(true, "Город и связанные данные успешно удалены");
                } else {
                    connection.rollback();
                    return new ExecutionResponse(false, "Город с ID " + id + " не найден");
                }
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Ошибка при откате транзакции: " + rollbackEx.getMessage());
            }
            return new ExecutionResponse(false, "Ошибка удаления: " + e.getMessage());
        }
    }
    public ExecutionResponse removeCollection(Long ownerId) {
        // SQL-запросы для удаления связанных данных
        String deleteCoordinatesSql = """
                DELETE FROM "Coordinates" WHERE id IN
                (SELECT coordinates_id FROM "City" WHERE owner_id = ?)""";
        String deleteHumanSql = """
                DELETE FROM "Human" WHERE id IN
                (SELECT governor_id FROM "City" WHERE owner_id = ?)""";
        String deleteCitySql = """
        DELETE FROM "City" WHERE owner_id = ?""";

        try {
            // Отключаем авто-коммит для управления транзакцией
            //connection.setAutoCommit(false);

            int totalDeleted = 0;

            // 1. Удаляем координаты всех городов пользователя
            try (PreparedStatement deleteCoordinatesStmt = connection.prepareStatement(deleteCoordinatesSql)) {
                deleteCoordinatesStmt.setLong(1, ownerId);
                totalDeleted = deleteCoordinatesStmt.executeUpdate();
                // Не бросаем исключение если координатов нет (может быть NULL)
            }

            // 2. Удаляем губернаторов (если есть)
            try (PreparedStatement deleteHumanStmt = connection.prepareStatement(deleteHumanSql)) {
                deleteHumanStmt.setLong(1, ownerId);
                deleteHumanStmt.executeUpdate(); // Может быть 0 если губернаторов нет
            }

            // 3. Удаляем сами города
            try (PreparedStatement deleteCityStmt = connection.prepareStatement(deleteCitySql)) {
                deleteCityStmt.setLong(1, ownerId);
                int citiesDeleted = deleteCityStmt.executeUpdate();

                if (citiesDeleted >= 0) {
                    connection.commit(); // Фиксируем транзакцию
                    return new ExecutionResponse(true, "Успешно удалено городов: " + citiesDeleted);
                }
                throw new SQLException("Не удалось удалить города");
            }
        } catch (SQLException e) {
            try {
                connection.rollback(); // Откатываем при ошибке
            } catch (SQLException rollbackEx) {
                System.err.println("Ошибка при откате транзакции: " + rollbackEx.getMessage());
            }
            return new ExecutionResponse(false, "Ошибка удаления коллекции: " + e.getMessage());
        }
    }

    ///removeFirst

    public ExecutionResponse removeFirst(Long ownerId) {
        String findFirstSql = """
        SELECT id FROM "City" WHERE owner_id = ? ORDER BY id LIMIT 1""";
        String deleteSql = """
                DELETE FROM "City" WHERE id = ?""";

        try {
            // Находим первый ID
            Long idToDelete = null;
            try (PreparedStatement psFind = connection.prepareStatement(findFirstSql)) {
                psFind.setLong(1, ownerId);
                try (ResultSet rs = psFind.executeQuery()) {
                    if (rs.next()) {
                        idToDelete = rs.getLong("id");
                    }
                }
            }

            // Удаляем если нашли
            if (idToDelete != null) {
                return removeElement(idToDelete); // Используем метод removeElement
            }
            return new ExecutionResponse(false, "Нет элементов для удаления");
        } catch (SQLException e) {
            return new ExecutionResponse(false, "Ошибка: " + e.getMessage());
        }
    }

    /// removeLower

    public ExecutionResponse removeLower(Long ownerId, Float area) {
        String findIdsSql = """
SELECT id FROM "City" WHERE owner_id = ? AND area < ?""";
        String deleteSql = "DELETE FROM city WHERE id = ?";

        try {
            // Находим все ID для удаления
            List<Long> idsToDelete = new ArrayList<>();
            try (PreparedStatement psFind = connection.prepareStatement(findIdsSql)) {
                psFind.setLong(1, ownerId);
                psFind.setFloat(2, area);
                try (ResultSet rs = psFind.executeQuery()) {
                    while (rs.next()) {
                        idsToDelete.add(rs.getLong("id"));
                    }
                }
            }

            // Удаляем по одному
            int deletedCount = 0;
            for (Long id : idsToDelete) {
                ExecutionResponse result = removeElement(id);
                if (result.getExitCode()) {
                    deletedCount++;
                }
            }

            return new ExecutionResponse(true, "Удалено городов: " + deletedCount);
        } catch (SQLException e) {
            return new ExecutionResponse(false, "Ошибка при удалении города: " + e.getMessage());
        }
    }

    public boolean checkPassword(String inputPassword, byte[] passwordHash, String salt) {
        if (inputPassword == null) return false;
        byte[] inputHash = hashPassword(inputPassword, salt);
        return Arrays.equals(passwordHash, inputHash);
    }
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16 байт = 128 бит
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private byte[] hashPassword(String password, String salt) {
        try {
            String saltedPassword = password + salt;
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(saltedPassword.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка хэширования", e);
        }
    }
}
