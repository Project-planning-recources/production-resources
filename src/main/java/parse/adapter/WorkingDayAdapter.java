package parse.adapter;

import model.production.WorkingDay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalTime;

/**
 * Класс, помогающий адаптировать данные о рабочих днях из входного xml-файла
 */
public class WorkingDayAdapter extends XmlAdapter<WorkingDayAdapter.AdaptedWorkingDay, WorkingDay> {

    private static final String DELIMITER = "-";

    /**
     *
     * @param adaptedWorkingDay Экземпляр промежуточного класса с данными о рабочем дне
     * @return WorkingDay
     */
    @Override
    public WorkingDay unmarshal(AdaptedWorkingDay adaptedWorkingDay) {
        if(adaptedWorkingDay == null) {
            return null;
        }

        WorkingDay workingDay = new WorkingDay();

        if(adaptedWorkingDay.dayNumber != null) {
            workingDay.setDayNumber(Short.valueOf(adaptedWorkingDay.dayNumber));
        }

        if(adaptedWorkingDay.timePeriod != null) {
            String[] times = adaptedWorkingDay.timePeriod.split(DELIMITER);
            workingDay.setStartWorkingTime(LocalTime.parse(times[0]));
            workingDay.setEndWorkingTime(LocalTime.parse(times[1]));
            workingDay.setWeekday(true);
        }

        return workingDay;
    }

    @Override
    public AdaptedWorkingDay marshal(WorkingDay workingDay) {
        return null;
    }

    /**
     * Промежуточный класс для парсинга данных из xml-файла в java-класс
     */
    @XmlType(name = "Include")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AdaptedWorkingDay {

        /**
         * Номер дня
         */
        @XmlAttribute(name = "day_of_week")
        private String dayNumber;

        /**
         * Временной период в виде строки
         */
        @XmlAttribute(name = "time_period")
        private String timePeriod;
    }
}
