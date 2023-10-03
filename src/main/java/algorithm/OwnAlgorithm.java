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
    protected int chooseAlternativeness(long concreteProductId, Product product) {
        return this.alternativeness.get((int) concreteProductId - 1) - 1;
    }

    OwnAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, String operationChooser, ArrayList<Integer> alternativeness) throws Exception {
        super(inputProduction, inputOrders, startTime, operationChooser, null);
        this.alternativeness = alternativeness;
    }

}
