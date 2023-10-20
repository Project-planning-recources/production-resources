package parse.input.production;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>Класс для IO</b>
 * <b>Расписание производства</b>
 * <p>В будущем можно будет добавить выходные, время отдыха и т.д.</p>
 *
 */
@XmlType(name = "CalendarInformation", propOrder = {"week"})
@XmlAccessorType(XmlAccessType.FIELD)
public class InputSchedule implements Serializable {

    /**
     * Список дней недели
     */
    @XmlElementWrapper(name="Timetable")
    @XmlElement(name = "Day")
    private final List<InputWorkingDay> week;

    public InputSchedule() {
        week = new ArrayList<>();
    }

    public InputSchedule(List<InputWorkingDay> week) {
        this.week = week;
    }

    public List<InputWorkingDay> getWeek() {
        return week;
    }

    public InputWorkingDay getWorkDayByDayNumber(Short dayNumber) {
        for(InputWorkingDay day : week) {
           if(day.getDayNumber().equals(dayNumber)) {
               return day;
           }
        }
        return null;
    }

    @Override
    public String toString() {
        return "InputSchedule{" +
                "week=" + week +
                '}';
    }
}
