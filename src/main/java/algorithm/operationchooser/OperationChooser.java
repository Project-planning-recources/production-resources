package algorithm.operationchooser;

import algorithm.model.result.OperationResult;

import java.util.List;

/**
 * Интерфейс для выборщика
 */

public interface OperationChooser {

    /**
     * Из списка операций выбирает, какую операцию начнём на данный момент
     * @param operations - список операций
     * @return - выбранная операция
     */
    OperationResult choose(List<OperationResult> operations);
}
