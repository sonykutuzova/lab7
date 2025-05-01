package data;

import utility.Element;
import utility.Validate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * класс города
 */
public class City extends Element implements Validate {
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Float area; //Значение поля должно быть больше 0, Поле не может быть null
    private Integer population; //Значение пол  я должно быть больше 0, Поле не может быть null
    private float metersAboveSeaLevel;
    private LocalDate establishmentDate;
    private Government government; //Поле не может быть null
    private StandardOfLiving standardOfLiving; //Поле не может быть null
    private Human governor; //Поле может быть null

    /**
     * констркутор
     *
     * @param id              уникальный номер объекта
     * @param name            название
     * @param coordinates     координаты
     * @param creationDate    дата создания
     * @param area            площадь
     * @param population      количество людей
     * @param metersAboveSeaLevel    высота над уровнем моря
     * @param establishmentDate      дата основания города
     * @param government      правительство
     * @param standardOfLiving       губернатор
     * @param governor        тип жизни
     */
    public City(long id, String name, Coordinates coordinates, LocalDate creationDate, Float area, Integer population, float metersAboveSeaLevel, LocalDate establishmentDate, Government government, StandardOfLiving standardOfLiving, Human governor){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.area = area;
        this.population = population;
        this.metersAboveSeaLevel = metersAboveSeaLevel;
        this.establishmentDate = establishmentDate;
        this.government = government;
        this.standardOfLiving = standardOfLiving;
        this.governor = governor;
    }
    public City(long id, String name, Coordinates coordinates, Float area, Integer population, float metersAboveSeaLevel, LocalDate establishmentDate, Government government, StandardOfLiving standardOfLiving, Human governor){
        this(id, name, coordinates, LocalDate.now(), area, population, metersAboveSeaLevel, establishmentDate, government, standardOfLiving, governor);
    }
    /**
     * Валидирует правильность полей.
     * @return true, если все верно, иначе false
     */
    @Override
    public boolean validate() {
        if (id <= 0) return false;
        if (area <= 0 || area == null) return false;
        if (population <= 0 || population == null) return false;
        if (government == null) return false;
        if (standardOfLiving == null) return false;
        if (name == null || name.isEmpty()) return false;
        if (creationDate == null) return false;
        if (coordinates == null || !coordinates.validate()) return false;
        return true;
    }
    /**
     * Конструктор
     *
     * @param args массив строк, из которого будет создан объект
     * @return возвращает лабораторную работу
     */


    public static City fromArray(String[] args) {
        Long id;
        String name;
        Coordinates coordinates;
        LocalDate creationDate;
        Float area;
        Integer population;
        Float metersAboveSeaLevel;
        LocalDate establishmentDate;
        Government government;
        StandardOfLiving standardOfLiving;
        Human governor;

        try {
            try {
                id = Long.parseLong(args[0]);
            } catch (NumberFormatException e) {
                id = null;
            }

            name = args[1];
            coordinates = new Coordinates(args[2]);

            creationDate = LocalDate.now(); // Автоматическая генерация даты

            try {
                area = Float.parseFloat(args[3]);
            } catch (NumberFormatException e) {
                area = null;
            }

            try {
                population = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                population = null;
            }

            try {
                metersAboveSeaLevel = Float.parseFloat(args[5]);
            } catch (NumberFormatException e) {
                metersAboveSeaLevel = null;
            }

            try {
                establishmentDate = LocalDate.parse(args[6]);
            } catch (DateTimeParseException e) {
                establishmentDate = null;
            }

            try {
                government = Government.valueOf(args[7]);
            } catch (IllegalArgumentException e) {
                government = null;
            }

            try {
                standardOfLiving = StandardOfLiving.valueOf(args[8]);
            } catch (IllegalArgumentException e) {
                standardOfLiving = null;
            }

            governor = new Human(args[9]);

            return new City(id, name, coordinates, creationDate, area, population, metersAboveSeaLevel, establishmentDate, government, standardOfLiving, governor);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public void setId(Long id){
        this.id = id;
    }




    /**
     * @return возвращает объект, переведенный в строковое представление
     */
    @Override
    public String toString() {
        return "city\n" +
                "  id: " + id + "\n" +
                "  name: " + name + "\n" +
                "  coordinates: " + coordinates + "\n" +
                "  creationDate: " + creationDate.format(DateTimeFormatter.ISO_DATE) + "\n" +
                "  area: " + area + "\n" +
                "  population: " + population + "\n" +
                "  metersAboveSeaLevel: " + metersAboveSeaLevel + "\n" +
                "  establishmentDate: " + establishmentDate.format(DateTimeFormatter.ISO_DATE) + "\n" +
                "  government: " + government + "\n" +
                "  standardOfLiving: " + standardOfLiving + "\n" +
                "  governor: " + (governor == null ? "null" : governor);
    }
    public String toStr() {
        var str = "";
        str += Long.toString(this.id);
        str += "/";
        str += this.name;
        str += "/";
        str += this.coordinates.toString();
        //str += '/';
        //str += this.creationDate.toString();
        str += "/";
        str += this.area.toString();
        str += "/";
        str += this.population.toString();
        str += "/";
        str += this.metersAboveSeaLevel;
        str += "/";
        str += this.establishmentDate.toString();
        str += "/";
        str += this.government.toString();
        str += "/";
        str += this.standardOfLiving.toString();
        str += "/";
        str += this.governor.toString();
        return str;
    }
    /**
     * @return возвращает id
     */

    public long getId() {
        return id;
    }
    /**
     * @return возвращает название
     */
    public String getName() {
        return name;
    }
    /**
     * @return возвращает координаты
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }
    /**
     * @return возвращает дату создания элемента
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }
    /**
     * @return возвращает площадь
     */
    public Float getArea() {
        return area;
    }
    /**
     * @return возвращает население
     */
    public Integer getPopulation() {
        return population;
    }
    /**
     * @return возвращает высоту над уровнем моря
     */
    public float getMetersAboveSeaLevel() {
        return metersAboveSeaLevel;
    }
    /**
     * @return возвращает дату основания города
     */
    public LocalDate getEstablishmentDate() {
        return establishmentDate;
    }
    /**
     * @return возвращает тип правительства
     */
    public Government getGovernment() {
        return government;
    }
    /**
     * @return возвращает тип жизни
     */
    public StandardOfLiving getStandardOfLiving() {
        return standardOfLiving;
    }
    /**
     * @return возвращает губернатора
     */
    public Human getGovernor() {
        return governor;
    }
    /**
     * Переопределение метода эквивалентности объект
     *
     * @param o сравниваемый объект
     * @return true, если объекты эквивалентны
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City that = (City) o;
        return Objects.equals(id, that.id);
    }
    /**
     * @return возвращает Хэш-код объекта
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, area, population, metersAboveSeaLevel, establishmentDate, government, standardOfLiving, governor);
    }

    /**
     * @param element the object to be compared.
     * @return возвращает на сколько один число студентов в одной группе больше, чем в другой
     */
    @Override
    public int compareTo(Element element) {
        return (int)(this.id - element.getId());
    }
}

