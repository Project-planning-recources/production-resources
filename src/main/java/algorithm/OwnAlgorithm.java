package algorithm;

import algorithm.model.order.Order;
import algorithm.model.order.Product;
import algorithm.model.production.Production;
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

    protected HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> alternativeness;

    @Override
    protected long chooseAlternativeness(long concreteProductId, Product product) {
        // todo: переделать функцию, идти по мапе детали и вычитать единицу соответствующую альтернативности, возвращая этот тип альтернативности. Поменять принимаемые параметры
        throw new RuntimeException("ПРОВЕРИТЬ АЙДИ И ВОЗВРАЩАЕМОЕ ЗНАЧЕНИЕ");
//        return this.alternativeness.get((int) concreteProductId - 1);
    }

    public OwnAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, String operationChooser, HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> alternativeness) {
        super(inputProduction, inputOrders, startTime, operationChooser, null);
        this.alternativeness = alternativeness;
    }

}
