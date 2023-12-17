package algorithm.records;

import algorithm.alternativeness.RandomAlternativeElector;
import algorithm.model.order.Order;
import algorithm.model.production.Production;
import algorithm.operationchooser.FirstElementChooser;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RecordBaseAlgorithmParallel extends RecordAbstractAlgorithmParallel {
    public RecordBaseAlgorithmParallel(Production production, ArrayList<Order> orders, LocalDateTime startTime, int threadCount) {
        super(production, orders, startTime, null, new RandomAlternativeElector(), threadCount);
    }

    public RecordBaseAlgorithmParallel(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, int threadCount) {
        super(inputProduction, inputOrders, startTime, null, new RandomAlternativeElector(), threadCount);
    }
}
