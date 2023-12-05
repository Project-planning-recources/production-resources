package algorithm.candidates;

import algorithm.alternativeness.AlternativeElector;
import algorithm.model.order.Order;
import algorithm.model.production.Production;
import algorithm.operationchooser.OperationChooser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <b>Собственный алгоритм</b>
 */
public class CandidatesOwnAlgorithm extends CandidatesAbstractAlgorithm {

    protected HashMap<Long, Integer> variant;

    public CandidatesOwnAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime, OperationChooser operationChooser, AlternativeElector alternativeElector, HashMap<Long, Integer> variant) {
        super(production, orders, startTime, operationChooser, alternativeElector);
        this.variant = variant;

    }

    @Override
    protected HashMap<Long, Integer> getAlternativenessMap() {
        return this.variant;
    }
}
