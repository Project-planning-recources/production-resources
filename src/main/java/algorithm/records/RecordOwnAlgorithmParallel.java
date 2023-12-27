package algorithm.records;

import algorithm.alternativeness.FromMapAlternativeElector;
import algorithm.model.order.Order;
import algorithm.model.production.Production;
import algorithm.operationchooser.FirstElementChooser;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class RecordOwnAlgorithmParallel extends RecordAbstractAlgorithmParallel {

    protected HashMap<Long, Integer> variant;
    public RecordOwnAlgorithmParallel(Production production, ArrayList<Order> orders, LocalDateTime startTime, HashMap<Long, Integer> variant, int threadCount) {
        super(production, orders, startTime, null, new FromMapAlternativeElector(variant), threadCount);
        this.variant = variant;
    }

    public RecordOwnAlgorithmParallel(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, HashMap<Long, Integer> variant, int threadCount) {
        super(inputProduction, inputOrders, startTime, null, new FromMapAlternativeElector(variant), threadCount);
        this.variant = variant;
    }

    @Override
    protected HashMap<Long, Integer> getAlternativenessMap() {
        return this.variant;
    }
}
