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


//        GeneratorParameters generatorParameters = new GeneratorParameters();
//        generatorParameters.daysForSchedule = 5;
//        generatorParameters.startWorkingTime = 9;
//        generatorParameters.endWorkingTime = 17;
//        generatorParameters.ordersCount = 1;
//        generatorParameters.minEquipmentGroupCount = 2;
//        generatorParameters.maxEquipmentGroupCount = 3;
//        generatorParameters.minEquipmentCount = 1;
//        generatorParameters.maxEquipmentCount = 5;
//        generatorParameters.minOrderStartTime = LocalDateTime.of(2023, 9, 1, 0, 0);
//        generatorParameters.maxOrderStartTime = LocalDateTime.of(2023, 11, 1, 0, 0);
//        generatorParameters.minDurationTimeInDays = 10;
//        generatorParameters.maxDurationTimeInDays = 30;
//        generatorParameters.minDetailsTypeCount = 1;
//        generatorParameters.maxDetailsTypeCount = 2;
//        generatorParameters.minDetailsCount = 5;
//        generatorParameters.maxDetailsCount = 100;
//        generatorParameters.minTechProcessCount = 1;
//        generatorParameters.maxTechProcessCount = 2;
//        generatorParameters.minOperationsCount = 1;
//        generatorParameters.maxOperationsCount = 10;
//        generatorParameters.minOperationDuration = 60;
//        generatorParameters.maxOperationDuration = 3600;

        GeneratorParameters generatorParameters = GeneratorJsonReader.readGeneratorParameters("generatorParameters.json");
        System.out.println(generatorParameters);
        ArrayList<GeneratedData> generatedData = Generator.generateData(1, generatorParameters);

        if(GeneratorTester.test(generatorParameters, generatedData.get(0)) && PossibilityTester.test(generatedData.get(0).getInputProduction(), generatedData.get(0).getInputOrderInformation())) {
            System.out.println("Ура!");
        } else {
            System.out.println(":(");
        }

    }

}
