package util;


import algorithm.Algorithm;
import algorithm.AlgorithmFactory;
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
import testing.ComparisonTester;
import testing.GeneratorTester;
import testing.PossibilityTester;
import testing.RealityTester;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

/**
 * Класс для любых тестов и проверок
 */
public class Trash {

    public static void main(String[] args) throws Exception {


        GeneratorParameters generatorParameters = GeneratorJsonReader.readGeneratorParameters("generatorParameters.json");
        ArrayList<GeneratedData> generatedData = Generator.generateData(1, generatorParameters);

        if(GeneratorTester.test(generatorParameters, generatedData.get(0)) && PossibilityTester.test(generatedData.get(0).getInputProduction(), generatedData.get(0).getInputOrderInformation())) {
            System.out.println("Ура!");
            XMLWriter writer = new XMLWriter();
            writer.writeProductionFile("production.xml", generatedData.get(0).getInputProduction());
            writer.writeOrderInformationFile("orders.xml", generatedData.get(0).getInputOrderInformation());

            Algorithm base = new BaseAlgorithm(generatedData.get(0).getInputProduction(), generatedData.get(0).getInputOrderInformation().getOrders(), LocalDateTime.of(2023, 10, 20, 0, 0));
            System.out.println("GENERATOR" + generatedData.get(0).getInputProduction());
            System.out.println("GENERATOR" + generatedData.get(0).getInputOrderInformation());
            OutputResult result = base.start();
            writer.writeResultFile("result.xml", result);
            System.out.println("RESULT" + result);
            if(RealityTester.test(generatedData.get(0).getInputProduction(), generatedData.get(0).getInputOrderInformation(), result)) {
                System.out.println("Ура2!");
            } else {
                System.out.println(":(2");
            }
        } else {
            System.out.println(":(");
        }

    }

}
