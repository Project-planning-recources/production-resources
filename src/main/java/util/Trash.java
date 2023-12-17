package util;


import algorithm.Algorithm;
import algorithm.BaseAlgorithm;
import generator.GeneratedData;
import generator.Generator;
import generator.GeneratorJsonReader;
import generator.GeneratorParameters;
import parse.output.result.OutputResult;
import parse.output.XMLWriter;
import testing.GeneratorTester;
import testing.PossibilityTester;
import testing.RealityTester;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс для любых тестов и проверок
 */
public class Trash {

    public static void main(String[] args) throws Exception {

        int threadCount = 3;

        GeneratorParameters generatorParameters = GeneratorJsonReader.readGeneratorParameters("generatorParameters.json");
        ArrayList<GeneratedData> generatedData = Generator.generateData(100, generatorParameters);

        AtomicInteger count = new AtomicInteger(1);
        generatedData.forEach(generatedData1 -> {
            if(GeneratorTester.test(generatorParameters, generatedData1) && PossibilityTester.test(generatedData1.getInputProduction(), generatedData1.getInputOrderInformation())) {
                System.out.println(count + ": Ура!");
                XMLWriter writer = new XMLWriter();
                writer.writeProductionFile("production.xml", generatedData.get(0).getInputProduction());
                writer.writeOrderInformationFile("orders.xml", generatedData.get(0).getInputOrderInformation());

                Algorithm base = new BaseAlgorithm(generatedData1.getInputProduction(),
                        generatedData1.getInputOrderInformation().getOrders(),
                        LocalDateTime.of(2023, 10, 20, 0, 0), threadCount);
                OutputResult result = null;
                try {
                    result = base.start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                //writer.writeResultFile("result.xml", result);
                if(RealityTester.test(generatedData1.getInputProduction(), generatedData1.getInputOrderInformation(), result)) {
                    System.out.println(count + ": Ура2!");
                } else {
                    System.out.println(count + ": :(2");
                }
            } else {
                System.out.println(count + ": :(");
            }

            count.getAndIncrement();
        });

        /*XMLReader reader = new XMLReader();
        InputProduction inputProduction = reader.readProductionFile("production.xml");
        InputOrderInformation inputOrderInformation = reader.readOrderFile("orders.xml");
        for (int i = 0; i < 10; i++) {
            if (PossibilityTester.test(inputProduction, inputOrderInformation)) {
                System.out.println(i + ": Ура!");
                XMLWriter writer = new XMLWriter();
                Algorithm base = new BaseAlgorithm(inputProduction, inputOrderInformation.getOrders(),
                        LocalDateTime.of(2023, 10, 20, 0, 0), threadCount);

                OutputResult result = null;
                try {
                    result = base.start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                writer.writeResultFile("result.xml", result);
                if (RealityTester.test(inputProduction, inputOrderInformation, result)) {
                    System.out.println(i + ": Ура2!");
                } else {
                    System.out.println(i + ": :(2");
                }
            } else {
                System.out.println(i + ": :(");
            }
        }*/
    }
}
