package algorithm.records;

import algorithm.AbstractAlgorithm;
import algorithm.alternativeness.AlternativeElector;
import algorithm.model.order.Order;
import algorithm.model.production.Production;
import algorithm.operationchooser.OperationChooser;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;

import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class RecordsAbstractAlgorithm extends AbstractAlgorithm {
    public RecordsAbstractAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime, OperationChooser operationChooser, AlternativeElector alternativeElector) {
        super(production, orders, startTime);
    }

    public RecordsAbstractAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, OperationChooser operationChooser, AlternativeElector alternativeElector) {
        super(inputProduction, inputOrders, startTime);
    }

    @Override
    protected void tickOfTime(LocalDateTime timeTick) throws Exception {

    }

    @Override
    protected void startOperations(LocalDateTime timeTick) {

    }

    @Override
    protected void addNewOrderOperations(Order order) {

    }


}
