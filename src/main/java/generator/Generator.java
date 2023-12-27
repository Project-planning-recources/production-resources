package generator;

import parse.input.order.*;
import parse.input.production.*;
import util.Random;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
                generatorParameters.maxDurationStartTime,
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
        for (short i = 1; i <= daysForSchedule; i++) {
            InputWorkingDay inputWorkingDay = new InputWorkingDay(i, LocalTime.of(startWorkingTime, 0),
                    LocalTime.of(endWorkingTime, 0), true);
            inputSchedule.getWeek().add(inputWorkingDay);
        }
        for(short dayNumber = 1; dayNumber < 8; dayNumber++) {
            if(Objects.isNull(inputSchedule.getWorkDayByDayNumber(dayNumber))) {
                inputSchedule.getWeek().add(new InputWorkingDay(dayNumber, LocalTime.MIN, LocalTime.MIN, false));
            }
        }

        inputProduction.setSchedule(inputSchedule);

        long equipmentGroupCount = Random.randomInt(minEquipmentGroupCount, maxEquipmentGroupCount);
        List<InputEquipmentGroup> groups = new ArrayList<>();
        for(long i = 1; i <= equipmentGroupCount; i++) {
            long equipmentCount = Random.randomInt(minEquipmentCount, maxEquipmentCount);
            int lengthEquipmentCount = (int)(Math.log10(equipmentCount) + 1);

            List<InputEquipment> equipments = new ArrayList<>();
            for (long j = 1; j <= equipmentCount; j++) {
                long idEquipment = i * (long)Math.pow(10, lengthEquipmentCount) + j;
                InputEquipment inputEquipment = new InputEquipment(idEquipment, "Оборудование " + i + "." + idEquipment);
                equipments.add(inputEquipment);
            }
            InputEquipmentGroup inputEquipmentGroup = new InputEquipmentGroup(i, "Группа " + i, equipments);
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
                                                                       int maxDurationStartTime,
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
        ArrayList<InputOrder> orders = new ArrayList<>();
        for (int i = 1; i <= ordersCount; i++) {
            long orderStartSeconds =  Random.randomInt(maxDurationStartTime);
            LocalDateTime orderStartTime = minOrderStartTime.plusDays(orderStartSeconds);
            int durationTimeInDays = Random.randomInt(minDurationTimeInDays, maxDurationTimeInDays);
            LocalDateTime deadline = orderStartTime.plusDays(durationTimeInDays);

            long detailsTypeCount = Random.randomInt(minDetailsTypeCount, maxDetailsTypeCount);
            int lengthDetailsCount = (int)(Math.log10(detailsTypeCount) + 1);

            ArrayList<InputProduct> products = new ArrayList<>();
            InputOrder order = new InputOrder(i, orderStartTime, deadline, products);

            for (long j = 1; j <= detailsTypeCount; j++) {
                int detailsCount = Random.randomInt(minDetailsCount, maxDetailsCount);
                long techProcessCount;
                if(j == 1) {
                    techProcessCount = maxTechProcessCount;
                } else {
                    techProcessCount = Random.randomInt(minTechProcessCount, maxTechProcessCount);
                }
                int lengthTechProcessCount = (int)(Math.log10(techProcessCount) + 1);

                ArrayList<InputTechProcess> techProcesses = new ArrayList<>();
                long idProduct = i * (long)Math.pow(10, lengthDetailsCount) + j;
                InputProduct inputProduct = new InputProduct(idProduct, "Деталь " + i + "." + idProduct,
                        detailsCount, techProcesses);

                for(long k = 1; k <= techProcessCount; k++) {
                    long operationsCount = Random.randomInt(minOperationsCount, maxOperationsCount);
                    int lengthOperationsCount = (int)(Math.log10(operationsCount)+1);

                    LinkedList<InputOperation> operations = new LinkedList<>();
                    InputTechProcess inputTechProcess = new InputTechProcess(
                            inputProduct.getId() * (long)Math.pow(10, lengthTechProcessCount) + k, operations);

                    for (long o = 1; o <= operationsCount; o++) {
                        int operationDuration = Random.randomInt(minOperationDuration, maxOperationDuration);
                        int requiredGroup = Random.randomInt(1, inputProduction.getEquipmentGroups().size());

                        long idOperation = inputTechProcess.getId() * (long)Math.pow(10, lengthOperationsCount) + o;

                        InputOperation inputOperation = new InputOperation(idOperation,
                                "Операция " + inputTechProcess.getId() + "." + idOperation, operationDuration,
                                requiredGroup, 0, 0);

                        if (operations.size() != 0) {
                            inputOperation.setPrevOperationId(operations.getLast().getId());
                            operations.getLast().setNextOperationId(idOperation);
                        }
                        operations.add(inputOperation);
                    }
                    techProcesses.add(inputTechProcess);
                }
                products.add(inputProduct);
            }
            orders.add(order);
        }

        return new InputOrderInformation(orders);
    }

}
