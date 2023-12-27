package algorithm.model.result;

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
     * Длительность в секундах
     */
    private int duration;

    /**
     * ID необходимой группы оборудования
     */
    private long equipmentGroupId;

    /**
     * ID выбранного для операции конкретного оборудования
     */
    private long equipmentId;

    /**
     * Время раннего начала выполнения заказа
     */
    private LocalDateTime earlyStartTime;

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

    private boolean done;

    private OperationResult prevOperation;

    private OperationResult nextOperation;

    private OperationPriorities operationPriorities;

    public OperationResult() {

    }

    public OperationResult(long operationId, long prevOperationId, long nextOperationId, int duration,
                           long equipmentGroupId, long equipmentId, LocalDateTime startTime, LocalDateTime endTime,
                           ProductResult productResult) {
        this.operationId = operationId;
        this.prevOperationId = prevOperationId;
        this.nextOperationId = nextOperationId;
        this.duration = duration;
        this.equipmentGroupId = equipmentGroupId;
        this.equipmentId = equipmentId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.productResult = productResult;
        this.done = false;
    }

    public OperationResult(long operationId, long prevOperationId, long nextOperationId, long equipmentGroupId,
                           long equipmentId, LocalDateTime earlyStartTime, LocalDateTime startTime, LocalDateTime endTime,
                           ProductResult productResult) {
        this.operationId = operationId;
        this.prevOperationId = prevOperationId;
        this.nextOperationId = nextOperationId;
        this.equipmentGroupId = equipmentGroupId;
        this.equipmentId = equipmentId;
        this.earlyStartTime = earlyStartTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.productResult = productResult;
        this.done = false;
    }

    public OperationResult(long operationId, long prevOperationId, long nextOperationId, long equipmentGroupId,
                           long equipmentId, LocalDateTime earlyStartTime, LocalDateTime startTime, LocalDateTime endTime,
                           ProductResult productResult, OperationPriorities priorities) {
        this.operationId = operationId;
        this.prevOperationId = prevOperationId;
        this.nextOperationId = nextOperationId;
        this.equipmentGroupId = equipmentGroupId;
        this.equipmentId = equipmentId;
        this.earlyStartTime = earlyStartTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.productResult = productResult;
        this.operationPriorities = priorities;
        this.done = false;
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

    public int getDuration() {
        return duration;
    }

    public long getEquipmentGroupId() {
        return equipmentGroupId;
    }

    public long getEquipmentId() {
        return equipmentId;
    }

    public LocalDateTime getEarlyStartTime() {
        return earlyStartTime;
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

    public OperationPriorities getOperationPriorities() {
        return operationPriorities;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public OperationResult getPrevOperation() {
        return prevOperation;
    }

    public void setPrevOperation(OperationResult prevOperation) {
        this.prevOperation = prevOperation;
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
        if (Objects.isNull(o)) {
            return -1;
        }
        if (this.operationPriorities.getOrderInTechProcess() < o.getOperationPriorities().getOrderInTechProcess()) {
            return -1;
        } else if (this.operationPriorities.getOrderInTechProcess() > o.getOperationPriorities().getOrderInTechProcess()) {
            return 1;
        } else if (this.operationPriorities.getOrderInTechProcess() == o.getOperationPriorities().getOrderInTechProcess()) {
            if (this.operationPriorities.getDuration() < o.getOperationPriorities().getDuration()) {
                return -1;
            } else if (this.operationPriorities.getDuration() > o.getOperationPriorities().getDuration()) {
                return 1;
            } else if (this.operationPriorities.getDuration() == o.getOperationPriorities().getDuration()) {
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
