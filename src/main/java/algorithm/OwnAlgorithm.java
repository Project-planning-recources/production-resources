package algorithm;

import algorithm.model.order.Order;
import algorithm.model.order.Product;
import algorithm.model.production.Production;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Собственный алгоритм</b>
 */
public class OwnAlgorithm extends AbstractAlgorithm {

    protected ArrayList<Integer> alternativeness;

    @Override
    protected long chooseAlternativeness(long concreteProductId, Product product) {
        // todo: ПРОВЕРИТЬ АЙДИ И ВОЗВРАЩАЕМОЕ ЗНАЧЕНИЕ
        throw new RuntimeException("ПРОВЕРИТЬ АЙДИ И ВОЗВРАЩАЕМОЕ ЗНАЧЕНИЕ");
//        return this.alternativeness.get((int) concreteProductId - 1);
    }

    public OwnAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, String operationChooser, ArrayList<Integer> alternativeness) {
        super(inputProduction, inputOrders, startTime, operationChooser, null, 1);
        this.alternativeness = alternativeness;
    }

}
