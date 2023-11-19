import algorithm.*;
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
import util.Criterion;
import util.Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
     *             <p>1 аргумент - Тип работы: ALG / GEN / TEST / COMP_RESULT_TABLES</p>
     *             <p>    ALG - запустить работу алгоритма, записать результаты в файл</p>
     *             <p>        Следующие аргументы для ALG: <тип алгоритма>(BASE / OWN) <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml</p>
     *             <p>    GEN - запустить генератор файлов производства и заказов, сохранить в файлы</p>
     *             <p>        Следующие аргументы для GEN: <имя файла параметров генератора>.json <количество экземпляров для генерации></p>
     *             <p>    TEST - запустить тестирование на уже существующих данных</p>
     *             <p>        Второй аргумент после TEST - Тип теста: POSS / REAL / COMP / BASIS</p>
     *             <p>    Следующие аргументы для POSS: <имя файла производства>.xml <имя файла заказов>.xml</p>
     *             <p>    Следующие аргументы для REAL: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов>.xml</p>
     *             <p>    Следующие аргументы для COMP: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов первого>.xml <имя файла результатов второго>.xml </p>
     *             <p>    Следующие аргументы для BASIS: <Название папки с данными производства и заказов> <Количество пар производство-заказы> <Стартовое количество распределений альтернативностей> <Бюджет генератора альтернативностей> <имя файла результатов>.xml <количество запусков алгоритма></p>
     *             <p>    COMP_RESULT_TABLES - сравнить таблицы с результатами работы двух алгоритмов</p>
     *             <p>        Следующие аргументы для COMP_RESULT_TABLES: <имя файла с таблицей результатов первого алгоритма>.csv <имя файла с таблицей результатов второго алгоритма>.csv <имя файла с результатами сравнения>.csv</p>
     *             <br>
     *             <p>Примеры:</p>
     *             <p>    java ProductionResources ALG BASE production.xml orders.xml result.xml </p>
     *             <p>    java ProductionResources TEST REAL production.xml orders.xml result.xml </p>
     *             <p>    java ProductionResources TEST POSS production.xml orders.xml resultBase.xml resultOwn.xml </p>
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
                    OutputResult result = algorithm.start();
                    if (RealityTester.test(production, orders, result)) {
                        WRITER.writeResultFile(argv[4], result);
                    } else {
                        System.out.println("Неверные результаты.");
                    }
                } else if ("own".equalsIgnoreCase(argv[1])) {
                    System.out.println("1");
                    algorithm = AlgorithmFactory.getNewOwnAlgorithm(production, orders.getOrders(), null, Integer.parseInt(argv[5]), Integer.parseInt(argv[6]), Integer.parseInt(argv[7]));
                    System.out.println("2");
                    OutputResult result = algorithm.start();
                    System.out.println("3");
                    if (RealityTester.test(production, orders, result)) {
                        WRITER.writeResultFile(argv[4], result);
                    } else {
                        System.out.println("Неверные результаты.");
                    }
                } else {
                    System.out.println("Неверный список аргументов. Используйте \"help\" чтобы увидеть список доступных команд.");
                }
            }
        } else if (argv.length > 0 && "gen".equalsIgnoreCase(argv[0]) && checkForGen(argv)) {
            GeneratorParameters generatorParameters = GeneratorJsonReader.readGeneratorParameters(argv[1]);
            int numberOfPacks = Integer.parseInt(argv[2]);
            ArrayList<GeneratedData> generatedData = Generator.generateData(numberOfPacks, generatorParameters);
            for (int i = 0; i < numberOfPacks; i++) {
                if (GeneratorTester.test(generatorParameters, generatedData.get(i))) {
                    if (PossibilityTester.test(generatedData.get(i).getInputProduction(), generatedData.get(i).getInputOrderInformation())) {
                        WRITER.writeProductionFile((i + 1) + "_production.xml", generatedData.get(i).getInputProduction());
                        WRITER.writeOrderInformationFile((i + 1) + "_orders.xml", generatedData.get(i).getInputOrderInformation());
                    } else {
                        System.out.println("На шаге " + i + " сгенерированы неверные данные.");
                    }
                } else {
                    System.out.println("На шаге " + i + " сгенерированные данные не соответствуют параметрам генератора.");
                }
            }
        } else if (argv.length > 0 && "test".equalsIgnoreCase(argv[0]) && checkForTest(argv)) {
            if ("basis".equalsIgnoreCase(argv[1])) {
                System.out.println(Arrays.toString(argv));
                int count = Integer.parseInt(argv[3]);
                int startGen = Integer.parseInt(argv[4]);
                int budgetGen = Integer.parseInt(argv[5]);
                int startsAlg = Integer.parseInt(argv[7]);

                try (FileWriter writer = new FileWriter(argv[6], false)) {
                    writer.write("№;Количество заказов;Количество операций;Количество атомарных ресурсов;Минимальное число альтернатив на деталь;" +
                            "Максимальное число альтернатив на деталь;Среднее число альтернатив на деталь;Среднее количество дней просрочки в днях;Средний критерий;Среднее время исполнения в секундах\n");

                    for (int i = 0; i < count; i++) {
                        InputProduction production = READER.readProductionFile(argv[2] + "/" + (i + 1) + "_production.xml");
                        InputOrderInformation orders = READER.readOrderFile(argv[2] + "/" + (i + 1) + "_orders.xml");

                        if (PossibilityTester.test(production, orders)) {
//                            BaseAlgorithm baseAlgorithm = new BaseAlgorithm(production, orders.getOrders(), null);
//                            OutputResult baseResult = baseAlgorithm.start();

                            Data.AlternativenessCount alternativenessCount = Data.getAlternativenessCount(orders.getOrders());
                            long equipmentCount = Data.getEquipmentCount(production);

                            long operationsCount = 0;
                            long averageOverdueDays = 0;
                            double averageCriterion = 0;
                            long averageTime = 0;

                            for (int j = 0; j < startsAlg; j++) {

                                System.out.println(i + ":" + j + ": Запущен...");

                                long startTime = System.currentTimeMillis();
                                AlternativenessOwnAlgorithm ownAlgorithm = new AlternativenessOwnAlgorithm(production, orders.getOrders(), null, startGen, budgetGen);
                                OutputResult ownResult = ownAlgorithm.start();

                                if (RealityTester.test(production, orders, ownResult)) {
                                    long endTime = System.currentTimeMillis();

                                    operationsCount += Data.getOperationsCount(ownResult);
                                    averageOverdueDays += Data.getAverageOverdueDays(orders.getOrders(), ownResult);
                                    averageCriterion += Criterion.getCriterion(orders, ownResult);
                                    averageTime += (endTime - startTime) / 1000;

                                    System.out.println(i + ":" + j + ": Завершён...");
                                } else {
                                    throw new Exception(i + ":" + j + ": Результат собственного алгоритма не соответствует заказам");
                                }
                            }

                            writer.write((i + 1) + ";" +
                                    orders.getOrders().size() + ";" +
                                    ((double) operationsCount / startsAlg) + ";" +
                                    equipmentCount + ";" +
                                    alternativenessCount.min + ";" +
                                    alternativenessCount.max + ";" +
                                    alternativenessCount.average + ";" +
                                    ((double) averageOverdueDays / startsAlg) + ";" +
                                    (averageCriterion / startsAlg) + ";" +
                                    ((double) averageTime / startsAlg) + " секунд\n");
                        } else {
                            throw new Exception(i + ": Заказы не соответствуют производству");
                        }
                    }
                    System.out.println("Работа завершена.");
                }


            } else {
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
                    if (RealityTester.test(production, orders, result1)) {
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
            }

        } else if (argv.length == 4 && "comp_result_tables".equalsIgnoreCase(argv[0])) {
            try (BufferedReader reader1 = new BufferedReader(new FileReader(argv[1]))) {
                List<String> results1 = reader1.lines().collect(Collectors.toList());
                try (BufferedReader reader2 = new BufferedReader(new FileReader(argv[2]))) {
                    List<String> results2 = reader2.lines().collect(Collectors.toList());

                    if(results1.size() == results2.size()) {
                        try (FileWriter writer = new FileWriter(argv[3], false)) {
                            writer.write("№;Имя файла с лучшим результатом по времени просрочки;Обгон по времени просрочки;Имя файла с лучшим результатом по критерию;Обгон по критерию\n");

                            for (int i = 1; i < results1.size(); i++) {
                                String[] split1 = results1.get(i).split(";");
                                String[] split2 = results2.get(i).split(";");
                                System.out.println(split1[7]);
                                double days1 = Double.parseDouble(split1[7]);
                                double days2 = Double.parseDouble(split2[7]);
                                double criterion1 = Double.parseDouble(split1[8]);
                                double criterion2 = Double.parseDouble(split2[8]);

                                if(days1 < days2) {
                                    writer.write(i + ";" +
                                            argv[1] + ";" +
                                            (days2 - days1) + ";");
                                } else if(days1 > days2) {
                                    writer.write(i + ";" +
                                            argv[2] + ";" +
                                            (days1 - days2) + ";");
                                } else {
                                    writer.write((i+1) + ";Одинаково;0;");
                                }

                                if(criterion1 < criterion2) {
                                    writer.write(argv[1] + ";" +
                                            (criterion2 - criterion1) + "\n");
                                } else if(criterion1 > criterion2) {
                                    writer.write(argv[2] + ";" +
                                            (criterion2 - criterion1) + "\n");
                                } else {
                                    writer.write(";Одинаково;0\n");
                                }
                            }
                        }
                    } else {
                        System.out.println("Несовпадающие таблицы результатов.");
                    }

                }
            }

        } else if (argv.length > 0 && "help".equalsIgnoreCase(argv[0])) {
            help();
        }  else {
            System.out.println("Неверный список аргументов. Используйте \"help\" чтобы увидеть список доступных команд.");
        }
    }

    private static boolean checkForAlg(String[] argv) {
        if (argv.length >= 5) {
            if ("base".equalsIgnoreCase(argv[1])) {
                return true;
            }
            if ("own".equalsIgnoreCase(argv[1])) {
                if (argv.length == 8) {
                    try {
                        Integer.parseInt(argv[5]);
                        Integer.parseInt(argv[6]);
                        Integer.parseInt(argv[7]);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkForGen(String[] argv) {
        if (argv.length >= 3) {
            try {
                return Integer.parseInt(argv[2]) > 0;
            } catch (NumberFormatException e) {
                return false;
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
            } else if ("comp".equalsIgnoreCase(argv[1]) && argv.length >= 6) {
                return true;
            } else {
                if ("basis".equalsIgnoreCase(argv[1]) && argv.length >= 8) {
                    Integer count = null;
                    Integer startGen = null;
                    Integer budget = null;
                    Integer algStarts = null;
                    try {
                        count = Integer.parseInt(argv[3]);
                        startGen = Integer.parseInt(argv[4]);
                        budget = Integer.parseInt(argv[5]);
                        algStarts = Integer.parseInt(argv[7]);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                    return count > 0 && startGen > 0 && budget > startGen && algStarts > 0;
                }

            }
        }
        return false;
    }

    private static void help() {
        System.out.println("1 аргумент - Тип работы: ALG / GEN / TEST / COMP_RESULT_TABLES");
        System.out.println("    Аргументы для ALG: <тип алгоритма>(BASE / OWN) <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml");
        System.out.println("    Аргументы для GEN: <имя файла параметров генератора>.json <количество экземпляров для генерации>");
        System.out.println("    Аргументы для TEST: Тип теста: POSS / REAL / COMP / BASIS");
        System.out.println("        Аргументы для POSS: <имя файла производства>.xml <имя файла заказов>.xml");
        System.out.println("        Аргументы для REAL: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов>.xml");
        System.out.println("        Аргументы для COMP: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов первого>.xml <имя файла результатов второго>.xml");
        System.out.println("        Аргументы для BASIS: <Название папки с данными производства и заказов> <Количество пар производство-заказы> <Стартовое количество распределений альтернативностей> <Бюджет генератора альтернативностей> <имя файла результатов>.xml <количество запусков алгоритма>");
        System.out.println("    Аргументы для COMP_RESULT_TABLES: <имя файла с таблицей результатов первого алгоритма>.csv <имя файла с таблицей результатов второго алгоритма>.csv <имя файла с результатами сравнения>.csv");
    }
}
