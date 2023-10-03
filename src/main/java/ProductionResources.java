import algorithm.Algorithm;
import algorithm.AlgorithmFactory;
import generator.GeneratedData;
import generator.Generator;
import generator.GeneratorJsonReader;
import generator.GeneratorParameters;
import parse.input.production.*;
import parse.input.order.*;
import parse.input.XMLReader;
import parse.output.XMLWriter;
import parse.output.result.OutputResult;
import testing.ComparisonTester;
import testing.GeneratorTester;
import testing.PossibilityTester;
import testing.RealityTester;

import java.util.ArrayList;

public class ProductionResources {


    private static final String[] FOR_FIRST_ARG = {"ALG", "GEN", "TEST", "HELP"};

    private static final String[] FOR_ALG_TYPE = {"BASE", "OWN"};
    private static final String[] FOR_TEST_TYPE = {"POSS", "REAL", "COMP"};
    private static final XMLReader READER = new XMLReader();
    private static final XMLWriter WRITER = new XMLWriter();

    /**
     * Класс для запуска работы системы из консоли:
     * Перед созданием объекта алгоритма запустить со считанными из файла объектами production и orders PossibilityTester
     *
     * @param argv - аргументы командной строки:
     *             <p>1 аргумент - Тип работы: ALG / GEN / TEST</p>
     *             <p>ALG - запустить работу алгоритма, записать результаты в файл</p>
     *             <p>Следующие аргументы для ALG: <тип алгоритма>(BASE / OWN) <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml</p>
     *             <p>GEN - запустить генератор файлов производства и заказов, сохранить в файлы</p>
     *             <p>Следующие аргументы для GEN: <имя файла параметров генератора>.json <количество экземпляров для генерации></p>
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
            InputProduction production = READER.readProductionFile(argv[2]);
            InputOrderInformation orders = READER.readOrderFile(argv[3]);
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
        }else if(argv.length > 0 && "gen".equalsIgnoreCase(argv[0]) && checkForGen(argv)) {
            GeneratorParameters generatorParameters = GeneratorJsonReader.readGeneratorParameters(argv[1]);
            int numberOfPacks = Integer.parseInt(argv[2]);
            ArrayList<GeneratedData> generatedData = Generator.generateData(numberOfPacks, generatorParameters);
            for (int i = 0; i < numberOfPacks; i++) {
                if(GeneratorTester.test(generatorParameters, generatedData.get(i))) {
                    if (PossibilityTester.test(generatedData.get(i).getInputProduction(), generatedData.get(i).getInputOrderInformation())) {
                        System.out.println("На шаге " + i + " сгенерированы неверные данные.");
                    } else {
                        WRITER.writeProductionFile(argv[1] + "prod" + i, generatedData.get(i).getInputProduction());
                        WRITER.writeOrderInformationFile(argv[1] + "order" + i, generatedData.get(i).getInputOrderInformation());
                    }
                } else {
                    System.out.println("На шаге " + i + " сгенерированные данные не соответствуют параметрам генератора.");
                }
            }
        }else if (argv.length > 0 && "test".equalsIgnoreCase(argv[0]) && checkForTest(argv)) {
            InputProduction production = null;
            InputOrderInformation orders = null;
            OutputResult result1 = null;
            OutputResult result2 = null;
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

    private static boolean checkForGen(String[] argv) {
        if (argv.length >= 3) {
            return Integer.parseInt(argv[2]) > 0;
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
        System.out.println("1 аргумент - Тип работы: ALG / GEN / TEST");
        System.out.println("    Аргументы для ALG: <тип алгоритма>(BASE / OWN) <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml");
        System.out.println("    Аргументы для GEN: <имя файла параметров генератора>.json <количество экземпляров для генерации>");
        System.out.println("    Аргументы для TEST: Тип теста: POSS / REAL / COMP");
        System.out.println("        Аргументы для POSS: <имя файла производства>.xml <имя файла заказов>.xml");
        System.out.println("        Аргументы для REAL: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов>.xml");
        System.out.println("        Аргументы для COMP: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов первого>.xml <имя файла результатов второго>.xml");
    }


}
