package util;

import algorithm.model.order.Order;
import algorithm.model.order.Product;
import algorithm.model.order.TechProcess;
import algorithm.model.production.Production;
import com.ctc.wstx.exc.WstxOutputException;
import parse.input.order.InputOrder;
import parse.input.order.InputOrderInformation;
import parse.output.result.OutputOrderResult;
import parse.output.result.OutputProductResult;
import parse.output.result.OutputResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Criterion {
    private Criterion() {}

    public static double getBackpackCriterion(ArrayList<Order> orders, HashMap<Long, Long> productionPower, HashMap<Long, HashMap<Long, Integer>> techProcessPower, HashMap<Long, Integer> variant) {
        double criterion = 0;
        HashMap<Long, Long> overspanding = new HashMap<>(productionPower);
        for (Order order : orders) {
            for (Product product : order.getProducts()) {
                for (TechProcess techProcess : product.getTechProcesses()) {
                    long hash = Hash.hash(order.getId(), product.getId(), techProcess.getId());
                    int count = variant.get(hash);
                    HashMap<Long, Integer> equipPower = techProcessPower.get(hash);
                    equipPower.forEach((eId, power) -> {
                        overspanding.replace(eId, overspanding.get(eId) - (long) count * power);
                    });
                }
            }
        }
        for (Map.Entry<Long, Long> entry : overspanding.entrySet()) {
            if(entry.getValue() < 0) {
                criterion += (double) (-1 * entry.getValue()) / productionPower.get(entry.getKey());
            }
        }
        return criterion;
    }

    public static double getCriterion(InputOrderInformation ordersInformation, OutputResult result) {
        double criterion = 0;

        for (InputOrder order : ordersInformation.getOrders()) {
            HashMap<Long, HashMap<Long, Double>> productWorksMap = getProductWorksMap(order);
            for (OutputOrderResult firstOrderResult : result.getOrderResults()) {
                if (order.getId() == firstOrderResult.getOrderId()) {
                    criterion += Criterion.getCriterionForOrder(order, firstOrderResult, productWorksMap);
                }
            }
        }

        return criterion / ordersInformation.getOrders().size();
    }

    public static double getCriterion(ArrayList<Order> orders, OutputResult result) {
        double criterion = 0;

        for (Order order : orders) {
            HashMap<Long, HashMap<Long, Double>> productWorksMap = getProductWorksMap(order);
            for (OutputOrderResult firstOrderResult : result.getOrderResults()) {
                if (order.getId() == firstOrderResult.getOrderId()) {
                    criterion += Criterion.getCriterionForOrder(order, firstOrderResult, productWorksMap);
                }
            }
        }

        return criterion / orders.size();
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

    private static double getCriterionForOrder(Order order, OutputOrderResult orderResult, HashMap<Long, HashMap<Long, Double>> productWorksHashMap) {
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

    private static HashMap<Long, HashMap<Long, Double>> getProductWorksMap(Order order) {
        HashMap<Long, HashMap<Long, Double>> productWorksMap = new HashMap<>();

        order.getProducts().forEach(product -> {
            productWorksMap.putIfAbsent(product.getId(), new HashMap<>());

            product.getTechProcesses().forEach(techProcess -> {
                double works = 0;

                for (int i = 0; i < techProcess.getOperations().size(); i++) {
                    works += techProcess.getOperations().get(i).getDuration();
                }

                productWorksMap.get(product.getId()).putIfAbsent(techProcess.getId(), works);

            });
        });


        return productWorksMap;
    }
}
