package testing;

import parse.input.order.*;
import parse.output.result.*;
import util.Criterion;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;


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

        double firstCriterion = Criterion.getCriterion(orders, first);
        double secondCriterion = Criterion.getCriterion(orders, second);

        System.out.println("Критерий 1 = " + firstCriterion/orders.getOrders().size() + " | Критерий 2 = " + secondCriterion/orders.getOrders().size());
    }





}
