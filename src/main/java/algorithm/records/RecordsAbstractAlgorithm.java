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

public class RecordsAbstractAlgorithm extends AbstractAlgorithm {
    //todo: записать сюда работу с кандидатами
    public RecordsAbstractAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime, OperationChooser operationChooser, AlternativeElector alternativeElector) {
        super(production, orders, startTime, operationChooser, alternativeElector);
    }

    public RecordsAbstractAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, OperationChooser operationChooser, AlternativeElector alternativeElector) {
        super(inputProduction, inputOrders, startTime, operationChooser, alternativeElector);
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
