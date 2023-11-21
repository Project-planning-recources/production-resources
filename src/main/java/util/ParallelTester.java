package util;

import algorithm.AlternativenessOwnAlgorithm;
import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import testing.PossibilityTester;
import testing.RealityTester;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class ParallelTester {
    public static void main(String[] args) {

        int threadsNum = 4;
        int startGen = 10;
        int budgetGen = 100;
        int startsAlg = 10;
        int basisSize = 5;

        try (FileWriter writer = new FileWriter(argv[6], false)) {
            writer.write("№;Количество заказов;Количество типов деталей;Среднее количество деталей каждого типа;Среднее количество операций на деталь;Количество атомарных ресурсов;Минимальное число альтернатив на деталь;" +
                    "Максимальное число альтернатив на деталь;Среднее число альтернатив на деталь;Количество произведенных операций;Среднее суммарное количество дней просрочки;Средний критерий;Среднее время исполнения в секундах\n");

            for (int i = 0; i < basisSize; i++) {
                InputProduction production = READER.readProductionFile(argv[2] + "/" + (i + 1) + "_production.xml");
                InputOrderInformation orders = READER.readOrderFile(argv[2] + "/" + (i + 1) + "_orders.xml");

                if (PossibilityTester.test(production, orders)) {
//                            BaseAlgorithm baseAlgorithm = new BaseAlgorithm(production, orders.getOrders(), null);
//                            OutputResult baseResult = baseAlgorithm.start();

                    Data.AlternativenessCount alternativenessCount = Data.getAlternativenessCount(orders.getOrders());
                    long equipmentCount = Data.getEquipmentCount(production);

                    long performOperationsCount = 0;
                    long averageOverdueDays = 0;
                    double averageCriterion = 0;
                    long averageTime = 0;

                    for (int j = 0; j < startsAlg; j++) {

                        System.out.println(i + ":" + j + ": Запущен...");

                        long startTime = System.currentTimeMillis();
                        AlternativenessOwnAlgorithm ownAlgorithm = new AlternativenessOwnAlgorithm(production, orders.getOrders(), null, startGen, budgetGen);
                        OutputResult ownResult = ownAlgorithm.start();

                        if (RealityTester.test(production, orders, ownResult)) {
                            long endTime = System.currentTimeMillis();

                            performOperationsCount += Data.getPerformOperationsCount(ownResult);
                            averageOverdueDays += Data.getAverageOverdueDays(orders.getOrders(), ownResult);
                            averageCriterion += Criterion.getCriterion(orders, ownResult);
                            averageTime += (endTime - startTime) / 1000;

                            System.out.println(i + ":" + j + ": Завершён...");
                        } else {
                            throw new Exception(i + ":" + j + ": Результат собственного алгоритма не соответствует заказам");
                        }
                    }

                    writer.write((i + 1) + ";" +
                            orders.getOrders().size() + ";" +
                            Data.getDetailTypesCount(orders.getOrders()) + ";" +
                            Data.getAverageDetailsCount(orders.getOrders()) + ";" +
                            Data.getAverageOperationsCountOnDetail(orders.getOrders()) + ";" +
                            equipmentCount + ";" +
                            alternativenessCount.min + ";" +
                            alternativenessCount.max + ";" +
                            alternativenessCount.average + ";" +
                            ((double) performOperationsCount / startsAlg) + ";" +
                            ((double) averageOverdueDays / startsAlg) + ";" +
                            (averageCriterion / startsAlg) + ";" +
                            ((double) averageTime / startsAlg) + "\n");
                } else {
                    throw new Exception(i + ": Заказы не соответствуют производству");
                }
            }
            System.out.println("Работа завершена.");
        }
    }
}
