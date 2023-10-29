package util;


import algorithm.Algorithm;
import algorithm.AlgorithmFactory;
import algorithm.BaseAlgorithm;
import algorithm.model.result.OperationResult;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Класс для любых тестов и проверок
 */
public class Trash {

    public static void main(String[] args) throws Exception {


        /*GeneratorParameters generatorParameters = GeneratorJsonReader.readGeneratorParameters("generatorParameters.json");
        ArrayList<GeneratedData> generatedData = Generator.generateData(100, generatorParameters);

        generatedData.forEach(generatedData1 -> {
            if(GeneratorTester.test(generatorParameters, generatedData1) && PossibilityTester.test(generatedData1.getInputProduction(), generatedData1.getInputOrderInformation())) {
                System.out.println("Ура!");
                XMLWriter writer = new XMLWriter();
//            writer.writeProductionFile("production.xml", generatedData.get(0).getInputProduction());
//            writer.writeOrderInformationFile("orders.xml", generatedData.get(0).getInputOrderInformation());

                Algorithm base = new BaseAlgorithm(generatedData1.getInputProduction(), generatedData1.getInputOrderInformation().getOrders(), LocalDateTime.of(2023, 10, 20, 0, 0));
//            System.out.println("GENERATOR" + generatedData.get(0).getInputProduction());
//            System.out.println("GENERATOR" + generatedData.get(0).getInputOrderInformation());
                OutputResult result = null;
                try {
                    result = base.start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
//            writer.writeResultFile("result.xml", result);
//            System.out.println("RESULT" + result);
                if(RealityTester.test(generatedData1.getInputProduction(), generatedData1.getInputOrderInformation(), result)) {
                    System.out.println("Ура2!");
                } else {
                    System.out.println(":(2");
                }
            } else {
                System.out.println(":(");
            }
        });*/

        List<OperationResult> performedOperations = new ArrayList<>();
        OperationResult o1 = new OperationResult();
        o1.setEndTime(LocalDateTime.now().minusYears(1));
        performedOperations.add(o1);
        OperationResult o2 = new OperationResult();
        o2.setEndTime(LocalDateTime.now().plusYears(1));
        performedOperations.add(o2);
        OperationResult o3 = new OperationResult();
        o3.setEndTime(LocalDateTime.now().plusYears(2));
        performedOperations.add(o3);
        OperationResult o4 = new OperationResult();
        performedOperations.add(o4);

        LocalDateTime whenEquipmentWouldBeFree = performedOperations
                .stream()
                .filter(operation -> Objects.nonNull(operation.getEndTime()))
                .filter(operation -> operation.getEndTime().isAfter(LocalDateTime.now()))
                .map(OperationResult::getEndTime)
                .collect(Collectors.toList())
                .stream()
                .max(LocalDateTime::compareTo)
                .orElse(null);

        System.out.println(Objects.nonNull(whenEquipmentWouldBeFree));
        System.out.println(whenEquipmentWouldBeFree);

    }

}
