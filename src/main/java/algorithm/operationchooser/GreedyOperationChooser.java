package algorithm.operationchooser;

import algorithm.model.order.Operation;
import algorithm.model.result.OperationResult;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Выборщик, берущий операцию с наименьшим временем выполнения
 */
public class GreedyOperationChooser implements OperationChooser {

    private final HashMap<Long, Operation> allOperations;

    public GreedyOperationChooser(HashMap<Long, Operation> allOperations) {
        this.allOperations = allOperations;
    }

    @Override
    public OperationResult choose(List<OperationResult> operations) {
        return operations.stream()
                .min(Comparator.comparing(operation -> allOperations.get(operation.getOperationId()).getDuration()))
                .orElseThrow(() -> new RuntimeException("Операция не найдена"));
    }
}
