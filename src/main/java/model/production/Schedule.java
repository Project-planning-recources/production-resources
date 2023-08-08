package model.production;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * <b>Расписание производства</b>
 * <p>В будущем можно будет добавить выходные, время отдыха и т.д.</p>
 *
 */
@XmlType(name = "CalendarInformation", propOrder = {"week"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Schedule implements Serializable {

    /**
     * Список дней недели
     */
    @XmlElementWrapper(name="Timetable")
    @XmlElement(name = "Day")
    private List<WorkingDay> week;

    public Schedule() {

    }

    public Schedule(List<WorkingDay> week) {
        this.week = week;
    }

    public List<WorkingDay> getWeek() {
        return week;
    }

    public boolean checkWorkDayInScheduleByDayNumber(Short dayNumber) {
        for(WorkingDay day : week) {
            if(day.getDayNumber().equals(dayNumber)) {
                return true;
            }
        }
        return false;
    }

    public WorkingDay getWorkDayByDayNumber(Short dayNumber) {
        for(WorkingDay day : week) {
           if(day.getDayNumber().equals(dayNumber)) {
               return day;
           }
        }
        return null;
    }
}
