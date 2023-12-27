package parse.adapter;

import parse.input.production.InputWorkingDay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

/**
 * Класс, помогающий адаптировать данные о рабочих днях из входного xml-файла
 */
public class WorkingDayAdapter extends XmlAdapter<WorkingDayAdapter.AdaptedWorkingDay, InputWorkingDay> {

    private static final String DELIMITER = "-";


    /**
     *
     * @param adaptedWorkingDay Экземпляр промежуточного класса с данными о рабочем дне
     * @return WorkingDay
     */
    @Override
    public InputWorkingDay unmarshal(AdaptedWorkingDay adaptedWorkingDay) {
        if(adaptedWorkingDay == null) {
            return null;
        }

        InputWorkingDay workingDay = new InputWorkingDay();

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
    public AdaptedWorkingDay marshal(InputWorkingDay workingDay) {
        return new AdaptedWorkingDay(String.valueOf(workingDay.getDayNumber()),
                ISO_LOCAL_TIME.format(workingDay.getStartWorkingTime()) + DELIMITER +
                        ISO_LOCAL_TIME.format(workingDay.getEndWorkingTime()));
    }

    /**
     * Промежуточный класс для парсинга данных из xml-файла в java-класс
     */
    @XmlType(name = "Day")
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

        public AdaptedWorkingDay() {
        }

        public AdaptedWorkingDay(String dayNumber, String timePeriod) {
            this.dayNumber = dayNumber;
            this.timePeriod = timePeriod;
        }
    }
}
