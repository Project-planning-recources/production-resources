package algorithm.parallel;

import algorithm.Algorithm;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import util.Pair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class ParallelRecordSolver extends ParallelAlphaSolver1 {

    private Boolean parallelRecord;
    public ParallelRecordSolver(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, int startVariatorCount, int variatorBudget,
                                ArrayList<Pair<HashMap<Long, Integer>, Double>> variation, HashMap<Long, Boolean> variantPairs, ParallelAlphaVariatorAlgorithm1 main, Semaphore variationSemaphore, Semaphore pairsSemaphore,
                                Boolean parallelRecord) {
        super(inputProduction, inputOrders, startTime, startVariatorCount, variatorBudget, variation, variantPairs, main, variationSemaphore, pairsSemaphore);
        this.parallelRecord = parallelRecord;
    }

    @Override
    protected Algorithm getAlgorithm(HashMap<Long, Integer> variant) {
        return null;
    }
}
