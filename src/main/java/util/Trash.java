package util;


import algorithm.Algorithm;
import algorithm.AlphaAlgorithm;
import algorithm.BaseAlgorithm;
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

        int dataCount = 1;

//        checkGenerator();

//        generate(dataCount);
        checkOwnAlgorithm();

        System.out.println("=====FINISH=====");

    }

    public static ArrayList<GeneratedData> generate(int dataCount) {
        ArrayList<GeneratedData> generatedData = Generator.generateData(dataCount, GENERATOR_PARAMETERS);
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

        InputProduction production = READER.readProductionFile("production.xml");
        InputOrderInformation orderFile = READER.readOrderFile("orders.xml");

        Algorithm algorithm = new AlphaAlgorithm(production, orderFile.getOrders(), null, 10, 50);

        OutputResult result = null;
        try {
            result = algorithm.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Testing...");

        if(RealityTester.test(production, orderFile, result)) {
            WRITER.writeResultFile("result.xml", result);
            System.out.println("Creterion: " + Criterion.getCriterion(orderFile, result));
            System.out.println("Overdue: " + Data.getAverageOverdueDays(orderFile.getOrders(), result));
            System.out.println("Done!");
        } else {
            System.out.println("Bad2!");
        }
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

                Algorithm base = new BaseAlgorithm(generatedData1.getInputProduction(), generatedData1.getInputOrderInformation().getOrders(), LocalDateTime.of(2023, 10, 20, 0, 0));
                OutputResult result = null;
                try {
                    result = base.start();
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
