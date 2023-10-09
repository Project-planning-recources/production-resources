package generator;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Date;

public class GeneratorParameters {

    /**
     * Количество дней для расписания
     */
    public int daysForSchedule;

    /**
     * Время начала рабочего дня
     */
    public int startWorkingTime;

    /**
     * Время конца рабочего дня
     */
    public int endWorkingTime;

    /**
     * Количество заказов, которые надо сгенерировать
     */
    public int ordersCount;

    /**
     * Минимальное количество групп оборудования
     */
    public int minEquipmentGroupCount;

    /**
     * Максимальное количество групп оборудования
     */
    public int maxEquipmentGroupCount;

    /**
     * Минимальное количество оборудования в группе
     */
    public int minEquipmentCount;

    /**
     * Максимальное количество оборудования в группе
     */
    public int maxEquipmentCount;

    /**
     * Минимальное время начала заказа
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    public Date minOrderStartTime;

    /**
     * Максимальное количество для время начала заказа от минимальной даты
     */
    public int maxDurationStartTime;

    /**
     * Минимальная длительность заказа в днях
     */
    public int minDurationTimeInDays;

    /**
     * Максимальная длительность заказа в днях
     */
    public int maxDurationTimeInDays;

    /**
     * Минимальное количество типов деталей в заказе
     */
    public int minDetailsTypeCount;

    /**
     * Максимальное количество типов деталей в заказе
     */
    public int maxDetailsTypeCount;

    /**
     * Минимальное количество деталей в заказе
     */
    public int minDetailsCount;

    /**
     * Максимальное количество деталей в заказе
     */
    public int maxDetailsCount;

    /**
     * Минимальное количество техпроцессов для каждой детали
     */
    public int minTechProcessCount;

    /**
     * Максимальное количество техпроцессов для каждой детали
     * Хотя бы для одной детали из файла количество техпроцессов должно обязательно
     * равняться этому значению, чтобы не было случая, когда для каждой детали один
     * техпроцесс
     */
    public int maxTechProcessCount;

    /**
     * Минимальное количество операций в техпроцессе
     */
    public int minOperationsCount;

    /**
     * Максимальное количество операций в техпроцессе
     */
    public int maxOperationsCount;

    /**
     * Минимальная длительность операции в секундах
     */
    public int minOperationDuration;

    /**
     * Максимальная длительность операции в секундах
     */
    public int maxOperationDuration;

    @Override
    public String toString() {
        return "GeneratorParameters{" +
                "daysForSchedule=" + daysForSchedule +
                ", startWorkingTime=" + startWorkingTime +
                ", endWorkingTime=" + endWorkingTime +
                ", ordersCount=" + ordersCount +
                ", minEquipmentGroupCount=" + minEquipmentGroupCount +
                ", maxEquipmentGroupCount=" + maxEquipmentGroupCount +
                ", minEquipmentCount=" + minEquipmentCount +
                ", maxEquipmentCount=" + maxEquipmentCount +
                ", minOrderStartTime=" + minOrderStartTime +
                ", maxDurationStartTime=" + maxDurationStartTime +
                ", minDurationTimeInDays=" + minDurationTimeInDays +
                ", maxDurationTimeInDays=" + maxDurationTimeInDays +
                ", minDetailsTypeCount=" + minDetailsTypeCount +
                ", maxDetailsTypeCount=" + maxDetailsTypeCount +
                ", minDetailsCount=" + minDetailsCount +
                ", maxDetailsCount=" + maxDetailsCount +
                ", minTechProcessCount=" + minTechProcessCount +
                ", maxTechProcessCount=" + maxTechProcessCount +
                ", minOperationsCount=" + minOperationsCount +
                ", maxOperationsCount=" + maxOperationsCount +
                ", minOperationDuration=" + minOperationDuration +
                ", maxOperationDuration=" + maxOperationDuration +
                '}';
    }
}



