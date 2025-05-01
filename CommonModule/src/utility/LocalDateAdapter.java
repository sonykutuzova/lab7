package utility;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Класс для приведения даты к нужному формату
 */
public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    @Override
    /**
     * Сериализация даты
     */
    public JsonElement serialize(LocalDate date, Type typeOfSrc,
                                 JsonSerializationContext context) {
        return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
    }
    /**
     * Десериализация даты
     */
    @Override
    public LocalDate deserialize(JsonElement json, Type type,
                                 JsonDeserializationContext context) throws JsonParseException {
        return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
    }
}
