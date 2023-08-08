package testing;

import model.order.Operation;
import model.order.OrderInformation;
import model.order.TechProcess;
import model.production.Production;
import model.production.Schedule;
import model.production.WorkingDay;
import model.result.OperationResult;
import model.result.OrderResult;
import model.result.ProductResult;
import model.result.Result;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;

/**
 * <b>Тест на соответствие результатов работы алгоритма реальности</b>
 * <p>Тестирует функцию на физическую возможность осуществить данную укладку заказов на производстве</p>
 */
public class RealityTester {
    private RealityTester() {
    }

    /**
     * Тестирующая функция
     *
     * @param production - информация о производстве
     * @param orders - информация о заказах
     * @param result     - результаты запуска алгоритма
     * @return Подумать о том, в каком виде получать результаты работы функции и стоит ли их вообще возвращать(можно просто выводить результаты в консоль)
     */
    public static boolean test(Production production, OrderInformation orders, Result result) {
        boolean flag = true;
        LinkedList<OperationResult> operationResults = new LinkedList<>();
        for (OrderResult order : result.getOrderResults()) {
            for (ProductResult product : order.getProductResults()) {
                operationResults.addAll(product.getPerformedOperations());
                long techProcessId = product.getTechProcessId();
                TechProcess techProcess = orders.getOrderByOrderId(order.getOrderId())
                        .getProductByProductId(product.getProductId()).getTechProcessByTechProcessId(techProcessId);
                int completedOperations = 0;
                for (OperationResult operation : product.getPerformedOperations()) {
                    if(operationCompletingCheck(operation)) {
                        completedOperations++;
                    }
                    Operation operationFromInputData = techProcess.getOperationByOperationId(operation.getOperationId());
                    if (!durationCheck(operation, production.getSchedule(), operationFromInputData)) {
                        System.out.println("Операция " + operation.getOperationId() + " занимает оборудование дольше заявленного");
                        flag = false;
                    }
                    if (!sequenceOperationsCheck(product, operation, operationFromInputData)) {
                        System.out.println("Операция " + operation.getOperationId() + " нарушает технологический процесс");
                        flag = false;
                    }
                    if (!operationTimePeriodCheck(operation, product)) {
                        System.out.println("Операция " + operation.getOperationId() + " выполняется в неверное время");
                        flag = false;
                    }
                }

                if (completedOperations != techProcess.getOperations().size()) {
                    System.out.println("Не все операции из технологического процесса  " + techProcess.getId() + " были выполнены для продукта " + product.getId());
                    flag = false;
                }
                if (!productTimePeriodCheck(product, order)) {
                    System.out.println("Продукт " + product.getId() + " создаётся в неверное время");
                    flag = false;
                }
            }

            if (!orderTimePeriodCheck(order, result)) {
                System.out.println("Заказ " + order.getOrderId() + " выполняется в неверное время");
                flag = false;
            }
        }

        if(!usingSameEquipmentCheck(operationResults)) {
            System.out.println("Две операции занимают одно и то же оборудование в одно и то же время");
            flag = false;
        }

        return flag;
    }

    private static boolean operationCompletingCheck(OperationResult operation) {
        if(operation.getStartTime() == null || operation.getEndTime() == null) {
            return false;
        }

        return true;
    }

    private static boolean durationCheck(OperationResult operation, Schedule schedule, Operation operationFromInputData) {
        LocalDateTime startDttm = operation.getStartTime();
        LocalDateTime endDttm = operation.getEndTime();
        if (startDttm == null || endDttm == null) {
            return false;
        }

        long duration = 0;
        while(true) {
            if (startDttm.toLocalDate().equals(endDttm.toLocalDate())) {
                duration += Duration.between(startDttm, endDttm).get(ChronoUnit.SECONDS);
                break;
            } else {
                WorkingDay workingDay = schedule.getWorkDayByDayNumber((short)startDttm.getDayOfWeek().getValue());
                if(!workingDay.getWeekday()) {
                    startDttm = makeNextDay(startDttm, schedule);
                    continue;
                }
                duration += Duration.between(startDttm, LocalDateTime.of(startDttm.toLocalDate(), workingDay.getEndWorkingTime())).get(ChronoUnit.SECONDS);
                startDttm = makeNextDay(startDttm, schedule);
            }
        }

        return duration == operationFromInputData.getDuration();
    }

    private static boolean sequenceOperationsCheck(ProductResult product, OperationResult operation, Operation operationFromInputData) {
        LinkedList<OperationResult> operations = product.getPerformedOperations();
        int indexOfOperation = operations.indexOf(operation);
        if (indexOfOperation == 0 && operation.getPrevOperationId() != 0) {
            return false;
        } else if (indexOfOperation == (operations.size() - 1) && operation.getNextOperationId() != 0) {
            return false;
        } else if (indexOfOperation > 0 && indexOfOperation < operations.size() - 1) {
            return operation.getNextOperationId() == operationFromInputData.getNextOperationId();
        }

        return true;
    }

    private static boolean operationTimePeriodCheck(OperationResult operation, ProductResult product) {
        if(product.getStartTime() == null || product.getEndTime() == null || operation.getStartTime() == null || operation.getEndTime() == null) {
            return false;
        }
        if (operation.getStartTime().isBefore(product.getStartTime())) {
            return false;
        } else if (operation.getEndTime().isAfter(product.getEndTime())) {
            return false;
        }
        return true;
    }

    private static boolean productTimePeriodCheck(ProductResult product, OrderResult order) {
        if(order.getStartTime() == null || order.getEndTime() == null || product.getStartTime() == null || product.getEndTime() == null) {
            return false;
        }
        if (product.getStartTime().isBefore(order.getStartTime())) {
            return false;
        } else if (product.getEndTime().isAfter(order.getEndTime())) {
            return false;
        }
        return true;
    }

    private static boolean orderTimePeriodCheck(OrderResult order, Result result) {
        if(order.getStartTime() == null || order.getEndTime() == null || result.getAllStartTime() == null || result.getAllEndTime() == null) {
            return false;
        }
        if (order.getStartTime().isBefore(result.getAllStartTime())) {
            return false;
        } else if (order.getEndTime().isAfter(result.getAllEndTime())) {
            return false;
        }
        return true;
    }

    //todo: оптимизировать
    private static boolean usingSameEquipmentCheck(LinkedList<OperationResult> operationResults) {
        for(int i = 0; i < operationResults.size() - 1; i++) {
            OperationResult operationLeft = operationResults.get(i);
            if(operationLeft.getStartTime() == null || operationLeft.getEndTime() == null) {
                return false;
            }
            for (int j = i + 1; j < operationResults.size() - 1; j++) {
                OperationResult operationRight = operationResults.get(j);
                if (operationLeft.getEquipmentId() != operationRight.getEquipmentId()) {
                    continue;
                }
                if ((operationLeft.getStartTime().isBefore(operationRight.getStartTime()) && operationLeft.getEndTime().isAfter(operationRight.getEndTime())) ||
                        (operationLeft.getStartTime().isAfter(operationRight.getStartTime()) && operationLeft.getEndTime().isBefore(operationRight.getEndTime())) ||
                        (operationLeft.getStartTime().isBefore(operationRight.getStartTime()) && operationLeft.getEndTime().isAfter(operationRight.getStartTime()) && operationLeft.getEndTime().isBefore(operationRight.getEndTime())) ||
                        (operationLeft.getStartTime().isAfter(operationRight.getStartTime()) && operationLeft.getStartTime().isBefore(operationRight.getEndTime()) && operationLeft.getEndTime().isAfter(operationRight.getEndTime()))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static LocalDateTime makeNextDay(LocalDateTime dttm, Schedule schedule) {
        dttm = dttm.plusDays(1);
        WorkingDay workingDay = schedule.getWorkDayByDayNumber((short)dttm.getDayOfWeek().getValue());
        return LocalDateTime.of(dttm.toLocalDate(), workingDay.getStartWorkingTime());
    }
}
