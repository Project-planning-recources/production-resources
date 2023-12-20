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


            System.out.println(i + ":" + j + " " + frontAlgorithmType + "-" + frontThreadsCount + ": Запущен...");

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

                System.out.println(i + ":" + j + " " + frontAlgorithmType + "-" + frontThreadsCount + ": Завершён...");
            } else {
                throw new Exception(i + ":" + j + " " + frontAlgorithmType + "-" + frontThreadsCount + ": Результат алгоритма не соответствует заказам");
            }
        }
        return dataFromCalculation;
    }

    public static void main(String[] args) {

        unionTests();

//        variationParallelTests();
//        frontParallelTests();
    }

    public static void variationParallelTests() {
        int startGen = 5;
        int budgetGen = 50;
        int startsAlg = 3;
        int basisSize = 8;
        int threadMax = 16;

        try (FileWriter writer = new FileWriter("variationParallel.csv", false)) {
            writer.write("№ задачи;Последовательный;2 потока;4 потока;8 потоков;16 потоков\n");

            for (int i = 0; i < basisSize; i++) {
                InputProduction production = READER.readProductionFile("Basis/" + (i + 1) + "_production.xml");
                InputOrderInformation orders = READER.readOrderFile( "Basis/" + (i + 1) + "_orders.xml");



                if (PossibilityTester.test(production, orders)) {
                    writer.write((i+1) + ";");
                    for (int j = 1; j <= threadMax; j *= 2) {
                        double time = 0;
                        for (int k = 0; k < startsAlg; k++) {

                            Algorithm algorithm = null;
                            OutputResult result = null;
                            if(j == 1) {
                                long startTime = System.currentTimeMillis();
                                algorithm = new AlphaClusterVariatorAlgorithm(production, orders.getOrders(), null, "candidates", 1, startGen, budgetGen);
                                result = algorithm.start();
                                time += (double)(System.currentTimeMillis() - startTime) / 1000;
                            } else {
                                long startTime = System.currentTimeMillis();
                                algorithm = new AlphaClusterVariatorAlgorithmParallel(production, orders.getOrders(), null, "candidates", 1, startGen, budgetGen, j);
                                result = algorithm.start();
                                time += (double) (System.currentTimeMillis() - startTime) / 1000;
                            }

                            if (result != null && RealityTester.test(production, orders, result)) {
                                System.out.println(i + ":" + j + "threads " + k + ": Завершён...");
                            } else {
                                throw new Exception(i + ":" + j + "threads " + k +  ": Результат алгоритма не соответствует заказам");
                            }

                        }
                        if(j == threadMax) {
                            writer.write((time/startsAlg) + "\n");
                        } else {
                            writer.write((time/startsAlg) + ";");
                        }
                    }


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

    public static void frontParallelTests() {
        int startGen = 5;
        int budgetGen = 100;
        int startsAlg = 100;
        int basisSize = 1;
        int threadMax = 2;

        try (FileWriter writer = new FileWriter("frontParallel.csv", false)) {
            writer.write("№ задачи;Последовательный;2 потока;4 потока;8 потоков;16 потоков\n");

            for (int i = 0; i < basisSize; i++) {
                InputProduction production = READER.readProductionFile("Basis/" + (i + 1) + "_production.xml");
                InputOrderInformation orders = READER.readOrderFile( "Basis/" + (i + 1) + "_orders.xml");

                if (PossibilityTester.test(production, orders)) {
                    writer.write((i+1) + ";");
                    for (int j = 2; j <= 2; j *= 2) {
                        double time = 0;
                        for (int k = 0; k < startsAlg; k++) {

                            Algorithm algorithm = null;
                            OutputResult result = null;
                            if(j == 1) {
                                long startTime = System.currentTimeMillis();
                                algorithm = new AlphaClusterVariatorAlgorithm(production, orders.getOrders(), null, "record", 1, startGen, budgetGen);
                                result = algorithm.start();
                                time += (double)(System.currentTimeMillis() - startTime) / 1000;
                            } else {
                                long startTime = System.currentTimeMillis();
                                algorithm = new AlphaClusterVariatorAlgorithmParallel(production, orders.getOrders(), null, "record", j, startGen, budgetGen, 2);
                                result = algorithm.start();
                                time += (double)(System.currentTimeMillis() - startTime) / 1000;
                            }

                            if (result != null && RealityTester.test(production, orders, result)) {
                                System.out.println(i + ":" + j + "threads:" + k + ": Завершён...");
                            } else {
                                System.out.println(i + ":" + j + "threads:" + k +  ": Результат алгоритма не соответствует заказам");
                                throw new Exception(i + ":" + j + "threads:" + k +  ": Результат алгоритма не соответствует заказам");
                            }
                        }
                        if(j == threadMax) {
                            writer.write((time/startsAlg) + "\n");
                        } else {
                            writer.write((time/startsAlg) + ";");
                        }
                    }


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

    public static void unionTests() {
        int threadsCount = 2;
        int frontThreadsCount = 2;
        int startGen = 10;
        int budgetGen = 50;
        int startsAlg = 100;

        int basisStart = 0;
        int basisSize = 1;

        try (FileWriter writer = new FileWriter("parallel.csv", false)) {
            writer.write("№;Количество заказов;Количество типов деталей;Среднее количество деталей каждого типа;Среднее количество операций на деталь;Количество атомарных ресурсов;Среднее число альтернатив на деталь;" +
                    "Время последовательного с кандидатами;Время параллельного с кандидатами;Время последовательного с рекордом;Время последовательного с параллельным рекордом;Время параллельного с последовательным рекордом;Время параллельного с параллельным рекордом\n");

            for (int i = basisStart; i < basisSize; i++) {
                InputProduction production = READER.readProductionFile("Basis/" + (i + 1) + "_production.xml");
                InputOrderInformation orders = READER.readOrderFile( "Basis/" + (i + 1) + "_orders.xml");

                if (PossibilityTester.test(production, orders)) {


                    AlternativenessCount alternativenessCount = Data.getAlternativenessCount(orders.getOrders());
                    long equipmentCount = Data.getEquipmentCount(production);

//                    DataFromCalculation consistentVariatorConsistentCandidates = calculation(i, production, orders, "candidates", 1, startGen, budgetGen, 1, startsAlg);
//                    DataFromCalculation parallelVariatorConsistentCandidates = calculation(i, production, orders, "candidates", 1, startGen, budgetGen, threadsCount, startsAlg);
                    DataFromCalculation consistentVariatorConsistentRecords = calculation(i, production, orders, "record", 1, startGen, budgetGen, 1, startsAlg);
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
//                            ((double) consistentVariatorConsistentCandidates.averageTime / startsAlg) + ";" +
//                            ((double) parallelVariatorConsistentCandidates.averageTime / startsAlg) + ";" +
                            ";;" + ((double) consistentVariatorConsistentRecords.averageTime / startsAlg) + ";" + ";;\n");
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
