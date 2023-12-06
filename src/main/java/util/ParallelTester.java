package util;

import algorithm.Algorithm;
import algorithm.AlphaVariatorAlgorithm;
import algorithm.parallel.ParallelAlphaVariatorAlgorithm1;
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

    public static DataFromCalculation calculation(int i, InputProduction production, InputOrderInformation orders, int startGen, int budgetGen, int threadsNum, int startsAlg) throws Exception {
        DataFromCalculation dataFromCalculation = new DataFromCalculation(0, 0, 0,0);
        for (int j = 0; j < startsAlg; j++) {

            System.out.println("Consistent: " + i + ":" + j + ": Запущен...");

            Algorithm algorithm = null;
            OutputResult result = null;
            long time = 0;

            if(threadsNum == 1) {
                long startTime = System.currentTimeMillis();
                algorithm = new AlphaVariatorAlgorithm(production, orders.getOrders(), null, startGen, budgetGen);
                result = algorithm.start();
                time = (System.currentTimeMillis() - startTime) / 1000;
            } else {
                long startTime = System.currentTimeMillis();
                algorithm = new ParallelAlphaVariatorAlgorithm1(production, orders.getOrders(), null, startGen, budgetGen, threadsNum);
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

        int threadsNum = 4;
        int startGen = 10;
        int budgetGen = 100;
        int startsAlg = 1;
        int basisSize = 5;

        try (FileWriter writer = new FileWriter("parallel.csv", false)) {
            writer.write("№;Количество заказов;Количество типов деталей;Среднее количество деталей каждого типа;Среднее количество операций на деталь;Количество атомарных ресурсов;Среднее число альтернатив на деталь;" +
                    "Среднее время исполнения в секундах последовательного;Среднее время исполнения в секундах параллельного1\n");

            for (int i = 0; i < basisSize; i++) {
                InputProduction production = READER.readProductionFile("Basis/" + (i + 1) + "_production.xml");
                InputOrderInformation orders = READER.readOrderFile( "Basis/" + (i + 1) + "_orders.xml");

                if (PossibilityTester.test(production, orders)) {


                    Data.AlternativenessCount alternativenessCount = Data.getAlternativenessCount(orders.getOrders());
                    long equipmentCount = Data.getEquipmentCount(production);

                    DataFromCalculation consistent = calculation(i, production, orders, startGen, budgetGen, 1, startsAlg);
                    DataFromCalculation parallel1 = calculation(i, production, orders, startGen, budgetGen, threadsNum, startsAlg);


                    writer.write((i + 1) + ";" +
                            orders.getOrders().size() + ";" +
                            Data.getDetailTypesCount(orders.getOrders()) + ";" +
                            Data.getAverageDetailsCount(orders.getOrders()) + ";" +
                            Data.getAverageOperationsCountOnDetail(orders.getOrders()) + ";" +
                            equipmentCount + ";" +
                            alternativenessCount.average + ";" +
                            ((double) consistent.averageTime / startsAlg) + ";" +
                            ((double) parallel1.averageTime / startsAlg) + "\n");

//                    writer.write((i + 1) + ";" +
//                            orders.getOrders().size() + ";" +
//                            Data.getDetailTypesCount(orders.getOrders()) + ";" +
//                            Data.getAverageDetailsCount(orders.getOrders()) + ";" +
//                            Data.getAverageOperationsCountOnDetail(orders.getOrders()) + ";" +
//                            equipmentCount + ";" +
//                            alternativenessCount.min + ";" +
//                            alternativenessCount.max + ";" +
//                            alternativenessCount.average + ";" +
//                            ((double) consistent.performOperationsCount / startsAlg) + ";" +
//                            ((double) consistent.averageOverdueDays / startsAlg) + ";" +
//                            (consistent.averageCriterion / startsAlg) + ";" +
//                            ((double) consistent.averageTime / startsAlg) + ";" +
//                            ((double) parallel1.performOperationsCount / startsAlg) + ";" +
//                            ((double) parallel1.averageOverdueDays / startsAlg) + ";" +
//                            (parallel1.averageCriterion / startsAlg) + ";" +
//                            ((double) parallel1.averageTime / startsAlg) + "\n");
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
