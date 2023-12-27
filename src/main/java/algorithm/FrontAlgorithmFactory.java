package algorithm;

import algorithm.candidates.CandidatesBaseAlgorithm;
import algorithm.candidates.CandidatesOwnAlgorithm;
import algorithm.model.order.Order;
import algorithm.model.production.Production;
import algorithm.records.RecordBaseAlgorithm;
import algorithm.records.RecordBaseAlgorithmParallel;
import algorithm.records.RecordOwnAlgorithm;
import algorithm.records.RecordOwnAlgorithmParallel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class FrontAlgorithmFactory {
    private FrontAlgorithmFactory() {}

    public static Algorithm getOwnFrontAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime, HashMap<Long, Integer> variant, String frontAlgorithmType, int threadsCount) {
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

    public static Algorithm getBaseFrontAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime, String frontAlgorithmType, int frontThreadsCount) {
        switch (frontAlgorithmType.toLowerCase()) {
            case "candidates":
                return new CandidatesBaseAlgorithm(production, orders, startTime);

            case "record":
                if(frontThreadsCount == 1) {
                    return new RecordBaseAlgorithm(production, orders, startTime);
                } else if(frontThreadsCount > 1) {
                    return new RecordBaseAlgorithmParallel(production, orders, startTime, frontThreadsCount);
                } else {
                    throw new RuntimeException("Неправильное количество потоков!");
                }

            default:
                throw new RuntimeException("Неправильный тип алгоритма!");
        }
    }
}
