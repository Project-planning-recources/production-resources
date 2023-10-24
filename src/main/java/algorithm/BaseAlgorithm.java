package algorithm;

import parse.input.order.InputOrder;
import parse.input.production.InputProduction;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Базовый алгоритм</b>
 * <p>По умолчанию в нём альтернативности выбираются рандомно</p>
 */
public class BaseAlgorithm extends AbstractAlgorithm {

    public BaseAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime) {
        super(inputProduction, inputOrders, startTime, "FirstElement", "Random");
    }

}
