package algorithm.model.production;

import parse.adapter.WorkingDayAdapter;
import parse.input.production.InputWorkingDay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalTime;

/**
 * <b>Класс для Алгоритма</b>
 * <b>День недели</b>
 */
public class WorkingDay implements Serializable {

    /**
     * Номер дня
     * 1 - понедельник
     * 7 - воскресение
     */
    private Short dayNumber;

    /**
     * Время начала рабочего дня
     */
    private LocalTime startWorkingTime;

    /**
     * Время окончания рабочего дня
     */
    private LocalTime endWorkingTime;

    /**
     * Параметр, показывающий, является ли день рабочим
     */
    private Boolean isWeekday;

    public WorkingDay() {

    }

    public WorkingDay(InputWorkingDay inputWorkingDay) {
        this.dayNumber = inputWorkingDay.getDayNumber();
        this.startWorkingTime = inputWorkingDay.getStartWorkingTime();
        this.endWorkingTime = inputWorkingDay.getEndWorkingTime();
        this.isWeekday = inputWorkingDay.getWeekday();
    }

    public WorkingDay(Short dayNumber, LocalTime startWorkingTime, LocalTime endWorkingTime, Boolean isWeekday) {
        this.dayNumber = dayNumber;
        this.startWorkingTime = startWorkingTime;
        this.endWorkingTime = endWorkingTime;
        this.isWeekday = isWeekday;
    }

    public Short getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Short dayNumber) {
        this.dayNumber = dayNumber;
    }

    public LocalTime getStartWorkingTime() {
        return startWorkingTime;
    }

    public void setStartWorkingTime(LocalTime startWorkingTime) {
        this.startWorkingTime = startWorkingTime;
    }

    public LocalTime getEndWorkingTime() {
        return endWorkingTime;
    }

    public void setEndWorkingTime(LocalTime endWorkingTime) {
        this.endWorkingTime = endWorkingTime;
    }

    public Boolean getWeekday() {
        return isWeekday;
    }

    public void setWeekday(Boolean weekday) {
        isWeekday = weekday;
    }

    @Override
    public String toString() {
        return "WorkingDay{" +
                "dayNumber=" + dayNumber +
                ", startWorkingTime=" + startWorkingTime +
                ", endWorkingTime=" + endWorkingTime +
                ", isWeekday=" + isWeekday +
                '}';
    }
}
