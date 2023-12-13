package util;

import algorithm.Algorithm;
import algorithm.alpha.AlphaClusterVariatorAlgorithm;
import algorithm.alpha.AlphaClusterVariatorAlgorithmParallel;
import algorithm.alpha.AlphaVariatorAlgorithm;
import algorithm.alpha.AlphaVariatorAlgorithmParallel;
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

    static class DataFromCalculation {
        long performOperationsCount = 0;
        long averageOverdueDays = 0;
        double averageCriterion = 0;
        long averageTime = 0;

        public DataFromCalculation(long performOperationsCount, long averageOverdueDays, double averageCriterion, long averageTime) {
            this.performOperationsCount = performOperationsCount;
            this.averageOverdueDays = averageOverdueDays;
            this.averageCriterion = averageCriterion;
            this.averageTime = averageTime;
        }
    }

    public static DataFromCalculation calculation(int i, InputProduction production, InputOrderInformation orders, String frontAlgorithmType, int frontThreadsCount, int startGen, int budgetGen, int threadsCount, int startsAlg) throws Exception {
        DataFromCalculation dataFromCalculation = new DataFromCalculation(0, 0, 0,0);
        for (int j = 0; j < startsAlg; j++) {

            System.out.println("Consistent: " + i + ":" + j + ": Запущен...");

            Algorithm algorithm = null;
            OutputResult result = null;
            long time = 0;

            if(threadsCount == 1) {
                long startTime = System.currentTimeMillis();
                algorithm = new AlphaClusterVariatorAlgorithm(production, orders.getOrders(), null, frontAlgorithmType, frontThreadsCount, startGen, budgetGen);
                result = algorithm.start();
                time = (System.currentTimeMillis() - startTime) / 1000;
            } else {
                long startTime = System.currentTimeMillis();
                algorithm = new AlphaClusterVariatorAlgorithmParallel(production, orders.getOrders(), null, frontAlgorithmType, frontThreadsCount, startGen, budgetGen, threadsCount);
                result = algorithm.start();
                time = (System.currentTimeMillis() - startTime) / 1000;
            }

            if (result != null && RealityTester.test(production, orders, result)) {


                dataFromCalculation.performOperationsCount += Data.getPerformOperationsCount(result);
                dataFromCalculation.averageOverdueDays += Data.getAverageOverdueDays(orders.getOrders(), result);
                dataFromCalculation.averageCriterion += Criterion.getCriterion(orders, result);
                dataFromCalculation.averageTime += time;

                System.out.println(i + ":" + j + ": Завершён...");
            } else {
                throw new Exception(i + ":" + j + ": Результат алгоритма не соответствует заказам");
            }
        }
        return dataFromCalculation;
    }
    public static void main(String[] args) {

        int threadsCount = 4;
        int frontThreadsCount = 2;
        int startGen = 10;
        int budgetGen = 100;
        int startsAlg = 3;
        int basisSize = 5;

        try (FileWriter writer = new FileWriter("parallel.csv", false)) {
            writer.write("№;Количество заказов;Количество типов деталей;Среднее количество деталей каждого типа;Среднее количество операций на деталь;Количество атомарных ресурсов;Среднее число альтернатив на деталь;" +
                    "Время последовательного с кандидатами;Время параллельного с кандидатами;Время последовательного с рекордом;Время последовательного с параллельным рекордом;Время параллельного с последовательным рекордом;Время параллельного с параллельным рекордом\n");

            for (int i = 0; i < basisSize; i++) {
                InputProduction production = READER.readProductionFile("Basis/" + (i + 1) + "_production.xml");
                InputOrderInformation orders = READER.readOrderFile( "Basis/" + (i + 1) + "_orders.xml");

                if (PossibilityTester.test(production, orders)) {


                    AlternativenessCount alternativenessCount = Data.getAlternativenessCount(orders.getOrders());
                    long equipmentCount = Data.getEquipmentCount(production);

                    DataFromCalculation consistentVariatorConsistentCandidates = calculation(i, production, orders, "candidates", 1, startGen, budgetGen, 1, startsAlg);
                    DataFromCalculation parallelVariatorConsistentCandidates = calculation(i, production, orders, "candidates", 1, startGen, budgetGen, threadsCount, startsAlg);
//                    DataFromCalculation consistentVariatorConsistentRecords = calculation(i, production, orders, "record", 1, startGen, budgetGen, 1, startsAlg);
//                    DataFromCalculation consistentVariatorParallelRecords = calculation(i, production, orders, "record", frontThreadsCount, startGen, budgetGen, 1, startsAlg);
//                    DataFromCalculation parallelVariatorConsistentRecord = calculation(i, production, orders, "record", 1, startGen, budgetGen, threadsCount, startsAlg);
//                    DataFromCalculation parallelVariatorParallelRecord = calculation(i, production, orders, "record", frontThreadsCount, startGen, budgetGen, threadsCount, startsAlg);


                    writer.write((i + 1) + ";" +
                            orders.getOrders().size() + ";" +
                            Data.getDetailTypesCount(orders.getOrders()) + ";" +
                            Data.getAverageDetailsCount(orders.getOrders()) + ";" +
                            Data.getAverageOperationsCountOnDetail(orders.getOrders()) + ";" +
                            equipmentCount + ";" +
                            alternativenessCount.average + ";" +
                            ((double) consistentVariatorConsistentCandidates.averageTime / startsAlg) + ";" +
                            ((double) parallelVariatorConsistentCandidates.averageTime / startsAlg) + "\n");
//                            ((double) consistentVariatorConsistentRecords.averageTime / startsAlg) + ";" +
//                            ((double) consistentVariatorParallelRecords.averageTime / startsAlg) + ";" +
//                            ((double) parallelVariatorConsistentRecord.averageTime / startsAlg) + ";" +
//                            ((double) parallelVariatorParallelRecord.averageTime / startsAlg) + "\n");
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
