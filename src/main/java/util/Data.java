package util;

import parse.input.order.InputOrder;
import parse.input.order.InputOrderInformation;
import parse.input.order.InputProduct;
import parse.input.production.InputEquipmentGroup;
import parse.input.production.InputProduction;
import parse.output.result.OutputOrderResult;
import parse.output.result.OutputProductResult;
import parse.output.result.OutputResult;

import java.time.Duration;
import java.util.ArrayList;

public class Data {
    private Data() {}

    public static int getOperationsCount(OutputResult result) {
        int operationsCount = 0;

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

                if(size < min) {
                    min = size;
                }
                if(size > max) {
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
                if(order.getId() == orderResult.getOrderId()) {
                    average += (int) (Duration.between(order.getDeadline(), orderResult.getEndTime()).getSeconds() / 86400);
                }
            }
        }

        return average / orders.size();
    }

    public static class AlternativenessCount {
        public int min;
        public int max;

        public double average;

        AlternativenessCount(int min, int max, double average) {
            this.min = min;
            this.max = max;
            this.average = average;
        }
    }
}
