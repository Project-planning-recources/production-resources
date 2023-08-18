package algorithm.operationchooser;

import algorithm.model.result.OperationResult;

import java.util.List;

/**
 * Выборщик, берущий первую операцию из списка ожидающих операций
 */
public class FirstElementChooser implements OperationChooser {

    public FirstElementChooser() {
    }

    @Override
    public OperationResult choose(List<OperationResult> operations) {
        return operations.get(0);
    }
}
