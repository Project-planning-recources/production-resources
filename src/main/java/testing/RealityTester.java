package testing;

import parse.input.production.*;
import parse.input.order.*;
import parse.output.result.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    public static boolean test(InputProduction production, InputOrderInformation orders, OutputResult result) {
        boolean flag = true;
        HashMap<Long, ArrayList<OutputOperationResult>> performedOperationsOnEquipments = result.getPerformedOperationsOnEquipments();
        performedOperationsOnEquipments = Objects.isNull(performedOperationsOnEquipments) || performedOperationsOnEquipments.isEmpty() ?
                result.fillPerformedOperationsOnEquipments() : performedOperationsOnEquipments;
        LinkedList<OutputOperationResult> operationResults = new LinkedList<>();
        for (OutputOrderResult order : result.getOrderResults()) {
            for (OutputProductResult product : order.getProductResults()) {
                operationResults.addAll(product.getPerformedOperations());
                long techProcessId = product.getTechProcessId();
                InputTechProcess techProcess = orders.getOrderByOrderId(order.getOrderId())
                        .getProductByProductId(product.getProductId()).getTechProcessByTechProcessId(techProcessId);
//                System.out.println("order " + order);
//                System.out.println("product " + product);
//                System.out.println("techprocid " + techProcessId);
                int completedOperations = 0;
                for (OutputOperationResult operation : product.getPerformedOperations()) {
                    if(operationCompletingCheck(operation)) {
                        completedOperations++;
                    }
//                    System.out.println("oper " + operation.getOperationId());
//                    System.out.println(techProcess);
                    InputOperation operationFromInputData = techProcess.getOperationByOperationId(operation.getOperationId());
                    if (!durationCheck(operation, production.getSchedule(), operationFromInputData)) {
                        System.out.println("Операция " + operation.getOperationId() + " из тех. процесса " + techProcess.getId()
                                + " продукта " + product.getId() + " из заказа " + order.getOrderId() + " занимает оборудование дольше заявленного");
                        flag = false;
                    }
                    if (!sequenceOperationsCheck(product, operation, operationFromInputData)) {
                        System.out.println("Операция " + operation.getOperationId() + " из тех. процесса " + techProcess.getId()
                                + " продукта " + product.getId() + " из заказа " + order.getOrderId() + " нарушает технологический процесс");
                        flag = false;
                    }
                    if (!operationTimePeriodCheck(operation, product)) {
                        System.out.println("Операция " + operation.getOperationId() + " из тех. процесса " + techProcess.getId()
                                + " продукта " + product.getId() + " из заказа " + order.getOrderId() + " выполняется в неверное время");
                        flag = false;
                    }
                }

                if (completedOperations != techProcess.getOperations().size()) {
                    System.out.println("Не все операции из технологического процесса  " + techProcess.getId() + " были выполнены для продукта " +
                            product.getId() + " из заказа " + order.getOrderId());
                    flag = false;
                }
                if (!productTimePeriodCheck(product, order)) {
                    System.out.println("Продукт " + product.getId() + " из заказа " + order.getOrderId() + " создаётся в неверное время");
                    flag = false;
                }
            }

            if (!orderTimePeriodCheck(order, result)) {
                System.out.println("Заказ " + order.getOrderId() + " выполняется в неверное время");
                flag = false;
            }
        }


        List<String> errorsWithusingSameEquipment = usingSameEquipmentCheck(operationResults, performedOperationsOnEquipments);
        if(errorsWithusingSameEquipment.size() != 0) {
            errorsWithusingSameEquipment.forEach(System.out::println);
            flag = false;
        }

        return flag;
    }

    private static boolean operationCompletingCheck(OutputOperationResult operation) {
        if(operation.getStartTime() == null || operation.getEndTime() == null) {
            return false;
        }

        return true;
    }

    private static boolean durationCheck(OutputOperationResult operation, InputSchedule schedule, InputOperation operationFromInputData) {
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
                InputWorkingDay workingDay = schedule.getWorkDayByDayNumber((short)startDttm.getDayOfWeek().getValue());
                if(!workingDay.getWeekday()) {
                    startDttm = makeNextDay(startDttm, schedule);
                    continue;
                }
                duration += Duration.between(startDttm, LocalDateTime.of(startDttm.toLocalDate(), workingDay.getEndWorkingTime())).get(ChronoUnit.SECONDS);
                startDttm = makeNextDay(startDttm, schedule);
            }
        }
//        System.out.println(operation +  " duration check: " + duration + "  " + operationFromInputData.getDuration());
        return duration == operationFromInputData.getDuration();
    }

    private static boolean sequenceOperationsCheck(OutputProductResult product, OutputOperationResult operation, InputOperation operationFromInputData) {
        LinkedList<OutputOperationResult> operations = product.getPerformedOperations();
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

    private static boolean operationTimePeriodCheck(OutputOperationResult operation, OutputProductResult product) {
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

    private static boolean productTimePeriodCheck(OutputProductResult product, OutputOrderResult order) {
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

    private static boolean orderTimePeriodCheck(OutputOrderResult order, OutputResult result) {
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

    private static List<String> usingSameEquipmentCheck(LinkedList<OutputOperationResult> operationResults,
                                                        HashMap<Long, ArrayList<OutputOperationResult>> performedOperationsOnEquipments) {
        List<String> errorMessage = new ArrayList<>();

//        System.out.println(performedOperationsOnEquipments);
        for(OutputOperationResult operationLeft : operationResults) {
            if(operationLeft.getStartTime() == null || operationLeft.getEndTime() == null) {
                errorMessage.add("Операция " + operationLeft.getOperationId() + " не имеет время начала и/или конца выполнения");
            }
            ArrayList<OutputOperationResult> operationsOnEquipment = performedOperationsOnEquipments.get(operationLeft.getEquipmentId());
            for (OutputOperationResult operationRight : operationsOnEquipment) {
                if (operationLeft.getOperationId() == operationRight.getOperationId()) {
                    continue;
                }
                if (operationLeft.getEquipmentId() != operationRight.getEquipmentId()) {
                    continue;
                }
                if ((operationLeft.getStartTime().isBefore(operationRight.getStartTime()) && operationLeft.getEndTime().isAfter(operationRight.getEndTime())) ||
                        (operationLeft.getStartTime().isAfter(operationRight.getStartTime()) && operationLeft.getEndTime().isBefore(operationRight.getEndTime())) ||
                        (operationLeft.getStartTime().isBefore(operationRight.getStartTime()) && (operationLeft.getEndTime().isAfter(operationRight.getStartTime()) && !operationLeft.getEndTime().equals(operationRight.getStartTime())) && operationLeft.getEndTime().isBefore(operationRight.getEndTime())) ||
                        (operationLeft.getStartTime().isAfter(operationRight.getStartTime()) && (operationLeft.getStartTime().isBefore(operationRight.getEndTime()) && !operationRight.getEndTime().equals(operationLeft.getStartTime())) && operationLeft.getEndTime().isAfter(operationRight.getEndTime()))) {
                    errorMessage.add("Операции " + operationLeft.getOperationId() + " и " + operationRight.getOperationId() +
                            " одновременно выполняются на одном и том же оборудовании " + operationLeft.getEquipmentId() +
                            ": операция " + operationLeft.getOperationId() + " выполняется c " + operationLeft.getStartTime() + " по " + operationLeft.getEndTime() +
                            ", а операция " + operationLeft.getOperationId() + " выполняется c " + operationRight.getStartTime() + " по " + operationRight.getEndTime());
                }
            }
        }
        return errorMessage;
    }

    private static LocalDateTime makeNextDay(LocalDateTime dttm, InputSchedule schedule) {
        dttm = dttm.plusDays(1);
        InputWorkingDay workingDay = schedule.getWorkDayByDayNumber((short)dttm.getDayOfWeek().getValue());
        return LocalDateTime.of(dttm.toLocalDate(), workingDay.getStartWorkingTime());
    }
}
