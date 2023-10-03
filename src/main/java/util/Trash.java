package util;


import algorithm.Algorithm;
import algorithm.AlgorithmFactory;
import algorithm.AlternativenessOwnAlgorithm;
import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import parse.input.XMLReader;
import parse.output.XMLWriter;
import testing.ComparisonTester;
import testing.PossibilityTester;
import testing.RealityTester;

import java.time.LocalDateTime;

/**
 * Класс для любых тестов и проверок
 */
public class Trash {

    public static void main(String[] args) throws Exception {

        XMLReader reader = new XMLReader();
        XMLWriter xmlWriter = new XMLWriter();

        InputProduction inputProduction = reader.readProductionFile("doc/system.xml");
        InputOrderInformation orders = reader.readOrderFile("doc/tech.xml");

//        Algorithm algorithm1 = AlgorithmFactory.getNewBaseAlgorithm(inputProduction, orders.getOrders(), LocalDateTime.of(2017, 1, 1, 8, 0, 0));
//        OutputResult outputResult1 = algorithm1.start();
//        xmlWriter.writeResultFile("test1.xml", outputResult1);
//
//        Algorithm algorithm2 = AlgorithmFactory.getNewBaseAlgorithm(inputProduction, orders.getOrders(), LocalDateTime.of(2017, 1, 1, 8, 0, 0));
//        OutputResult outputResult2 = algorithm2.start();
//        xmlWriter.writeResultFile("test2.xml", outputResult2);
//
//        System.out.println(PossibilityTester.test(inputProduction, orders));
//        System.out.println(RealityTester.test(inputProduction, orders, outputResult1));
//        System.out.println(RealityTester.test(inputProduction, orders, outputResult2));
//        ComparisonTester.test(orders, outputResult1, outputResult2);

        AlternativenessOwnAlgorithm algorithm = new AlternativenessOwnAlgorithm(inputProduction, orders.getOrders(), LocalDateTime.of(2017, 1, 1, 8, 0, 0));
        algorithm.start();



    }

}
