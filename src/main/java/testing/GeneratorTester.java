package testing;

import generator.GeneratedData;
import generator.GeneratorParameters;
import parse.input.order.*;
import parse.input.production.InputEquipmentGroup;
import parse.input.production.InputProduction;
import parse.input.production.InputSchedule;
import parse.input.production.InputWorkingDay;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        boolean flag = true;
        InputProduction inputProduction = generatedData.getInputProduction();

        InputSchedule inputSchedule = inputProduction.getSchedule();
        if (Objects.isNull(inputSchedule) || Objects.isNull(inputSchedule.getWeek()) || inputSchedule.getWeek().isEmpty()) {
            System.out.println("Рабочее расписание не создано");
            flag = false;
        }
        int iwdCounter = 0;
        for (InputWorkingDay iwd :
                inputSchedule.getWeek()) {
            if(iwd.getWeekday()) {
                iwdCounter++;
            }
        }
        if (generatorParameters.daysForSchedule != iwdCounter) {
            System.out.println("Количество рабочих дней (" + iwdCounter
                    + ") не совпадает с заданным количеством (" + generatorParameters.daysForSchedule + ")");
            flag = false;
        }

        if (inputSchedule.getWeek().size() > 7) {
            System.out.println("Сгенерировано более 7 дней");
            flag = false;
        }

        for (InputWorkingDay day : inputSchedule.getWeek()) {
            LocalTime startTime = LocalTime.of(generatorParameters.startWorkingTime, 0);
            if (!startTime.equals(day.getStartWorkingTime()) && day.getWeekday()) {
                System.out.println("Время начала рабочего дня (" + day.getStartWorkingTime().toString()
                        + ") не совпадает с заданным (" + startTime + ")");
                flag = false;
            }

            LocalTime endTime = LocalTime.of(generatorParameters.endWorkingTime, 0);
            if (!endTime.equals(day.getEndWorkingTime()) && day.getWeekday()) {
                System.out.println("Время конца рабочего дня (" + day.getEndWorkingTime().toString()
                        + ") не совпадает с заданным (" + startTime + ")");
                flag = false;
            }
        }

        if (Objects.isNull(inputProduction.getEquipmentGroups())) {
            System.out.println("Группы оборудования не были созданы");
            flag = false;
        }

        int equipmentGroupsCount = inputProduction.getEquipmentGroups().size();
        if (flag && !(generatorParameters.minEquipmentGroupCount <= equipmentGroupsCount
                && generatorParameters.maxEquipmentGroupCount >= equipmentGroupsCount)) {
            System.out.println("Количество групп оборудования (" + equipmentGroupsCount
                    + ") не входит в заданный промежуток [" + generatorParameters.minEquipmentGroupCount
                    + ", " + generatorParameters.maxEquipmentGroupCount + "]");
            flag = false;
        }

        for (InputEquipmentGroup group : inputProduction.getEquipmentGroups()) {
            if (Objects.isNull(group)) {
                System.out.println("Оборудования не были созданы");
                flag = false;
                continue;
            }

            if (!(generatorParameters.minEquipmentCount <= group.getEquipment().size()
                    && generatorParameters.maxEquipmentCount >= group.getEquipment().size())) {
                System.out.println("Количество оборудования в группе "+ group.getId() + "(" + group.getEquipment().size()
                        + ") не входит в заданный промежуток [" + generatorParameters.minEquipmentCount
                        + ", " + generatorParameters.maxEquipmentCount + "]");
                flag = false;
            }
        }

        InputOrderInformation inputOrderInformation = generatedData.getInputOrderInformation();

        if (Objects.isNull(inputOrderInformation.getOrders())) {
            System.out.println("Заказы не были созданы");
            flag = false;
        }

        if (flag && generatorParameters.ordersCount != inputOrderInformation.getOrders().size()) {
            System.out.println("Количество заказов (" + inputOrderInformation.getOrders().size()
                    + ") не совпадает с заданным количеством (" + generatorParameters.ordersCount + ")");
            flag = false;
        }

        LocalDateTime minStartOrderTime = LocalDateTime.ofEpochSecond(
                generatorParameters.minOrderStartTime.getTime()/1000, 0, ZoneOffset.ofHours(0));

        for (InputOrder order : inputOrderInformation.getOrders()) {
            long diffBetweenDates = ChronoUnit.DAYS.between(minStartOrderTime, order.getStartTime());
            if (flag && !(0 <= diffBetweenDates && diffBetweenDates <= generatorParameters.maxDurationStartTime)) {
                System.out.println("Раннее время выполнения заказа (" + order.getStartTime()
                        + ") не входит в заданный промежуток [" + minStartOrderTime
                        + ", " + minStartOrderTime.plusDays(generatorParameters.maxDurationStartTime) + "]");
                flag = false;
            }

            long durationDays = Duration.between(order.getStartTime(), order.getDeadline()).toDays();
            if (flag && !(generatorParameters.minDurationTimeInDays <= durationDays && durationDays
                    <= generatorParameters.maxDurationTimeInDays)) {
                System.out.println("Длительность выполнения заказа (" + durationDays
                        + ") не входит в заданный промежуток [" + generatorParameters.minDurationTimeInDays
                        + ", " + generatorParameters.maxDurationTimeInDays + "]");
                flag = false;
            }

            List<InputProduct> products = order.getProducts();
            if (Objects.isNull(products)) {
                System.out.println("Детали не были созданы");
                flag = false;
                continue;
            }

            if (!(generatorParameters.minDetailsTypeCount <= products.size() &&
                    products.size() <= generatorParameters.maxDetailsTypeCount)) {
                System.out.println("Количество типов деталей (" + products.size()
                        + ") не входит в заданный промежуток [" + generatorParameters.minDetailsTypeCount
                        + ", " + generatorParameters.maxDetailsTypeCount + "]");
                flag = false;
            }

            for (InputProduct product : products) {
                List<InputTechProcess> processes = product.getTechProcesses();
                if (Objects.isNull(processes)) {
                    System.out.println("Техпроцессы не были созданы");
                    flag = false;
                    continue;
                }

                if (!(generatorParameters.minTechProcessCount <= processes.size() &&
                        processes.size() <= generatorParameters.maxTechProcessCount)) {
                    System.out.println("Количество техпроцессов (" + processes.size()
                            + ") не входит в заданный промежуток [" + generatorParameters.minTechProcessCount
                            + ", " + generatorParameters.maxTechProcessCount + "]");
                    flag = false;
                }

                for (InputTechProcess process : processes) {
                    List<InputOperation> operations = process.getOperations();
                    if (Objects.isNull(operations)) {
                        System.out.println("Операцмм не были созданы");
                        flag = false;
                        continue;
                    }

                    if (!(generatorParameters.minOperationsCount <= operations.size() &&
                            operations.size() <= generatorParameters.maxOperationsCount)) {
                        System.out.println("Количество операций (" + operations.size()
                                + ") не входит в заданный промежуток [" + generatorParameters.minOperationsCount
                                + ", " + generatorParameters.maxOperationsCount + "]");
                        flag = false;
                    }

                    for (InputOperation operation : operations) {
                        if (!(generatorParameters.minOperationDuration <= operation.getDuration() &&
                                operation.getDuration() <= generatorParameters.maxOperationDuration)) {
                            System.out.println("Время выполнения операции (" + operation.getDuration()
                                    + ") не входит в заданный промежуток [" + generatorParameters.minOperationDuration
                                    + ", " + generatorParameters.maxOperationDuration + "]");
                            flag = false;
                        }

                        if (!(1 <= operation.getRequiredEquipment() &&
                                operation.getRequiredEquipment() <= equipmentGroupsCount)) {
                            System.out.println("Требуемая группа оборудования (" + operation.getRequiredEquipment()
                                    + ") не входит в число имеющихся групп [" + 1
                                    + ", " + equipmentGroupsCount + "]");
                            flag = false;
                        }
                    }
                }
            }
        }

        return flag;
    }
}
