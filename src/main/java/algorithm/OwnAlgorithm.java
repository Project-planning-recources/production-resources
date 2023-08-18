package algorithm;

import algorithm.model.order.Order;
import algorithm.model.production.Production;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Собственный алгоритм</b>
 */
public class OwnAlgorithm extends AbstractAlgorithm {

    OwnAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, String operationChooser, String alternativeElector) throws Exception {
        super(inputProduction, inputOrders, startTime, operationChooser, alternativeElector);
    }

}
