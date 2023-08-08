import algorithm.Algorithm;
import algorithm.AlgorithmFactory;
import model.result.Result;
import model.production.Production;
import model.order.*;
import parse.input.XMLReader;
import parse.output.XMLWriter;
import testing.ComparisonTester;
import testing.PossibilityTester;
import testing.RealityTester;

public class ProductionResources {


    private static final String[] FOR_FIRST_ARG = {"ALG", "TEST", "HELP"};
    private static final String[] FOR_ALG_TYPE = {"BASE", "OWN"};
    private static final String[] FOR_TEST_TYPE = {"POSS", "REAL", "COMP"};
    private static final XMLReader READER = new XMLReader();
    private static final XMLWriter WRITER = new XMLWriter();

    /**
     * Класс для запуска работы системы из консоли:
     * Перед созданием объекта алгоритма запустить со считанными из файла объектами production и orders PossibilityTester
     *
     * @param argv - аргументы командной строки:
     *             <p>1 аргумент - Тип работы: ALG / TEST</p>
     *             <p>ALG - запустить работу алгоритма, записать результаты в файл</p>
     *             <p>Следующие аргументы для ALG: <тип алгоритма>(BASE / OWN) <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml</p>
     *             <p>TEST - запустить тестирование на уже существующих данных</p>
     *             <p>Второй аргумент после TEST - Тип теста: POSS / REAL / COMP</p>
     *             <p>      Следующие аргументы для POSS: <имя файла производства>.xml <имя файла заказов>.xml</p>
     *             <p>      Следующие аргументы для REAL: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов>.xml</p>
     *             <p>      Следующие аргументы для COMP: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов первого>.xml <имя файла результатов второго>.xml </p>
     *             <br>
     *             <p>Примеры:</p>
     *             <p>      java ProductionResources ALG BASE production.xml orders.xml result.xml </p>
     *             <p>      java ProductionResources TEST REAL production.xml orders.xml result.xml </p>
     *             <p>      java ProductionResources TEST POSS production.xml orders.xml resultBase.xml resultOwn.xml </p>
     */
    public static void main(String[] argv) throws Exception {
        if (argv.length > 0 && "alg".equalsIgnoreCase(argv[0]) && checkForAlg(argv)) {
            Production production = READER.readProductionFile(argv[2]);
            OrderInformation orders = READER.readOrderFile(argv[3]);
            if (!PossibilityTester.test(production, orders)) {
                System.out.println("Заказы нельзя выполнить на данном производстве.");
            } else {
                Algorithm algorithm = null;
                if ("base".equalsIgnoreCase(argv[1])) {
                    algorithm = AlgorithmFactory.getNewBaseAlgorithm(production, orders.getOrders(), null);
                    WRITER.writeResultFile(argv[4], algorithm.start());
                } else {
                    System.out.println("WILL BE IN FUTURE");
                }
            }
        } else if (argv.length > 0 && "test".equalsIgnoreCase(argv[0]) && checkForTest(argv)) {
            Production production = null;
            OrderInformation orders = null;
            Result result1 = null;
            Result result2 = null;
            if ("poss".equalsIgnoreCase(argv[1])) {
                production = READER.readProductionFile(argv[2]);
                orders = READER.readOrderFile(argv[3]);
                if (PossibilityTester.test(production, orders)) {
                    System.out.println("Заказы можно выполнить на данном производстве.");
                } else {
                    System.out.println("Заказы нельзя выполнить на данном производстве.");
                }
            } else if ("real".equalsIgnoreCase(argv[1])) {
                production = READER.readProductionFile(argv[2]);
                orders = READER.readOrderFile(argv[3]);
                result1 = READER.readResultFile(argv[4]);
                if(RealityTester.test(production, orders, result1)) {
                    System.out.println("Данная укладка заказов по производственному времени возможна.");
                } else {
                    System.out.println("Данная укладка заказов по производственному времени невозможна.");
                }
            } else {
                orders = READER.readOrderFile(argv[2]);
                result1 = READER.readResultFile(argv[3]);
                result2 = READER.readResultFile(argv[4]);
                ComparisonTester.test(orders, result1, result2);
            }
        } else if (argv.length > 0 && "help".equalsIgnoreCase(argv[0])) {
            help();
        } else {
            System.out.println("Неверный список аргументов. Используйте \"help\" чтобы увидеть список доступных команд.");
        }
    }

    private static boolean checkForAlg(String[] argv) {
        if (argv.length >= 5) {
            for (String alg :
                    FOR_ALG_TYPE) {
                if (alg.equalsIgnoreCase(argv[1])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkForTest(String[] argv) {
        if (argv.length >= 2) {
            if ("poss".equalsIgnoreCase(argv[1]) && argv.length >= 4) {
                return true;
            } else if ("real".equalsIgnoreCase(argv[1]) && argv.length >= 5) {
                return true;
            } else return "comp".equalsIgnoreCase(argv[1]) && argv.length >= 5;
        }
        return false;
    }

    private static void help() {
        System.out.println("1 аргумент - Тип работы: ALG / TEST / ALG_TEST");
        System.out.println("    Аргументы для ALG: <тип алгоритма>(BASE / OWN) <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml");
        System.out.println("    Аргументы для TEST: Тип теста: POSS / REAL / COMP");
        System.out.println("        Аргументы для POSS: <имя файла производства>.xml <имя файла заказов>.xml");
        System.out.println("        Аргументы для REAL: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов>.xml");
        System.out.println("        Аргументы для COMP: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов первого>.xml <имя файла результатов второго>.xml");
    }


}
