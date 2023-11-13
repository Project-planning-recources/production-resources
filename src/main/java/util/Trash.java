package util;


import algorithm.Algorithm;
import algorithm.AlternativenessOwnAlgorithm;
import algorithm.BaseAlgorithm;
import generator.GeneratedData;
import generator.Generator;
import generator.GeneratorJsonReader;
import generator.GeneratorParameters;
import parse.output.result.OutputResult;
import parse.input.XMLReader;
import parse.output.XMLWriter;
import testing.GeneratorTester;
import testing.PossibilityTester;
import testing.RealityTester;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
//        checkGenerator();
        checkOwnAlgorithm();



        System.out.println("=====FINISH=====");

    }

    public static void checkOwnAlgorithm() throws Exception {

        for (int i = 0; i < 1; i++) {
            ArrayList<GeneratedData> generatedData = Generator.generateData(1, GENERATOR_PARAMETERS);
            generatedData.forEach(generatedData1 -> {

                if (GeneratorTester.test(GENERATOR_PARAMETERS, generatedData1) && PossibilityTester.test(generatedData1.getInputProduction(), generatedData1.getInputOrderInformation())) {
                    WRITER.writeProductionFile("production.xml", generatedData1.getInputProduction());
                    WRITER.writeOrderInformationFile("orders.xml", generatedData1.getInputOrderInformation());


//                    WRITER.writeProductionFile("testprod.xml",READER.readProductionFile("production.xml"));
//                    WRITER.writeOrderInformationFile("testorder.xml", READER.readOrderFile("orders.xml"));


                    Algorithm algorithm = new AlternativenessOwnAlgorithm(generatedData1.getInputProduction(), generatedData1.getInputOrderInformation().getOrders(), null, 10, 50);
//                    Algorithm algorithm = new BaseAlgorithm(generatedData1.getInputProduction(), generatedData1.getInputOrderInformation().getOrders(), null);

                    OutputResult result = null;
                    try {
                        result = algorithm.start();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
//                    System.out.println(result);
                    System.out.println(RealityTester.test(generatedData1.getInputProduction(), generatedData1.getInputOrderInformation(), result));
                    WRITER.writeResultFile("result.xml", result);



//                    WRITER.writeResultFile("testresult.xml", READER.readResultFile("result.xml"));
                    System.out.println("Ура!");

                } else {
                    System.out.println(":(");
                }
            });
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
