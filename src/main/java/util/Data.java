package util;

import algorithm.model.order.Order;
import algorithm.model.order.Product;
import algorithm.model.order.TechProcess;
import parse.input.order.InputOrder;
import parse.input.order.InputProduct;
import parse.input.order.InputTechProcess;
import parse.input.production.InputEquipmentGroup;
import parse.input.production.InputProduction;
import parse.output.result.OutputOrderResult;
import parse.output.result.OutputProductResult;
import parse.output.result.OutputResult;

import java.time.Duration;
import java.util.ArrayList;

public class Data {
    private Data() {
    }

    public static long getOperationsCount(ArrayList<Order> orders) {
        long operationsCount = 0;

        for (Order order : orders) {
            for (Product product : order.getProducts()) {
                long operationsOnProduct = 0;
                for (TechProcess techProcess : product.getTechProcesses()) {
                    operationsOnProduct += techProcess.getOperations().size();
                }
                operationsCount += operationsOnProduct * product.getCount();
            }
        }

        return operationsCount;
    }

    public static long getPerformOperationsCount(OutputResult result) {
        long operationsCount = 0;

        for (OutputOrderResult orderResult : result.getOrderResults()) {
            for (OutputProductResult outputProductResult : orderResult.getProductResults()) {
                operationsCount += outputProductResult.getPerformedOperations().size();
            }
        }

        return operationsCount;
    }

    public static int getEquipmentCount(InputProduction production) {
        int equipmentCount = 0;

        for (InputEquipmentGroup inputEquipmentGroup : production.getEquipmentGroups()) {
            equipmentCount += inputEquipmentGroup.getEquipment().size();
        }

        return equipmentCount;
    }

    public static AlternativenessCount getAlternativenessCount(ArrayList<InputOrder> orders) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        double average = 0;
        int details = 0;

        for (InputOrder inputOrder : orders) {
            for (InputProduct inputProduct : inputOrder.getProducts()) {
                int size = inputProduct.getTechProcesses().size();

                if (size < min) {
                    min = size;
                }
                if (size > max) {
                    max = size;
                }
                average += size;
                details++;
            }
        }

        return new AlternativenessCount(min, max, average / details);
    }

    public static int getAverageOverdueDays(ArrayList<InputOrder> orders, OutputResult result) {
        int average = 0;

        for (InputOrder order :
                orders) {
            for (OutputOrderResult orderResult :
                    result.getOrderResults()) {
                if (order.getId() == orderResult.getOrderId()) {
                    average += (int) (Duration.between(order.getDeadline(), orderResult.getEndTime()).getSeconds() / 86400);
                }
            }
        }

        return average;
    }

    public static int getDetailTypesCount(ArrayList<InputOrder> orders) {
        int detailTypes = 0;
        for (InputOrder order :
                orders) {
            detailTypes += order.getProducts().size();
        }
        return detailTypes;
    }

    public static double getAverageDetailsCount(ArrayList<InputOrder> orders) {
        double detailsCount = 0;
        int detailTypes = 0;
        for (InputOrder order :
                orders) {
            for (InputProduct product :
                    order.getProducts()) {
                detailsCount += product.getCount();
            }
            detailTypes += order.getProducts().size();
        }
        return detailsCount / detailTypes;
    }

    public static double getAverageOperationsCountOnDetail(ArrayList<InputOrder> orders) {
        double operationsCount = 0;
        int techProcessCount = 0;

        for (InputOrder order :
                orders) {
            for (InputProduct product :
                    order.getProducts()) {
                for (InputTechProcess techProcess :
                        product.getTechProcesses()) {
                    operationsCount += techProcess.getOperations().size();
                }
                techProcessCount += product.getTechProcesses().size();
            }

        }

        return operationsCount / techProcessCount;
    }
}
