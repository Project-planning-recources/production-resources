package algorithm.model.result;

import java.time.LocalDateTime;

public class OperationPriorities {

    /**
     * ID операции (для сопоставления)
     */
    private final long operationId;

    /**
     * Порядок в тех процессе (второй приоритет)
     */
    private final long orderInTechProcess;

    /**
     * Длительность в секундах
     */
    private final int duration;

    /**
     * Дедлайн для заказа (директивный срок) (третий приоритет)
     */
    private final LocalDateTime deadline;

    /**
     * Порядок создания (четвёртый приоритет для случаев, если все предыдущие пункты совпадают)
     * ? возможно, заменить на количество совпадающих операций ?
     */
    private final long addingOrder;

    public OperationPriorities(long operationId, int duration, long orderInTechProcess,
                               LocalDateTime deadline, long addingOrder) {
        this.operationId = operationId;
        this.duration = duration;
        this.orderInTechProcess = orderInTechProcess;
        this.deadline = deadline;
        this.addingOrder = addingOrder;
    }

    public long getOperationId() {
        return operationId;
    }

    public int getDuration() {
        return duration;
    }

    public long getOrderInTechProcess() {
        return orderInTechProcess;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public long getAddingOrder() {
        return addingOrder;
    }
}
