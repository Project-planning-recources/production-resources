package algorithm.parallel;

import algorithm.model.result.OperationResult;

import java.time.LocalDateTime;
import java.util.List;

public interface Record {

    //todo: проработать вариант для возвращения не null
    /**
     * Возвращает рекордное значение среди списка доступных операций. Если такая операция не найдена, возвращается null
     */
    OperationResult getRecord(List<OperationResult> operations, LocalDateTime timeTick);
}
