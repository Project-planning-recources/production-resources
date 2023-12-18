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

public abstract class RecordAbstractAlgorithmParallel extends AbstractAlgorithm {

    protected int threadCount;
    public RecordAbstractAlgorithmParallel(Production production, ArrayList<Order> orders, LocalDateTime startTime, OperationChooser operationChooser, AlternativeElector alternativeElector, int threadCount) {
        super(production, orders, startTime);
        this.threadCount = threadCount;
    }

    public RecordAbstractAlgorithmParallel(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, OperationChooser operationChooser, AlternativeElector alternativeElector,  int threadCount) {
        super(inputProduction, inputOrders, startTime);
        this.threadCount = threadCount;
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
