package algorithm;

import algorithm.alternativeness.AlternativeElector;
import algorithm.model.order.Order;
import algorithm.model.order.Product;
import algorithm.model.production.Production;
import algorithm.operationchooser.OperationChooser;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <b>Собственный алгоритм</b>
 */
public class OwnAlgorithm extends AbstractAlgorithm {

    protected HashMap<Long, Integer> variant;

    public OwnAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime, OperationChooser operationChooser, AlternativeElector alternativeElector, HashMap<Long, Integer> variant) {
        super(production, orders, startTime, operationChooser, alternativeElector);
        this.variant = variant;

    }

    @Override
    protected HashMap<Long, Integer> getAlternativenessMap() {
        return this.variant;
    }
}
