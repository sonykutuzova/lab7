package data;
import utility.Validate;

import java.util.Objects;

/**
 * класс координат
 */
public class Coordinates implements Validate {
    private double x; //Максимальное значение поля: 613
    private Double y; //Поле не может быть null
    /**
     * Конструктор
     *
     * @param x координата х
     * @param y координата у
     */
    public Coordinates(double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates(String arg) {
        try{
            try{
                x = Double.parseDouble(arg.split(";")[0]);
            }
            catch (NumberFormatException e){ }
            try{
                y = Double.parseDouble(arg.split(";")[1]);
            }
            catch (NumberFormatException e) { }
        }
        catch (ArrayIndexOutOfBoundsException e){ }
    }

    /**
     * Функция проверки валидности полей объекта
     *
     * @return true, если поля объекта валидны
     */
    @Override
    public boolean validate() {
        if (x > 613) return false;
        return y != null;
    }

    /**
     * @return возвращает объект, переведенный в строковое представление
     */
    @Override
    public String toString() {
        return x + ";" + y;
    }
    /**
     * @return возвращает координату х
     */
    public double getX() {
        return x;
    }
    /**
     * @return возвращает координату y
     */
    public Double getY() {
        return y;
    }
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
        Coordinates coordinates = (Coordinates) obj;
        return x==coordinates.x && y.equals(coordinates.y);
    }

    /**
     * @return возвращает Хэш-код объекта
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
