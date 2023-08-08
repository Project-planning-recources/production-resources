package parse.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

/**
 * Класс, помогающий адаптировать даты из строки xml файла и наоборот
 */
public class DateAdapter extends XmlAdapter<String, LocalDateTime> {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    //private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm::ss");

    /**
     *
     * @param date Дата в виде строки
     * @return LocalDateTime
     */
    @Override
    public LocalDateTime unmarshal(String date) {
        String[] dateAndTime = date.split(" ");
        LocalDate localDate = LocalDate.parse(dateAndTime[0], dateFormatter);
        LocalTime localTime = LocalTime.parse(dateAndTime[1], ISO_LOCAL_TIME);
        return LocalDateTime.of(localDate, localTime);
    }

    /**
     *
     * @param date Дата
     * @return Дата в формате строки
     */
    @Override
    public String marshal(LocalDateTime date) {
        return dateFormatter.format(date.toLocalDate()) + " " + ISO_LOCAL_TIME.format(date.toLocalTime());
    }
}
