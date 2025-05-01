
package managers;

import data.City;
import data.User;
import utility.ExecutionResponse;
import utility.StandardConsole;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Класс менеджера коллекции
 */
public class CollectionManager {
    /**
     * переменная для выдачи следующего id
     */
    private long currentId = 0;
    /**
     * словарь для хранения элементов коллекции по id
     */
    private Map<Long, City> cityMap = new HashMap<>();
    /**
     * хранимая коллекция
     */
    private ArrayList<City> collection = new ArrayList<>();
    /**
     * Время последней инициализации менеджера
     */
    private LocalDateTime lastInitTime;
    /**
     * время последнего сохранения коллекции
     */
    private LocalDateTime lastSaveTime;
    /**
     * будет ли сортировка
     */
    private boolean isAscendingSort;
    /**
     * какому пользователю какие элементы принадлежат
     */
    private Map<Long, Integer> usersElements = new HashMap<>();
    /**
     * файловый менеджер
     */
    private final DbManager dbmanager;
    /**
     * подключенные пользователи
     */
    private Map<String, Integer> users = new HashMap<>();
    /**
     * консоль для вывода
     */
    private StandardConsole console;
    /**
     * блокировка для многопоточности
     */
    private ReentrantLock lock;

    /**
     * Конструктор
     */
    public CollectionManager(DbManager dbmanager, StandardConsole console) {
        this.lastInitTime = null;
        this.lastSaveTime = null;
        this.dbmanager = dbmanager;
        this.console = console;
        this.lock = new ReentrantLock();
    }

    /**
     * @return возвращает время последней инициализации менеджера
     */
    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    /**
     * Устанавливает будет ли сортировка в обратном порядке
     */
    public void setIsAscendingSort(boolean isAscendingSort) {
        lock.lock();
        try {
            this.isAscendingSort = isAscendingSort;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return возвращает время последнего сохранения коллекции
     */
    public LocalDateTime getLastSaveTime() {
        return lastSaveTime;
    }

    /**
     * Функция получения коллекции из менеджера
     * @return возвращает хранимую в менеджере коллекцию
     */
    public ArrayList<City> getCollection() {
        lock.lock();
        try {
            return collection;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Получение элемента по ID
     * @param id уникальный идентификатор
     * @return объект City или null если не найден
     */
    public City byId(long id) {
        lock.lock();
        try {
            return cityMap.get(id);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Проверка наличия элемента в коллекции
     * @param e проверяемый элемент
     * @return true если элемент присутствует
     */
    public boolean isContain(City e) {
        lock.lock();
        try {
            return e == null || byId(e.getId()).equals(null);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Получение первого элемента коллекции
     * @return первый элемент или null если коллекция пуста
     */
    public City getFirst(User user) {
        lock.lock();
        try {
            for (City e: collection) {
                if(users.get(user.getName().toLowerCase()).equals(usersElements.get(e.getId()))) {
                    return e;
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }
    public City getFirst() {
        lock.lock();
        try {
            return collection.get(0);
        } finally {
            lock.unlock();
        }
    }

    public boolean init() {
        collection.clear();
        cityMap.clear();
        usersElements.clear();
        ExecutionResponse ans = dbmanager.readCollection(collection, usersElements);
        lastInitTime = LocalDateTime.now();
        if(ans.getExitCode()){
            for (var e : collection)
                if (byId(e.getId()) != null) {
                    collection.clear();
                    return false;
                } else {
                    cityMap.put(e.getId(), e);
                }
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Получение последнего элемента коллекции
     * @return последний элемент или null если коллекция пуста
     */
    public City getLast() {
        lock.lock();
        try {
            if (!collection.isEmpty()) return collection.get(collection.size() - 1);
            return null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Обновление коллекции с сортировкой
     */
    public void update() {
        lock.lock();
        try {
            collection.sort(Comparator.comparing(City::getName));
            if (isAscendingSort) Collections.reverse(collection);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Добавление элемента в коллекцию
     * @param e добавляемый элемент
     * @param user пользователь, добавляющий элемент
     * @return результат выполнения
     */
    public ExecutionResponse add(City e, User user) {
        lock.lock();
        try {
            ExecutionResponse response = dbmanager.addElement(e, Long.valueOf(users.get(user.getName().toLowerCase())));
            if (response.getExitCode()) {
                long id = Long.parseLong(response.getMassage());
                e.setId(id);
                cityMap.put(id, e);
                usersElements.put(id, users.get(user.getName().toLowerCase()));
                collection.add(e);
                update();
                return new ExecutionResponse(true, "Элемент успешно добавлен");
            }
            return response;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Обновление элемента коллекции
     * @param e обновляемый элемент
     * @return результат выполнения
     */
    public ExecutionResponse update(City e) {
        lock.lock();
        try {
            if (isContain(e)) return new ExecutionResponse(false, "Элемент не найден");
            ExecutionResponse response = dbmanager.updateElement(e);
            if (response.getExitCode()) {
                collection.remove(byId(e.getId()));
                cityMap.put(e.getId(), e);
                collection.add(e);
                update();
            }
            return response;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Удаление элемента коллекции
     * @param e удаляемый элемент
     * @return true если удаление успешно
     */
    public boolean remove(City e) {
        lock.lock();
        try {
            long curId = e.getId();
            if (byId(curId) == null || usersElements.get(e.getId()) == null) return false;
            collection.remove(cityMap.remove(curId));
            usersElements.remove(curId);
            update();
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Удаление элемента по ID
     * @param cityId ID элемента
     * @return результат выполнения
     */
    public ExecutionResponse remove(long cityId) {
        lock.lock();
        try {
            ExecutionResponse response = dbmanager.removeElement(cityId);
            System.out.println(response.getMassage());
            if (response.getExitCode()) {
                if (byId(cityId) == null) return new ExecutionResponse(false, "Элемент не найден");
                collection.remove(cityMap.remove(cityId));
                usersElements.remove(cityId);
                update();
                return new ExecutionResponse(true, "Элемент успешно удален");
            }
            return response;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Очистка коллекции пользователя
     * @param user пользователь
     * @return результат выполнения
     */
    public ExecutionResponse clear(User user) {
        lock.lock();
        try {
            if (collection.isEmpty()) return new ExecutionResponse(true, "Коллекция уже пуста");

            ExecutionResponse response = dbmanager.removeCollection(Long.valueOf(users.get(user.getName().toLowerCase())));
            if (response.getExitCode()) {
                Iterator<City> iterator = collection.iterator();
                List<Long> ids = new ArrayList<>();
                while (iterator.hasNext()) {
                    City e = iterator.next();
                    if (usersElements.get(e.getId()) == users.get(user.getName())) {
                        ids.add(e.getId());
                    }
                }
                for (Long id : ids) {
                    collection.remove(cityMap.remove(id));
                    usersElements.remove(id);
                }
                update();
            }
            return response;
        } finally {
            lock.unlock();
        }
    }


    /**
     * Получение ID пользователя
     * @param user пользователь
     * @return ID пользователя или null если не найден
     */
    public Integer getUserId(User user) {
        lock.lock();
        try {
            return users.get(user.getName().toLowerCase());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Получение карты элементов пользователей
     * @return карта элементов пользователей
     */
    public Map<Long, Integer> getUsersElements() {
        lock.lock();
        try {
            return usersElements;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Получение списка подключенных пользователей
     * @return карта пользователей
     */
    public Map<String, Integer> getUsers() {
        return users;
    }

    /**
     * Проверка авторизации пользователя
     * @param user пользователь
     * @return true если пользователь авторизован
     */
    public boolean isUserSigned(User user) {
        lock.lock();
        try {
            return users.get(user.getName().toLowerCase()) != null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Проверка и регистрация/авторизация пользователя
     * @param user пользователь
     * @return результат выполнения
     */
    public ExecutionResponse checkUser(User user) {
        lock.lock();
        try {
            int id = dbmanager.checkUser(user);
            if (id > 0) {
                users.put(user.getName().toLowerCase(), id);
                return new ExecutionResponse(true, "Авторизация прошла успешно");
            } else if (id == -1) {
                return new ExecutionResponse(false, "Неверный пароль");
            } else if (id == -2) {
                ExecutionResponse response = dbmanager.registerUser(user);
                if (response.getExitCode()) {
                    users.put(user.getName().toLowerCase(), Integer.parseInt(response.getMassage()));
                    return new ExecutionResponse(true, "Пользователь " + user.getName() + " успешно зарегистрирован");
                }
                return response;
            }
            return new ExecutionResponse(false, "Ошибка выполнения команды");
        } finally {
            lock.unlock();
        }
    }
    public ArrayList<City> showCollection(){
        lock.lock();
        try{return collection;}
        finally {
            lock.unlock();
        }
    }

    /**
     * Закрытие соединения пользователя
     * @param user пользователь
     * @return результат выполнения
     */
    public ExecutionResponse closeConnection(User user) {
        lock.lock();
        try {
            users.remove(user.getName());
            return new ExecutionResponse(true, "Соединение разорвано");
        } catch (Exception e) {
            return new ExecutionResponse(false, "Не удалось отключить");
        } finally {
            lock.unlock();
        }
    }
}