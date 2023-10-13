package testing;

import parse.input.order.*;
import parse.output.result.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static java.nio.file.Files.size;


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

    private static HashMap<Long, HashMap<Long, Double>> productWorksMap;

    public static void test(InputOrderInformation orders, OutputResult first, OutputResult second) {
        // todo: Проверять, что результаты соответствуют файлу заказов
        ArrayList<OutputOrderResult> firstResultOrders = first.getOrderResults();
        ArrayList<OutputOrderResult> secondResultOrders = second.getOrderResults();

        for (InputOrder order : orders.getOrders()) {
            fillProductWorksMap(order);

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

    private static void fillProductWorksMap(InputOrder order) {
        productWorksMap = new HashMap<>();

        order.getProducts().forEach(inputProduct -> {
            productWorksMap.putIfAbsent(inputProduct.getId(), new HashMap<>());

            inputProduct.getTechProcesses().forEach(inputTechProcess -> {
                double works = 0;

                for (int i = 0; i < inputTechProcess.getOperations().size(); i++) {
                    works += inputTechProcess.getOperations().get(i).getDuration();
                }

                productWorksMap.get(inputProduct.getId()).putIfAbsent(inputTechProcess.getId(), works);

            });
        });

    }

    private static double getCriterion(InputOrder order, OutputOrderResult orderResult) {
        double overdue = 0;
        double allWorks = 0;

        for (OutputProductResult productResult :
                orderResult.getProductResults()) {
            if (productResult.getEndTime().isAfter(order.getDeadline())) {
                overdue += (double) Duration.between(order.getDeadline(), productResult.getEndTime()).getSeconds();
            }

            allWorks += productWorksMap.get(productResult.getProductId()).get(productResult.getTechProcessId());
        }
        System.out.println(allWorks);
        System.out.println(overdue);
        return overdue / allWorks;
    }


}
