package testing;

import generator.GeneratedData;
import generator.GeneratorParameters;
import parse.input.order.*;
import parse.input.production.InputEquipmentGroup;
import parse.input.production.InputProduction;
import parse.input.production.InputSchedule;
import parse.input.production.InputWorkingDay;

import java.time.*;
import java.util.List;

public class GeneratorTester {

    private GeneratorTester() {}

    /**
     * Тестирующая функция
     *
     * Проверяет, что сгенерированные данные соответствуют параметрам своего генератора
     * @param generatorParameters - параметры генератора
     * @param generatedData - сгенерированные данные
     * @return Boolean
     */
    public static boolean test(GeneratorParameters generatorParameters, GeneratedData generatedData) {
        InputProduction inputProduction = generatedData.getInputProduction();

        InputSchedule inputSchedule = inputProduction.getSchedule();
        if (generatorParameters.daysForSchedule != inputSchedule.getWeek().size()) {
            System.out.println("Количество рабочих дней (" + inputSchedule.getWeek().size()
                    + ") не совпадает с заданным количеством (" + generatorParameters.daysForSchedule + ")");
            return false;
        }

        for (InputWorkingDay day : inputSchedule.getWeek()) {
            LocalTime startTime = LocalTime.of(generatorParameters.startWorkingTime, 0);
            if (!startTime.equals(day.getStartWorkingTime())) {
                System.out.println("Время начала рабочего дня (" + day.getStartWorkingTime().toString()
                        + ") не совпадает с заданным (" + startTime + ")");
                return false;
            }

            LocalTime endTime = LocalTime.of(generatorParameters.endWorkingTime, 0);
            if (!endTime.equals(day.getEndWorkingTime())) {
                System.out.println("Время конца рабочего дня (" + day.getEndWorkingTime().toString()
                        + ") не совпадает с заданным (" + startTime + ")");
                return false;
            }
        }

        int equipmentGroupsCount = inputProduction.getEquipmentGroups().size();
        if (!(generatorParameters.minEquipmentGroupCount <= equipmentGroupsCount
                && generatorParameters.maxEquipmentGroupCount >= equipmentGroupsCount)) {
            System.out.println("Количество групп оборудования (" + equipmentGroupsCount
                    + ") не входит в заданный промежуток [" + generatorParameters.minEquipmentGroupCount
                    + ", " + generatorParameters.maxEquipmentGroupCount + "]");
            return false;
        }

        for (InputEquipmentGroup group : inputProduction.getEquipmentGroups()) {
            if (!(generatorParameters.minEquipmentCount <= group.getEquipment().size()
                    && generatorParameters.maxEquipmentCount >= group.getEquipment().size())) {
                System.out.println("Количество оборудования в группе "+ group.getId() + "(" + group.getEquipment().size()
                        + ") не входит в заданный промежуток [" + generatorParameters.minEquipmentCount
                        + ", " + generatorParameters.maxEquipmentCount + "]");
                return false;
            }
        }

        InputOrderInformation inputOrderInformation = generatedData.getInputOrderInformation();

        if (generatorParameters.ordersCount != inputOrderInformation.getOrders().size()) {
            System.out.println("Количество заказов (" + inputOrderInformation.getOrders().size()
                    + ") не совпадает с заданным количеством (" + generatorParameters.ordersCount + ")");
            return false;
        }

        LocalDateTime minStartOrderTime = LocalDateTime.ofEpochSecond(
                generatorParameters.minOrderStartTime.getTime()/1000, 0, ZoneOffset.ofHours(0));

        LocalDateTime maxStartOrderTime = LocalDateTime.ofEpochSecond(
                generatorParameters.maxOrderStartTime.getTime()/1000, 0, ZoneOffset.ofHours(0));

        for (InputOrder order : inputOrderInformation.getOrders()) {
            if (!(order.getStartTime().isAfter(minStartOrderTime) && order.getStartTime().isBefore(maxStartOrderTime))) {
                System.out.println("Раннее время выполнения заказа (" + order.getStartTime()
                        + ") не входит в заданный промежуток [" + minStartOrderTime
                        + ", " + maxStartOrderTime + "]");
                return false;
            }

            long durationDays = Duration.between(order.getStartTime(), order.getDeadline()).toDays();
            if (!(generatorParameters.minDurationTimeInDays <= durationDays && durationDays <= generatorParameters.maxDurationTimeInDays)) {
                System.out.println("Длительность выполнения заказа (" + durationDays
                        + ") не входит в заданный промежуток [" + generatorParameters.minDurationTimeInDays
                        + ", " + generatorParameters.maxDurationTimeInDays + "]");
                return false;
            }

            List<InputProduct> products = order.getProducts();
            if (!(generatorParameters.minDetailsTypeCount <= products.size() &&
                    products.size() <= generatorParameters.maxDetailsTypeCount)) {
                System.out.println("Количество типов деталей (" + products.size()
                        + ") не входит в заданный промежуток [" + generatorParameters.minDetailsTypeCount
                        + ", " + generatorParameters.maxDetailsTypeCount + "]");
                return false;
            }

            for (InputProduct product : products) {
                List<InputTechProcess> processes = product.getTechProcesses();
                if (!(generatorParameters.minTechProcessCount <= processes.size() &&
                        processes.size() <= generatorParameters.maxTechProcessCount)) {
                    System.out.println("Количество техпроцессов (" + processes.size()
                            + ") не входит в заданный промежуток [" + generatorParameters.minTechProcessCount
                            + ", " + generatorParameters.maxTechProcessCount + "]");
                    return false;
                }

                for (InputTechProcess process : processes) {
                    List<InputOperation> operations = process.getOperations();
                    if (!(generatorParameters.minOperationsCount <= operations.size() &&
                            operations.size() <= generatorParameters.maxOperationsCount)) {
                        System.out.println("Количество операций (" + operations.size()
                                + ") не входит в заданный промежуток [" + generatorParameters.minOperationsCount
                                + ", " + generatorParameters.maxOperationsCount + "]");
                        return false;
                    }

                    for (InputOperation operation : operations) {
                        if (!(generatorParameters.minOperationDuration <= operation.getDuration() &&
                                operation.getDuration() <= generatorParameters.maxOperationDuration)) {
                            System.out.println("Время выполнения операции (" + operation.getDuration()
                                    + ") не входит в заданный промежуток [" + generatorParameters.minOperationDuration
                                    + ", " + generatorParameters.maxOperationDuration + "]");
                            return false;
                        }

                        if (!(1 <= operation.getRequiredEquipment() &&
                                operation.getRequiredEquipment() <= equipmentGroupsCount)) {
                            System.out.println("Требуемая группа оборудования (" + operation.getRequiredEquipment()
                                    + ") не входит в число имеющихся групп [" + 1
                                    + ", " + equipmentGroupsCount + "]");
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}
