package data;
import utility.Validate;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Класс человека
 */
public class Human implements Validate {
    private long age; //Значение поля должно быть больше 0
    private long height; //Значение поля должно быть больше 0
    private LocalDate birthday;
    /**
     * Констркутор
     *
     * @param age         возраст
     * @param height      рост
     * @param birthday    день рождения
     */
    public Human(long age, long height, LocalDate birthday) {
        this.age = age;
        this.height = height;
        this.birthday = birthday;
    }

    public Human(String arg) {
        try{
            age = Integer.parseInt(arg.split(";")[0]);
            try{
                height = Integer.parseInt(arg.split(";")[1]);
                try {
                    birthday = LocalDate.parse(arg.split(";")[2]);
                } catch (DateTimeParseException e) {}
            }
            catch(NumberFormatException e){
                height = 0;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) { }
    }

    /**
     * Функция проверки на валидность полей объекта
     *
     * @return true, если поля валидны
     */
    @Override
    public boolean validate() {
        if (age <= 0) return false;
        return height > 0;
    }
    /**
     * @return возвращает объект, переведенный в строковое представление
     */
    @Override
    public String toString() {
        return age + ";" + height + ";" + birthday;
    }
    /**
     * @return возвращает возраст
     */
    public long getAge() {
        return age;
    }
    /**
     * @return возвращает рост
     */
    public long getHeight() {
        return height;
    }

    public LocalDate getBirthday() {return birthday;}

    /**
     * Переопределение метода эквивалентности объект
     *
     * @param obj сравниваемый объект
     * @return true, если объекты эквивалентны
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Human person = (Human) obj;
            return age==person.age && height == person.height && birthday.equals(person.birthday);
    }

    /**
     * @return возвращает Хэш-код объекта
     */
    @Override
    public int hashCode() {
        return Objects.hash(age, height, birthday);
    }
}
