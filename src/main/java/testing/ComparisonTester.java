package testing;

import parse.input.order.*;
import parse.output.result.*;

import java.time.Duration;
import java.util.ArrayList;


/**
 * <b>Тестер, сравнивающий результаты работы двух алгоритмов</b>
 */
public class ComparisonTester {
    private ComparisonTester() {
    }

    /****************************************************
     * Тестирующая функция
     * Подразумеваем, что результаты корректны и проверены до вызова данной функции
     *
     * @param orders - информация о заказах
     * @param first - результаты работы первого алгоритма
     * @param second - результаты работы второго алгоритма
     *
     * @return Подумать о том, в каком виде получать результаты работы функции и стоит ли их вообще возвращать(можно просто выводить результаты в консоль)
     */


    public static void test(InputOrderInformation orders, OutputResult first, OutputResult second) {
        // todo: Проверять, что результаты соответствуют файлу заказов
        ArrayList<OutputOrderResult> firstResultOrders = first.getOrderResults();
        ArrayList<OutputOrderResult> secondResultOrders = second.getOrderResults();

        for (InputOrder order : orders.getOrders()) {
            double firstCriterion = 0;
            for (OutputOrderResult firstOrderResult : firstResultOrders) {
                if (order.getId() == firstOrderResult.getOrderId()) {
                    firstCriterion = getCriterion(order, firstOrderResult);
                }
            }

            double secondCriterion = 0;
            for (OutputOrderResult secondOrderResult : secondResultOrders) {
                if (order.getId() == secondOrderResult.getOrderId()) {
                    secondCriterion = getCriterion(order, secondOrderResult);
                }
            }
            System.out.println("Заказ " + order.getId() + ": Критерий 1 = " + firstCriterion + " | Критерий 2 = " + secondCriterion);
        }

    }

    private static double getCriterion(InputOrder order, OutputOrderResult orderResult) {
        double overdue = 0;
        double overdueProducts = 0;

        overdue = (double) Duration.between(order.getDeadline(), orderResult.getEndTime()).getSeconds() / 3600;

        if (overdue < 0) {
            return overdue;
        } else {
            for (OutputProductResult productResult :
                    orderResult.getProductResults()) {
                if (productResult.getEndTime().isAfter(order.getDeadline())) {
                    overdueProducts += (double) Duration.between(order.getDeadline(), productResult.getEndTime()).getSeconds() / 3600;
                }
            }
            return overdue * overdueProducts;
        }
    }
}
