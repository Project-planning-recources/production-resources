package algorithm.model.result;

import java.time.LocalDateTime;

public class OperationPriorities {

    /**
     * ID операции (для сопоставления)
     */
    private final long operationId;

    /**
     * Время раннего начала выполнения заказа (первый приоритет)
     */
    private final LocalDateTime startTime;

    /**
     * Порядок в тех процессе (второй приоритет)
     */
    private final long orderInTechProcess;

    /**
     * Дедлайн для заказа (директивный срок) (третий приоритет)
     */
    private final LocalDateTime deadline;

    /**
     * Порядок создания (четвёртый приоритет для случаев, если все предыдущие пункты совпадают)
     * ? возможно, заменить на количество совпадающих операций ?
     */
    private final long addingOrder;

    public OperationPriorities(long operationId, LocalDateTime startTime, long orderInTechProcess, LocalDateTime deadline,
                               long addingOrder) {
        this.operationId = operationId;
        this.startTime = startTime;
        this.orderInTechProcess = orderInTechProcess;
        this.deadline = deadline;
        this.addingOrder = addingOrder;
    }

    public long getOperationId() {
        return operationId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
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
