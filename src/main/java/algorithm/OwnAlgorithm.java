package algorithm;

import algorithm.alternativeness.AlternativeElector;
import algorithm.operationchooser.OperationChooser;
import model.order.Order;
import model.production.Production;
import model.result.OperationResult;
import model.result.Result;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Собственный алгоритм</b>
 */
public class OwnAlgorithm extends AbstractAlgorithm {

    OwnAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime, String operationChooser, String alternativeElector) throws Exception {
        super(production, orders, startTime, operationChooser, alternativeElector);
    }

}
