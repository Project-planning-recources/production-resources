package algorithm.candidates;

import algorithm.alternativeness.RandomAlternativeElector;
import algorithm.model.order.Order;
import algorithm.model.production.Production;
import algorithm.operationchooser.FirstElementChooser;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Базовый алгоритм</b>
 * <p>По умолчанию в нём альтернативности выбираются рандомно</p>
 */
public class CandidatesBaseAlgorithm extends CandidatesAbstractAlgorithm {

    public CandidatesBaseAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime) {
        super(production, orders, startTime, new FirstElementChooser(), new RandomAlternativeElector());
    }
    public CandidatesBaseAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime) {
        super(inputProduction, inputOrders, startTime, new FirstElementChooser(), new RandomAlternativeElector());
    }

}
