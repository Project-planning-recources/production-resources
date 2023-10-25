package util;

import parse.input.order.InputOrder;
import parse.input.order.InputOrderInformation;
import parse.output.result.OutputOrderResult;
import parse.output.result.OutputProductResult;
import parse.output.result.OutputResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

public class Criterion {
    private Criterion() {}

    public static double getCriterion(InputOrderInformation ordersInformation, OutputResult result) {
        double criterion = 0;

        for (InputOrder order : ordersInformation.getOrders()) {
            HashMap<Long, HashMap<Long, Double>> productWorksMap = getProductWorksMap(order);
            System.out.println(productWorksMap);
            for (OutputOrderResult firstOrderResult : result.getOrderResults()) {
                if (order.getId() == firstOrderResult.getOrderId()) {
                    criterion += Criterion.getCriterionForOrder(order, firstOrderResult, productWorksMap);
                }
            }
        }

        return criterion / ordersInformation.getOrders().size();
    }

    private static double getCriterionForOrder(InputOrder order, OutputOrderResult orderResult, HashMap<Long, HashMap<Long, Double>> productWorksHashMap) {
        double overdue = 0;
        double allWorks = 0;

        for (OutputProductResult productResult :
                orderResult.getProductResults()) {
            if (productResult.getEndTime().isAfter(order.getDeadline())) {
                overdue += (double) Duration.between(order.getDeadline(), productResult.getEndTime()).getSeconds();
            }

            allWorks += productWorksHashMap.get(productResult.getProductId()).get(productResult.getTechProcessId());
        }
        return overdue / allWorks;
    }

    private static HashMap<Long, HashMap<Long, Double>> getProductWorksMap(InputOrder order) {
        HashMap<Long, HashMap<Long, Double>> productWorksMap = new HashMap<>();

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


        return productWorksMap;
    }
}
