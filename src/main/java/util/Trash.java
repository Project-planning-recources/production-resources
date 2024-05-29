package util;


import algorithm.Algorithm;
import algorithm.NelderMeadAlgorithm.NelderMeadVariatorAlgorithm;
import algorithm.alpha.AlphaClusterVariatorAlgorithm;
import algorithm.alpha.AlphaClusterVariatorAlgorithmParallel;
import algorithm.alpha.AlphaVariatorAlgorithm;
import algorithm.alpha.AlphaVariatorAlgorithmParallel;
import algorithm.backpack.BackpackAlgorithm;
import algorithm.candidates.CandidatesBaseAlgorithm;
import generator.GeneratedData;
import generator.Generator;
import generator.GeneratorJsonReader;
import generator.GeneratorParameters;
import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import parse.input.XMLReader;
import parse.output.XMLWriter;
import testing.GeneratorTester;
import testing.PossibilityTester;
import testing.RealityTester;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Класс для любых тестов и проверок
 */
public class Trash {
    private final static XMLReader READER = new XMLReader();
    private final static XMLWriter WRITER = new XMLWriter();
    private final static GeneratorParameters GENERATOR_PARAMETERS;

    static {
        try {
            GENERATOR_PARAMETERS = GeneratorJsonReader.readGeneratorParameters("generatorParameters.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {

        System.out.println("=====START=====");

//        int dataCount = 1;

//        checkGenerator();

//        generate(1);
//        checkOwnAlgorithm();

//        testBackpack();
        //testOwnClusterAlgorithm();
        checkOwnAlgorithm();

        //testNelderMead();

//        testParallelAlgorithm();
        System.out.println("=====FINISH=====");

    }

    public static void testParallelAlgorithm() throws Exception {

        InputProduction production = READER.readProductionFile("Basis/5_production.xml");
        InputOrderInformation orderFile = READER.readOrderFile("Basis/5_orders.xml");

        Algorithm algorithm = new AlphaClusterVariatorAlgorithmParallel(production, orderFile.getOrders(), null, "candidates", 1, 10, 100, 2);

        OutputResult result = null;
        try {
            result = algorithm.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Testing...");

        if(RealityTester.test(production, orderFile, result)) {
            WRITER.writeResultFile("alphaClusterResult.xml", result);
            System.out.println("Creterion: " + Criterion.getCriterion(orderFile, result));
            System.out.println("Overdue: " + Data.getAverageOverdueDays(orderFile.getOrders(), result));
            System.out.println("Done!");
        } else {
            System.out.println("Bad2!");
        }
    }

    public static void testOwnClusterAlgorithm() throws Exception {

        InputProduction production = READER.readProductionFile("Basis/1_production.xml");
        InputOrderInformation orderFile = READER.readOrderFile("Basis/1_orders.xml");

        Algorithm algorithm = new AlphaClusterVariatorAlgorithm(production, orderFile.getOrders(), null, "candidates", 1, 10, 50);

        OutputResult result = null;
        try {
            result = algorithm.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Testing...");

        if(RealityTester.test(production, orderFile, result)) {
            WRITER.writeResultFile("alphaClusterResult.xml", result);
            System.out.println("Creterion: " + Criterion.getCriterion(orderFile, result));
            System.out.println("Overdue: " + Data.getAverageOverdueDays(orderFile.getOrders(), result));
            System.out.println("Done!");
        } else {
            System.out.println("Bad2!");
        }
    }


    public static void testNelderMead() {
        InputProduction production = READER.readProductionFile("Basis/1_production.xml");
        InputOrderInformation orderFile = READER.readOrderFile("Basis/1_orders.xml");
        double avCrit = 0;
        double avOverdue = 0;
        for(int i=0; i<3; i++) {

            Algorithm algorithm = new NelderMeadVariatorAlgorithm(production, orderFile.getOrders(), null, "record", 2, 30);

            OutputResult result = null;
            try {
                result = algorithm.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            System.out.println("Testing...");

            if (RealityTester.test(production, orderFile, result)) {
                WRITER.writeResultFile("Result.xml", result);
                double crit = Criterion.getCriterion(orderFile, result);
                double overdue = Data.getAverageOverdueDays(orderFile.getOrders(), result);
                System.out.println("Criterion: " + crit);
                System.out.println("Overdue: " + overdue);
                System.out.println("Done!");
                avCrit += crit;
                avOverdue += overdue;
            } else {
                System.out.println("Bad2!");
            }
        }
        avCrit /= 3;
        avOverdue /= 3;
        System.out.println("=======================");
        System.out.println("Av.Criterion: " +avCrit);
        System.out.println("Av.Overdue: " + avOverdue);
    }

    private static void testBackpack() {
        InputProduction production = READER.readProductionFile("Basis/7_production.xml");
        InputOrderInformation orderFile = READER.readOrderFile("Basis/7_orders.xml");
        Algorithm algorithm = new BackpackAlgorithm(production, orderFile.getOrders(), null, "candidates", 1, 10000, 5);

        OutputResult result = null;
        try {
            result = algorithm.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Testing...");

        if(RealityTester.test(production, orderFile, result)) {
            WRITER.writeResultFile("backpackResult.xml", result);
            System.out.println("Creterion: " + Criterion.getCriterion(orderFile, result));
            System.out.println("Overdue: " + Data.getAverageOverdueDays(orderFile.getOrders(), result));
            System.out.println("Done!");
        } else {
            System.out.println("Bad2!");
        }

    }


    public static ArrayList<GeneratedData> generate(int dataCount) {
        ArrayList<GeneratedData> generatedData = Generator.generateData(dataCount, GENERATOR_PARAMETERS);
        System.out.println(GENERATOR_PARAMETERS);
        generatedData.forEach(generatedData1 -> {
            if (GeneratorTester.test(GENERATOR_PARAMETERS, generatedData1) && PossibilityTester.test(generatedData1.getInputProduction(), generatedData1.getInputOrderInformation())) {
                WRITER.writeProductionFile("production.xml", generatedData1.getInputProduction());
                WRITER.writeOrderInformationFile("orders.xml", generatedData1.getInputOrderInformation());
                System.out.println("Good gen");
            } else {
                System.out.println("Bad gen");
            }
        });
        return generatedData;
    }

    public static void checkOwnAlgorithm() throws Exception {

        double avCrit = 0;
        double avOverdue = 0;
        for(int i=0; i<3; i++) {
            InputProduction production = READER.readProductionFile("Basis/6_production.xml");
            InputOrderInformation orderFile = READER.readOrderFile("Basis/6_orders.xml");

            Algorithm algorithm = new AlphaVariatorAlgorithm(production, orderFile.getOrders(), null, "candidates", 1, 10, 50);

            OutputResult result = null;
            try {
                result = algorithm.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            System.out.println("Testing...");

            if (RealityTester.test(production, orderFile, result)) {
                WRITER.writeResultFile("alphaResult.xml", result);
                double crit = Criterion.getCriterion(orderFile, result);
                double overdue = Data.getAverageOverdueDays(orderFile.getOrders(), result);
                System.out.println("Criterion: " + crit);
                System.out.println("Overdue: " + overdue);
                System.out.println("Done!");
                avCrit += crit;
                avOverdue += overdue;
            } else {
                System.out.println("Bad2!");
            }
        }
        avCrit /= 3;
        avOverdue /= 3;
        System.out.println("=======================");
        System.out.println("Av.Criterion: " +avCrit);
        System.out.println("Av.Overdue: " + avOverdue);
    }

    public static void checkGenerator() throws Exception {
        GeneratorParameters generatorParameters = GeneratorJsonReader.readGeneratorParameters("generatorParameters.json");
        ArrayList<GeneratedData> generatedData = Generator.generateData(1, generatorParameters);

        generatedData.forEach(generatedData1 -> {
            if (GeneratorTester.test(generatorParameters, generatedData1) && PossibilityTester.test(generatedData1.getInputProduction(), generatedData1.getInputOrderInformation())) {
                System.out.println("Ура!");
                XMLWriter writer = new XMLWriter();
                writer.writeProductionFile("production.xml", generatedData.get(0).getInputProduction());
                writer.writeOrderInformationFile("orders.xml", generatedData.get(0).getInputOrderInformation());

                Algorithm alg = new CandidatesBaseAlgorithm(generatedData1.getInputProduction(), generatedData1.getInputOrderInformation().getOrders(), LocalDateTime.of(2023, 10, 20, 0, 0));
                OutputResult result = null;
                try {
                    result = alg.start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                writer.writeResultFile("result.xml", result);
                if (RealityTester.test(generatedData1.getInputProduction(), generatedData1.getInputOrderInformation(), result)) {
                    System.out.println("Ура2!");

                    System.out.println("Критерий = " + Criterion.getCriterion(generatedData1.getInputOrderInformation(), result));
                } else {
                    System.out.println(":(2");
                }
            } else {
                System.out.println(":(");
            }
        });
    }


}
