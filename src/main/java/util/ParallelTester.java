package util;

import algorithm.AlphaAlgorithm;
import parse.input.XMLReader;
import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import parse.output.XMLWriter;
import parse.output.result.OutputResult;
import testing.PossibilityTester;
import testing.RealityTester;

import java.io.FileWriter;
import java.io.IOException;

public class ParallelTester {

    private static final XMLReader READER = new XMLReader();
    private static final XMLWriter WRITER = new XMLWriter();
    public static void main(String[] args) {

        int threadsNum = 4;
        int startGen = 10;
        int budgetGen = 100;
        int startsAlg = 1;
        int basisSize = 5;

        try (FileWriter writer = new FileWriter("parallel.csv", false)) {
            writer.write("№;Количество заказов;Количество типов деталей;Среднее количество деталей каждого типа;Среднее количество операций на деталь;Количество атомарных ресурсов;Минимальное число альтернатив на деталь;" +
                    "Максимальное число альтернатив на деталь;Среднее число альтернатив на деталь;Количество произведенных операций;Среднее суммарное количество дней просрочки;Средний критерий;Среднее время исполнения в секундах\n");

            for (int i = 0; i < basisSize; i++) {
                InputProduction production = READER.readProductionFile("ParallelBasis/" + (i + 1) + "_production.xml");
                InputOrderInformation orders = READER.readOrderFile( "ParallelBasis/" + (i + 1) + "_orders.xml");

                if (PossibilityTester.test(production, orders)) {


                    Data.AlternativenessCount alternativenessCount = Data.getAlternativenessCount(orders.getOrders());
                    long equipmentCount = Data.getEquipmentCount(production);

                    long performOperationsCount = 0;
                    long averageOverdueDays = 0;
                    double averageCriterion = 0;
                    long averageTime = 0;

                    for (int j = 0; j < startsAlg; j++) {

                        System.out.println(i + ":" + j + ": Запущен...");

                        long startTime = System.currentTimeMillis();
                        AlphaAlgorithm ownAlgorithm = new AlphaAlgorithm(production, orders.getOrders(), null, startGen, budgetGen);
                        OutputResult ownResult = ownAlgorithm.start();
                        long endTime = System.currentTimeMillis();

                        if (RealityTester.test(production, orders, ownResult)) {


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
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
