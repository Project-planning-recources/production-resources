package generator;

import algorithm.model.production.EquipmentGroup;
import parse.input.order.*;
import parse.input.production.*;
import util.Random.*;

import java.time.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {

    private Generator() {}
    /**
     * Генерирует данные для приложения
     *
     * @param dataCount - количество экземпляров сгенерированных данных
     * @param generatorParameters - параметры для генерирования
     * @return сгенерированные данные
     */
    public static ArrayList<GeneratedData> generateData(int dataCount, GeneratorParameters generatorParameters) {
        ArrayList<GeneratedData> generatedData = new ArrayList<>();

        for (int i = 0; i < dataCount; i++) {
            generatedData.add(Generator.generate(generatorParameters));
        }

        return generatedData;
    }

    private static GeneratedData generate(GeneratorParameters generatorParameters) {
        InputProduction inputProduction = Generator.generateInputProduction(generatorParameters.daysForSchedule,
                generatorParameters.startWorkingTime, generatorParameters.endWorkingTime, generatorParameters.minEquipmentGroupCount,
                generatorParameters.maxEquipmentGroupCount, generatorParameters.minEquipmentCount, generatorParameters.maxEquipmentCount);

        InputOrderInformation inputOrderInformation = Generator.generateInputOrderInformation(inputProduction, generatorParameters.ordersCount,
                LocalDateTime.ofEpochSecond(generatorParameters.minOrderStartTime.getTime()/1000, 0, ZoneOffset.ofHours(0)),
                LocalDateTime.ofEpochSecond(generatorParameters.maxOrderStartTime.getTime()/1000, 0, ZoneOffset.ofHours(0)),
                generatorParameters.minDurationTimeInDays, generatorParameters.maxDurationTimeInDays, generatorParameters.minDetailsTypeCount,
                generatorParameters.maxDetailsTypeCount, generatorParameters.minDetailsCount, generatorParameters.maxDetailsCount,
                generatorParameters.minTechProcessCount, generatorParameters.maxTechProcessCount, generatorParameters.minOperationsCount,
                generatorParameters.maxOperationsCount, generatorParameters.minOperationDuration, generatorParameters.maxOperationDuration);

        return new GeneratedData(inputProduction, inputOrderInformation);
    }

    /**
     * Генерация инфы о производстве
     */
    private static InputProduction generateInputProduction(int daysForSchedule,
                                                           int startWorkingTime,
                                                           int endWorkingTime,
                                                           int minEquipmentGroupCount,
                                                           int maxEquipmentGroupCount,
                                                           int minEquipmentCount,
                                                           int maxEquipmentCount) {
        InputProduction inputProduction = new InputProduction();

        InputSchedule inputSchedule = new InputSchedule();
        for (int i = 0; i < daysForSchedule; i++) {
            InputWorkingDay inputWorkingDay = new InputWorkingDay((short) (i + 1), LocalTime.of(startWorkingTime, 0),
                    LocalTime.of(endWorkingTime, 0), true);
            inputSchedule.getWeek().add(inputWorkingDay);
        }
        inputProduction.setSchedule(inputSchedule);

        int equipmentGroupCount = ThreadLocalRandom.current().nextInt(minEquipmentGroupCount, maxEquipmentGroupCount);
        List<InputEquipmentGroup> groups = new ArrayList<>();
        for(int i = 0; i < equipmentGroupCount; i++) {
            int equipmentCount = ThreadLocalRandom.current().nextInt(minEquipmentCount, maxEquipmentCount);
            List<InputEquipment> equipments = new ArrayList<>();
            for (int j = 0; j < equipmentCount; j++) {
                InputEquipment inputEquipment = new InputEquipment(j + 1, "Оборудование " + (i + 1) + "." + (j + 1));
                equipments.add(inputEquipment);
            }
            InputEquipmentGroup inputEquipmentGroup = new InputEquipmentGroup(i + 1, "Группа " + (i + 1), equipments);
            groups.add(inputEquipmentGroup);
        }

        inputProduction.setEquipmentGroups(groups);
        return inputProduction;
    }

    /**
     * Генерация инфы по заказам
     */
    private static InputOrderInformation generateInputOrderInformation(InputProduction inputProduction,
                                                                       int ordersCount,
                                                                       LocalDateTime minOrderStartTime,
                                                                       LocalDateTime maxOrderStartTime,
                                                                       int minDurationTimeInDays,
                                                                       int maxDurationTimeInDays,
                                                                       int minDetailsTypeCount,
                                                                       int maxDetailsTypeCount,
                                                                       int minDetailsCount,
                                                                       int maxDetailsCount,
                                                                       int minTechProcessCount,
                                                                       int maxTechProcessCount,
                                                                       int minOperationsCount,
                                                                       int maxOperationsCount,
                                                                       int minOperationDuration,
                                                                       int maxOperationDuration) {
        System.out.println(inputProduction);

        ArrayList<InputOrder> orders = new ArrayList<>();
        for (int i = 0; i < ordersCount; i++) {
            long minOrderStartTimeInSec = minOrderStartTime.atZone(ZoneId.of("+3")).toInstant().toEpochMilli();
            long maxOrderStartTimeInSec = maxOrderStartTime.atZone(ZoneId.of("+3")).toInstant().toEpochMilli();
            long orderStartSeconds = ThreadLocalRandom.current().nextLong(minOrderStartTimeInSec, maxOrderStartTimeInSec);
            LocalDateTime orderStartTime =  LocalDateTime.ofEpochSecond(orderStartSeconds/1000, 0, ZoneOffset.ofHours(0));
            int durationTimeInDays = ThreadLocalRandom.current().nextInt(minDurationTimeInDays, maxDurationTimeInDays);
            LocalDateTime deadline = orderStartTime.plusDays(durationTimeInDays);

            int detailsTypeCount = ThreadLocalRandom.current().nextInt(minDetailsTypeCount, maxDetailsTypeCount);
            ArrayList<InputProduct> products = new ArrayList<>();
            for (int j = 0; j < detailsTypeCount; j++) {
                int detailsCount = ThreadLocalRandom.current().nextInt(minDetailsCount, maxDetailsCount);
                int techProcessCount = ThreadLocalRandom.current().nextInt(minTechProcessCount, maxTechProcessCount);
                ArrayList<InputTechProcess> techProcesses = new ArrayList<>();
                for(int k = 0; k < techProcessCount; k++) {
                    int operationsCount = ThreadLocalRandom.current().nextInt(minOperationsCount, maxOperationsCount);
                    LinkedList<InputOperation> operations = new LinkedList<>();
                    for (int o = 0; o < operationsCount; o++) {
                        int operationDuration = ThreadLocalRandom.current().nextInt(minOperationDuration, maxOperationDuration);
                        int requiredGroup = ThreadLocalRandom.current().nextInt(1, inputProduction.getEquipmentGroups().size());
                        InputOperation inputOperation = new InputOperation(o + 1, "Операция " + (k + 1) + "." + (o + 1), operationDuration,
                                requiredGroup, 0, 0);

                        if (operations.size() != 0) {
                            inputOperation.setPrevOperationId(operations.getLast().getId());
                            operations.getLast().setNextOperationId(o + 1);
                        }
                        operations.add(inputOperation);
                    }

                    InputTechProcess inputTechProcess = new InputTechProcess(k + 1, operations);
                    techProcesses.add(inputTechProcess);
                }

                InputProduct inputProduct = new InputProduct(j + 1, "Деталь " + (i + 1) + "." + (j + 1), detailsCount, techProcesses);
                products.add(inputProduct);
            }

            InputOrder order = new InputOrder(i + 1, orderStartTime, deadline, products);
            orders.add(order);
        }

        return new InputOrderInformation(orders);
    }

}
