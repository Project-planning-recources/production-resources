package util;


import algorithm.Algorithm;
import algorithm.AlgorithmFactory;
import model.order.OrderInformation;
import model.production.Production;
import model.result.Result;
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

        Production production = reader.readProductionFile("doc/system.xml");
        OrderInformation orders = reader.readOrderFile("doc/tech.xml");

        Algorithm algorithm1 = AlgorithmFactory.getNewBaseAlgorithm(production, orders.getOrders(), LocalDateTime.of(2017, 1, 1, 8, 0, 0));
        Result result1 = algorithm1.start();
        xmlWriter.writeResultFile("test1.xml", result1);

        Algorithm algorithm2 = AlgorithmFactory.getNewBaseAlgorithm(production, orders.getOrders(), LocalDateTime.of(2017, 1, 1, 8, 0, 0));
        Result result2 = algorithm2.start();
        xmlWriter.writeResultFile("test2.xml", result2);

        System.out.println(PossibilityTester.test(production, orders));
        System.out.println(RealityTester.test(production, orders, result1));
        System.out.println(RealityTester.test(production, orders, result2));
        ComparisonTester.test(orders, result1, result2);



    }

}
