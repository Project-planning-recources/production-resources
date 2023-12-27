package parse.output.result;

import algorithm.model.result.OperationResult;
import parse.adapter.DateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * <b>Класс для IO</b>
 * <b>Результат работы для операции конкретной детали</b>
 */
@XmlType(name = "Operation")
@XmlAccessorType(XmlAccessType.FIELD)
public class OutputOperationResult {

    /**
     * ID операции
     */
    @XmlAttribute(name = "operation_id")
    private long operationId;

    /**
     * ID предыдущей операции
     * 0, если предыдущей нет
     */
    @XmlAttribute(name = "prev_operation_id")
    private long prevOperationId;

    /**
     * ID следующей операции
     * 0, если следующей нет
     */
    @XmlAttribute(name = "next_operation_id")
    private long nextOperationId;

    /**
     * ID выбранного для операции конкретного оборудования
     */
    @XmlAttribute(name = "equipment_id")
    private long equipmentId;

    /**
     * Время и дата начала выполнения операции
     */
    @XmlAttribute(name = "operation_start_date_time")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private LocalDateTime startTime;

    /**
     * Время и дата окончания выполнения операции
     */
    @XmlAttribute(name = "operation_end_date_time")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private LocalDateTime endTime;

    public OutputOperationResult() {

    }

    public OutputOperationResult(OperationResult operationResult, HashMap<Long, ArrayList<OutputOperationResult>> performedOperationsOnEquipments) {
        this.operationId = operationResult.getOperationId();
        this.prevOperationId = operationResult.getPrevOperationId();
        this.nextOperationId = operationResult.getNextOperationId();
        this.equipmentId = operationResult.getEquipmentId();
        this.startTime = operationResult.getStartTime();
        this.endTime = operationResult.getEndTime();
        if (!performedOperationsOnEquipments.containsKey(equipmentId)) {
            performedOperationsOnEquipments.put(equipmentId, new ArrayList<>());
        }
        performedOperationsOnEquipments.get(equipmentId).add(this);
    }

    public OutputOperationResult(long operationId, long prevOperationId, long nextOperationId, long equipmentId,
                                 LocalDateTime startTime, LocalDateTime endTime) {
        this.operationId = operationId;
        this.prevOperationId = prevOperationId;
        this.nextOperationId = nextOperationId;
        this.equipmentId = equipmentId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getOperationId() {
        return operationId;
    }

    public long getPrevOperationId() {
        return prevOperationId;
    }

    public long getNextOperationId() {
        return nextOperationId;
    }

    public long getEquipmentId() {
        return equipmentId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEquipmentId(long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "OperationResult{" +
                "operationId=" + operationId +
                ", prevOperationId=" + prevOperationId +
                ", nextOperationId=" + nextOperationId +
                ", equipmentId=" + equipmentId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

}
