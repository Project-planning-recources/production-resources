package algorithm.records;

import algorithm.alternativeness.AlternativeElector;
import algorithm.alternativeness.RandomAlternativeElector;
import algorithm.model.order.Order;
import algorithm.model.production.Production;
import algorithm.operationchooser.FirstElementChooser;
import algorithm.operationchooser.OperationChooser;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RecordBaseAlgorithm extends RecordsAbstractAlgorithm {
    public RecordBaseAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime) {
        super(production, orders, startTime, null, new RandomAlternativeElector());
    }

    public RecordBaseAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime) {
        super(inputProduction, inputOrders, startTime, null, new RandomAlternativeElector());
    }
}
