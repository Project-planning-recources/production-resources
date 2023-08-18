package algorithm.operationchooser;

import algorithm.model.result.OperationResult;

import java.util.Comparator;
import java.util.List;

/**
 * Выборщик, берущий операцию с наименьшей дате самого раннего заказа
 */
public class DateComingOrderChooser implements OperationChooser {


    public DateComingOrderChooser() {
    }

    @Override
    public OperationResult choose(List<OperationResult> operations) {
        return operations.stream()
                .min(Comparator.comparing(operation -> operation.getProductResult().getOrderResult().getOrder().getStartTime()))
                .orElseThrow(() -> new RuntimeException("Операция не найдена"));
    }
}
