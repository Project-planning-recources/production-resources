package algorithm.records.record;

import algorithm.model.result.OperationResult;

import java.time.LocalDateTime;

public interface Record {

    /**
     * Возвращает рекордное значение среди списка доступных операций. Если такая операция не найдена, возвращается null
     */
    OperationResult getRecord(LocalDateTime timeTick);
}
