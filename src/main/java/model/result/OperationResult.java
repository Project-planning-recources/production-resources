package model.result;

import parse.adapter.DateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.Objects;


/**
 * <b>Результат работы для операции конкретной детали</b>
 */
@XmlType(name = "Operation")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationResult {

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

    /**
     * Деталь, для которой выполнялась операция
     */
    @XmlTransient
    private ProductResult productResult;

    @XmlTransient
    private OperationResult nextOperation;

    public OperationResult() {

    }

    public OperationResult(long operationId, long prevOperationId, long nextOperationId, long equipmentId,
                           LocalDateTime startTime, LocalDateTime endTime, ProductResult productResult) {
        this.operationId = operationId;
        this.prevOperationId = prevOperationId;
        this.nextOperationId = nextOperationId;
        this.equipmentId = equipmentId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.productResult = productResult;
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

    public ProductResult getProductResult() {
        return productResult;
    }

    public void setNextOperation(OperationResult nextOperation) {
        this.nextOperation = nextOperation;
    }

    public OperationResult getNextOperation() {
        return nextOperation;
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
}
