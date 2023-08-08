package algorithm;

import algorithm.alternativeness.AlternativeElector;
import algorithm.operationchooser.OperationChooser;
import model.order.Order;
import model.production.Production;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Фабрика алгоритмов</b>
 * <p>Потом может быть придумаем что нибудь покруче, может быть настройку, но пока так</p>
 */
public class AlgorithmFactory {
    private AlgorithmFactory() {}

    public static Algorithm getNewBaseAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime) throws Exception {
        return new BaseAlgorithm(production, orders, startTime);
    }

    public static Algorithm getNewOwnAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime,
                                               String operationChooser, String alternativeElector) throws Exception {
        return new OwnAlgorithm(production, orders, startTime, operationChooser, alternativeElector);
    }
}
