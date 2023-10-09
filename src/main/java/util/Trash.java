package util;


import algorithm.Algorithm;
import algorithm.AlgorithmFactory;
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
        System.out.println(generatorParameters);
        ArrayList<GeneratedData> generatedData = Generator.generateData(1, generatorParameters);

        if(GeneratorTester.test(generatorParameters, generatedData.get(0)) && PossibilityTester.test(generatedData.get(0).getInputProduction(), generatedData.get(0).getInputOrderInformation())) {
            System.out.println("Ура!");
            XMLWriter writer = new XMLWriter();
            writer.writeProductionFile("production.xml", generatedData.get(0).getInputProduction());
            writer.writeOrderInformationFile("orders.xml", generatedData.get(0).getInputOrderInformation());
        } else {
            System.out.println(":(");
        }

    }

}
