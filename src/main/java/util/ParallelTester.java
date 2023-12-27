package util;

import algorithm.Algorithm;
import algorithm.FrontAlgorithmFactory;
import algorithm.alpha.AlphaClusterVariatorAlgorithm;
import algorithm.alpha.AlphaClusterVariatorAlgorithmParallel;
import algorithm.alpha.AlphaVariatorAlgorithm;
import algorithm.alpha.AlphaVariatorAlgorithmParallel;
import algorithm.model.order.Order;
import algorithm.model.production.Production;
import parse.input.XMLReader;
import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import parse.output.XMLWriter;
import parse.output.result.OutputResult;
import testing.PossibilityTester;
import testing.RealityTester;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
        DataFromCalculation dataFromCalculation = new DataFromCalculation(0, 0, 0, 0);
        for (int j = 0; j < startsAlg; j++) {


            System.out.println(i + ":" + j + " " + frontAlgorithmType + "-" + frontThreadsCount + ": Запущен...");

            Algorithm algorithm = null;
            OutputResult result = null;
            long time = 0;

            if (threadsCount == 1) {
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

    public static void main(String[] args) throws Exception {

//        unionTests();
//        unionTestsDifferentFiles();

//        variationParallelTests();
        frontParallelTests();
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
                InputOrderInformation orders = READER.readOrderFile("Basis/" + (i + 1) + "_orders.xml");


                if (PossibilityTester.test(production, orders)) {
                    writer.write((i + 1) + ";");
                    for (int j = 1; j <= 1; j *= 2) {
                        double time = 0;
                        for (int k = 0; k < startsAlg; k++) {

                            Algorithm algorithm = null;
                            OutputResult result = null;
                            if (j == 1) {
                                long startTime = System.currentTimeMillis();
                                algorithm = new AlphaClusterVariatorAlgorithm(production, orders.getOrders(), null, "candidates", 1, startGen, budgetGen);
                                result = algorithm.start();
                                time += (double) (System.currentTimeMillis() - startTime) / 1000;
                            } else {
                                long startTime = System.currentTimeMillis();
                                algorithm = new AlphaClusterVariatorAlgorithmParallel(production, orders.getOrders(), null, "candidates", 1, startGen, budgetGen, j);
                                result = algorithm.start();
                                time += (double) (System.currentTimeMillis() - startTime) / 1000;
                            }

                            if (result != null && RealityTester.test(production, orders, result)) {
                                System.out.println(i + ":" + j + "threads " + k + ": Завершён...");
                            } else {
                                throw new Exception(i + ":" + j + "threads " + k + ": Результат алгоритма не соответствует заказам");
                            }

                        }
                        if (j == threadMax) {
                            writer.write((time / startsAlg) + "\n");
                        } else {
                            writer.write((time / startsAlg) + ";");
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
//        int startGen = 10;
//        int budgetGen = 20;
        int startsAlg = 3;
        int basisSize = 7;
        int threadMax = 4;

        try (FileWriter writer = new FileWriter("frontParallel.csv", false)) {
            writer.write("№ задачи;Последовательный;2 потока;4 потока;8 потоков;16 потоков\n");

            for (int i = 0; i < basisSize; i++) {
                InputProduction production = READER.readProductionFile("Basis/" + (i + 1) + "_production.xml");
                InputOrderInformation inputOrderInformation = READER.readOrderFile("Basis/" + (i + 1) + "_orders.xml");


                if (PossibilityTester.test(production, inputOrderInformation)) {
                    writer.write((i + 1) + ";");
                    for (int j = 1; j <= threadMax; j *= 2) {
                        double time = 0;
                        for (int k = 0; k < startsAlg; k++) {
                            System.out.println(i + ":" + j + "threads:" + k + ": Запущен...");
                            ArrayList<Order> orders = new ArrayList<>();
                            inputOrderInformation.getOrders().forEach(inputOrder -> {
                                orders.add(new Order(inputOrder));
                            });
                            long startTime = System.currentTimeMillis();
                            Algorithm algorithm = FrontAlgorithmFactory.getBaseFrontAlgorithm(new Production(production), orders, null, "record", j);;
                            OutputResult result = algorithm.start();
                            time += (double) (System.currentTimeMillis() - startTime) / 1000;

                            if (result != null && RealityTester.test(production, inputOrderInformation, result)) {
                                System.out.println(i + ":" + j + "threads:" + k + ": Завершён...");
                            } else {
                                System.out.println(i + ":" + j + "threads:" + k + ": Результат алгоритма не соответствует заказам");
                            }
                        }
                        if (j == threadMax) {
                            writer.write((time / startsAlg) + "\n");
                        } else {
                            writer.write((time / startsAlg) + ";");
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
        int startsAlg = 3;

        int basisStart = 0;
        int basisSize = 7;

        try (FileWriter writer = new FileWriter("parallelWithoutRecord.csv", false)) {
            writer.write("№;Количество заказов;Количество типов деталей;Среднее количество деталей каждого типа;Среднее количество операций на деталь;Количество атомарных ресурсов;Среднее число альтернатив на деталь;" +
                    "Время последовательного с кандидатами;Время параллельного с кандидатами;Время последовательного с рекордом;Время последовательного с параллельным рекордом;Время параллельного с последовательным рекордом;Время параллельного с параллельным рекордом\n");

            for (int i = basisStart; i < basisSize; i++) {
                InputProduction production = READER.readProductionFile("Basis/" + (i + 1) + "_production.xml");
                InputOrderInformation orders = READER.readOrderFile("Basis/" + (i + 1) + "_orders.xml");

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
                            ((double) parallelVariatorConsistentCandidates.averageTime / startsAlg) + ";" + ";;;\n");
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

    public static void unionTestsDifferentFiles() throws Exception {
        int startGen = 10;
        int budgetGen = 50;
        int startsAlg = 3;
        int basisStart = 5;
        int basisSize = 7;



        for (int i = basisStart; i < basisSize; i++) {
            InputProduction production = READER.readProductionFile("Basis/" + (i + 1) + "_production.xml");
            InputOrderInformation orders = READER.readOrderFile("Basis/" + (i + 1) + "_orders.xml");

            if (PossibilityTester.test(production, orders)) {


                AlternativenessCount alternativenessCount = Data.getAlternativenessCount(orders.getOrders());
                long equipmentCount = Data.getEquipmentCount(production);

                if(i != 5) {
                    DataFromCalculation consistentVariatorConsistentRecords = calculation(i, production, orders, "record", 1, startGen, budgetGen, 1, startsAlg);
                    try (FileWriter writer = new FileWriter((i+1) + "consistentVariatorConsistentRecords.csv", false)) {
                        writer.write("№;Количество заказов;Количество типов деталей;Среднее количество деталей каждого типа;Среднее количество операций на деталь;Количество атомарных ресурсов;Среднее число альтернатив на деталь;" +
                                "Время последовательного с рекордом\n");
                        writer.write((i + 1) + ";" +
                                orders.getOrders().size() + ";" +
                                Data.getDetailTypesCount(orders.getOrders()) + ";" +
                                Data.getAverageDetailsCount(orders.getOrders()) + ";" +
                                Data.getAverageOperationsCountOnDetail(orders.getOrders()) + ";" +
                                equipmentCount + ";" +
                                alternativenessCount.average + ";" +
                                ((double) consistentVariatorConsistentRecords.averageTime / startsAlg) + "\n");
                    }

                    DataFromCalculation consistentVariatorParallelRecords = calculation(i, production, orders, "record", 2, startGen, budgetGen, 1, startsAlg);
                    try (FileWriter writer = new FileWriter((i+1) + "consistentVariatorParallelRecords.csv", false)) {
                        writer.write("№;Количество заказов;Количество типов деталей;Среднее количество деталей каждого типа;Среднее количество операций на деталь;Количество атомарных ресурсов;Среднее число альтернатив на деталь;" +
                                "Время последовательного с параллельным рекордом\n");
                        writer.write((i + 1) + ";" +
                                orders.getOrders().size() + ";" +
                                Data.getDetailTypesCount(orders.getOrders()) + ";" +
                                Data.getAverageDetailsCount(orders.getOrders()) + ";" +
                                Data.getAverageOperationsCountOnDetail(orders.getOrders()) + ";" +
                                equipmentCount + ";" +
                                alternativenessCount.average + ";" +
                                ((double) consistentVariatorParallelRecords.averageTime / startsAlg) + "\n");
                    }
                }


                DataFromCalculation parallelVariatorConsistentRecord = calculation(i, production, orders, "record", 1, startGen, budgetGen, 2, startsAlg);
                try (FileWriter writer = new FileWriter((i+1) + "parallelVariatorConsistentRecords.csv", false)) {
                    writer.write("№;Количество заказов;Количество типов деталей;Среднее количество деталей каждого типа;Среднее количество операций на деталь;Количество атомарных ресурсов;Среднее число альтернатив на деталь;" +
                            "Время параллельного с последовательным рекордом\n");
                    writer.write((i + 1) + ";" +
                            orders.getOrders().size() + ";" +
                            Data.getDetailTypesCount(orders.getOrders()) + ";" +
                            Data.getAverageDetailsCount(orders.getOrders()) + ";" +
                            Data.getAverageOperationsCountOnDetail(orders.getOrders()) + ";" +
                            equipmentCount + ";" +
                            alternativenessCount.average + ";" +
                            ((double) parallelVariatorConsistentRecord.averageTime / startsAlg) + "\n");
                }

                DataFromCalculation parallelVariatorParallelRecord = calculation(i, production, orders, "record", 2, startGen, budgetGen, 2, startsAlg);
                try (FileWriter writer = new FileWriter((i+1) + "parallelVariatorParallelRecords.csv", false)) {
                    writer.write("№;Количество заказов;Количество типов деталей;Среднее количество деталей каждого типа;Среднее количество операций на деталь;Количество атомарных ресурсов;Среднее число альтернатив на деталь;" +
                            "Время параллельного с параллельным рекордом\n");
                    writer.write((i + 1) + ";" +
                            orders.getOrders().size() + ";" +
                            Data.getDetailTypesCount(orders.getOrders()) + ";" +
                            Data.getAverageDetailsCount(orders.getOrders()) + ";" +
                            Data.getAverageOperationsCountOnDetail(orders.getOrders()) + ";" +
                            equipmentCount + ";" +
                            alternativenessCount.average + ";" +
                            ((double) parallelVariatorParallelRecord.averageTime / startsAlg) + "\n");
                }
            } else {
                throw new Exception(i + ": Заказы не соответствуют производству");
            }
        }
        System.out.println("Работа завершена.");

    }
}
