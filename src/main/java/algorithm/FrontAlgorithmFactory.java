package algorithm;

import algorithm.candidates.CandidatesOwnAlgorithm;
import algorithm.model.order.Order;
import algorithm.model.production.Production;
import algorithm.records.RecordOwnAlgorithm;
import algorithm.records.RecordOwnAlgorithmParallel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class FrontAlgorithmFactory {
    private FrontAlgorithmFactory() {}

    public static Algorithm getFrontAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime, HashMap<Long, Integer> variant, String frontAlgorithmType, int threadsCount) {
        switch (frontAlgorithmType.toLowerCase()) {
            case "candidates":
                return new CandidatesOwnAlgorithm(production, orders, startTime, variant);

            case "record":
                if(threadsCount == 1) {
                    return new RecordOwnAlgorithm(production, orders, startTime, variant);
                } else if(threadsCount > 1) {
                    return new RecordOwnAlgorithmParallel(production, orders, startTime, variant, threadsCount);
                } else {
                    throw new RuntimeException("Неправильное количество потоков!");
                }

            default:
                throw new RuntimeException("Неправильный тип алгоритма!");
        }
    }
}
