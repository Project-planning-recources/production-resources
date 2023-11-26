package algorithm.model.result;

import parse.adapter.DateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.Objects;


/**
 * <b>Класс для Алгоритма</b>
 * <b>Результат работы для операции конкретной детали</b>
 */
public class OperationResult implements Comparable<OperationResult>{

    /**
     * ID операции
     */
    private long operationId;

    /**
     * ID предыдущей операции
     * 0, если предыдущей нет
     */
    private long prevOperationId;

    /**
     * ID следующей операции
     * 0, если следующей нет
     */
    private long nextOperationId;

    /**
     * ID необходимой группы оборудования
     */
    private long equipmentGroupId;

    /**
     * ID выбранного для операции конкретного оборудования
     */
    private long equipmentId;

    /**
     * Длительность в секундах
     */
    private int duration;

    /**
     * Время и дата начала выполнения операции
     */
    private LocalDateTime startTime;

    /**
     * Время и дата окончания выполнения операции
     */
    private LocalDateTime endTime;

    /**
     * Деталь, для которой выполнялась операция
     */
    private ProductResult productResult;

    private OperationResult nextOperation;

    private OperationPriorities operationPriorities;

    public OperationResult() {

    }

    public OperationResult(long operationId, long prevOperationId, long nextOperationId, long equipmentGroupId,
                           long equipmentId, int duration, LocalDateTime startTime, LocalDateTime endTime,
                           ProductResult productResult) {
        this.operationId = operationId;
        this.prevOperationId = prevOperationId;
        this.nextOperationId = nextOperationId;
        this.equipmentGroupId = equipmentGroupId;
        this.equipmentId = equipmentId;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.productResult = productResult;
    }

    public OperationResult(long operationId, long prevOperationId, long nextOperationId, long equipmentGroupId,
                           long equipmentId, int duration, LocalDateTime startTime, LocalDateTime endTime,
                           ProductResult productResult, OperationPriorities priorities) {
        this.operationId = operationId;
        this.prevOperationId = prevOperationId;
        this.nextOperationId = nextOperationId;
        this.equipmentGroupId = equipmentGroupId;
        this.equipmentId = equipmentId;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.productResult = productResult;
        this.operationPriorities = priorities;
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

    public long getEquipmentGroupId() {
        return equipmentGroupId;
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

    public int getDuration() {
        return duration;
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

    public ProductResult getProductResult() {
        return productResult;
    }

    public void setNextOperation(OperationResult nextOperation) {
        this.nextOperation = nextOperation;
    }

    public OperationResult getNextOperation() {
        return nextOperation;
    }

    public OperationPriorities getOperationPriorities() {
        return operationPriorities;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationResult that = (OperationResult) o;
        return operationId == that.operationId && Objects.equals(productResult, that.productResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId, productResult);
    }

    @Override
    public int compareTo(OperationResult o) {
        if (this.operationPriorities.getStartTime().isBefore(o.getOperationPriorities().getStartTime())) {
            return -1;
        } else if (this.operationPriorities.getStartTime().isAfter(o.getOperationPriorities().getStartTime())) {
            return 1;
        } else if (this.operationPriorities.getStartTime().equals(o.getOperationPriorities().getStartTime())) {
            if (this.operationPriorities.getOrderInTechProcess() < o.getOperationPriorities().getOrderInTechProcess()) {
                return -1;
            } else if (this.operationPriorities.getOrderInTechProcess() > o.getOperationPriorities().getOrderInTechProcess()) {
                return 1;
            } else if (this.operationPriorities.getOrderInTechProcess() == o.getOperationPriorities().getOrderInTechProcess()) {
                if (this.operationPriorities.getDeadline().isBefore(o.getOperationPriorities().getDeadline())) {
                    return -1;
                } else if (this.operationPriorities.getDeadline().isAfter(o.getOperationPriorities().getDeadline())) {
                    return 1;
                }
                else if (this.operationPriorities.getDeadline().equals(o.getOperationPriorities().getDeadline())) {
                    if (this.operationPriorities.getAddingOrder() < o.getOperationPriorities().getAddingOrder()) {
                        return -1;
                    } else if (this.operationPriorities.getAddingOrder() > o.getOperationPriorities().getAddingOrder()) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }
}
