package algorithm;

import parse.input.order.InputOrder;
import parse.input.production.InputProduction;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Фабрика алгоритмов</b>
 * <p>Потом может быть придумаем что нибудь покруче, может быть настройку, но пока так</p>
 */
public class AlgorithmFactory {
    private AlgorithmFactory() {}

    public static Algorithm getNewBaseAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime) throws Exception {
        return new BaseAlgorithm(inputProduction, inputOrders, startTime);
    }

    public static Algorithm getNewOwnAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime) throws Exception {
        return new AlternativenessOwnAlgorithm(inputProduction, inputOrders, startTime);
    }
}
