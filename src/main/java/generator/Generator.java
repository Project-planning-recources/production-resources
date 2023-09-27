package generator;

import parse.input.order.InputOrder;
import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import util.Random.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
                generatorParameters.startWorkingTime, generatorParameters.endWorkingTime, generatorParameters.minEquipmentGroupCount, generatorParameters.maxEquipmentGroupCount,
                generatorParameters.minEquipmentCount, generatorParameters.maxEquipmentCount);

        InputOrderInformation inputOrderInformation = Generator.generateInputOrderInformation(inputProduction, generatorParameters.ordersCount, generatorParameters.minOrderStartTime, generatorParameters.maxOrderStartTime,
                generatorParameters.minDurationTimeInDays, generatorParameters.maxDurationTimeInDays, generatorParameters.minDetailsTypeCount, generatorParameters.maxDetailsTypeCount, generatorParameters.minDetailsCount, generatorParameters.maxDetailsCount,
                generatorParameters.minTechProcessCount, generatorParameters.maxTechProcessCount, generatorParameters.minOperationsCount, generatorParameters.maxOperationsCount, generatorParameters.minOperationDuration, generatorParameters.maxOperationDuration);

        return new GeneratedData(inputProduction, inputOrderInformation);
    }

    private static InputProduction generateInputProduction(int daysForSchedule,
                                                           int startWorkingTime,
                                                           int endWorkingTime,
                                                           int minEquipmentGroupCount,
                                                           int maxEquipmentGroupCount,
                                                           int minEquipmentCount,
                                                           int maxEquipmentCount) {
        return null;
    }

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
        return null;
    }

}
