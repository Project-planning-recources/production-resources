package algorithm;

import algorithm.alternativeness.RandomAlternativeElector;
import algorithm.operationchooser.GreedyOperationChooser;
import model.order.Order;
import model.production.Production;
import model.result.OperationResult;
import model.result.Result;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Базовый алгоритм</b>
 * <p>По умолчанию в нём альтернативности выбираются рандомно</p>
 */
public class BaseAlgorithm extends AbstractAlgorithm {

    BaseAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime) throws Exception {
        super(production, orders, startTime, "FirstElement", "Random");
    }

}
