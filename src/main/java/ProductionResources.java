import algorithm.*;
import algorithm.alpha.AlphaVariatorAlgorithm;
import algorithm.alpha.AlphaVariatorAlgorithm1Parallel;
import algorithm.backpack.BackpackAlgorithm;
import algorithm.candidates.CandidatesBaseAlgorithm;
import algorithm.candidates.CandidatesOwnAlgorithm;
import algorithm.model.order.Order;
import algorithm.model.production.Production;
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
import util.AlternativenessCount;
import util.Criterion;
import util.Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
     *             <p>           Следующие аргументы для POSS: <имя файла производства>.xml <имя файла заказов>.xml</p>
     *             <p>           Следующие аргументы для REAL: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов>.xml</p>
     *             <p>           Следующие аргументы для COMP: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов первого>.xml <имя файла результатов второго>.xml </p>
     *             <p>           Следующие аргументы для BASIS: <тип алгоритма>(BASE / OWN_ALPHA / OWN_BACKPACK)
     *             <p>               Следующие аргументы для BASE:</p> <Название папки с данными производства и заказов>
     *                 <Количество пар производство-заказы> <имя файла результатов>.xml <количество запусков алгоритма> <Тип фронтального алгоритма> <Количество потоков для фронтального алгоритма></p>
     *             <p>               Следующие аргументы для OWN_ALPHA:<Название папки с данными производства и заказов> <Количество пар производство-заказы>
     *                 <Бюджет генератора альтернативностей> <имя файла результатов>.xml
     *                 <количество запусков алгоритма> <количество потоков для вариатора> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма></p>
     *             <p>               Следующие аргументы для OWN_BACKPACK: <Название папки с данными производства и заказов> <Количество пар производство-заказы>
     *                 <Бюджет генератора альтернативностей> <Бюджет запусков пересчёта мощностей> <имя файла результатов>.xml
     *                 <количество запусков алгоритма> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма></p>
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
                    algorithm = new CandidatesBaseAlgorithm(production, orders.getOrders(), null);
                    OutputResult result = algorithm.start();
                    if (RealityTester.test(production, orders, result)) {
                        WRITER.writeResultFile(argv[4], result);
                    } else {
                        System.out.println("Неверные результаты.");
                    }
                } else if ("own".equalsIgnoreCase(argv[1])) {
                    System.out.println("1");
                    algorithm = new AlphaVariatorAlgorithm(production, orders.getOrders(), null, "candidates", 1, Integer.parseInt(argv[5]), Integer.parseInt(argv[6]));
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
                if("base".equalsIgnoreCase(argv[2])) {
                    int count = Integer.parseInt(argv[4]);
                    int startsAlg = Integer.parseInt(argv[6]);
                    int frontThreadsCount = Integer.parseInt(argv[8]);

                    try (FileWriter writer = new FileWriter(argv[5], false)) {
                        writer.write("№;Количество заказов;Количество типов деталей;Среднее количество деталей каждого типа;Среднее количество операций на деталь;Количество атомарных ресурсов;Минимальное число альтернатив на деталь;" +
                                "Максимальное число альтернатив на деталь;Среднее число альтернатив на деталь;Количество произведенных операций;Среднее суммарное количество дней просрочки;Средний критерий;Среднее время исполнения в секундах\n");

                        for (int i = 0; i < count; i++) {
                            InputProduction inputProduction = READER.readProductionFile(argv[3] + "/" + (i + 1) + "_production.xml");
                            InputOrderInformation inputOrderInformation = READER.readOrderFile(argv[3] + "/" + (i + 1) + "_orders.xml");

                            if (PossibilityTester.test(inputProduction, inputOrderInformation)) {
                                AlternativenessCount alternativenessCount = Data.getAlternativenessCount(inputOrderInformation.getOrders());
                                long equipmentCount = Data.getEquipmentCount(inputProduction);

                                long performOperationsCount = 0;
                                long averageOverdueDays = 0;
                                double averageCriterion = 0;
                                long averageTime = 0;

                                for (int j = 0; j < startsAlg; j++) {

                                    System.out.println(i + ":" + j + ": Запущен...");

                                    ArrayList<Order> orders = new ArrayList<>();
                                    inputOrderInformation.getOrders().forEach(inputOrder -> {
                                        orders.add(new Order(inputOrder));
                                    });
                                    long startTime = System.currentTimeMillis();
                                    Algorithm algorithm = FrontAlgorithmFactory.getBaseFrontAlgorithm(new Production(inputProduction), orders, null, argv[7], frontThreadsCount);
                                    OutputResult result = algorithm.start();

                                    if (RealityTester.test(inputProduction, inputOrderInformation, result)) {
                                        long endTime = System.currentTimeMillis();

                                        performOperationsCount += Data.getPerformOperationsCount(result);
                                        averageOverdueDays += Data.getAverageOverdueDays(inputOrderInformation.getOrders(), result);
                                        averageCriterion += Criterion.getCriterion(inputOrderInformation, result);
                                        averageTime += (endTime - startTime) / 1000;

                                        System.out.println(i + ":" + j + ": Завершён...");
                                    } else {
                                        throw new Exception(i + ":" + j + ": Результат алгоритма не соответствует заказам");
                                    }
                                }

                                writeIntoTable(startsAlg, writer, i, inputOrderInformation, alternativenessCount, equipmentCount, performOperationsCount, averageOverdueDays, averageCriterion, averageTime);
                            } else {
                                throw new Exception(i + ": Заказы не соответствуют производству");
                            }
                        }
                        System.out.println("Работа завершена.");
                    }
                } else if("own_alpha".equalsIgnoreCase(argv[2])) {
                    int count = Integer.parseInt(argv[4]);
                    int startGen = Integer.parseInt(argv[5]);
                    int budgetGen = Integer.parseInt(argv[6]);
                    int startsAlg = Integer.parseInt(argv[8]);
                    int threadsCount = Integer.parseInt(argv[9]);
                    int frontThreadsCount = Integer.parseInt(argv[11]);

                    try (FileWriter writer = new FileWriter(argv[7], false)) {
                        writer.write("№;Количество заказов;Количество типов деталей;Среднее количество деталей каждого типа;Среднее количество операций на деталь;Количество атомарных ресурсов;Минимальное число альтернатив на деталь;" +
                                "Максимальное число альтернатив на деталь;Среднее число альтернатив на деталь;Количество произведенных операций;Среднее суммарное количество дней просрочки;Средний критерий;Среднее время исполнения в секундах\n");

                        for (int i = 0; i < count; i++) {
                            InputProduction inputProduction = READER.readProductionFile(argv[3] + "/" + (i + 1) + "_production.xml");
                            InputOrderInformation inputOrderInformation = READER.readOrderFile(argv[3] + "/" + (i + 1) + "_orders.xml");

                            if (PossibilityTester.test(inputProduction, inputOrderInformation)) {
                                AlternativenessCount alternativenessCount = Data.getAlternativenessCount(inputOrderInformation.getOrders());
                                long equipmentCount = Data.getEquipmentCount(inputProduction);

                                long performOperationsCount = 0;
                                long averageOverdueDays = 0;
                                double averageCriterion = 0;
                                long averageTime = 0;

                                for (int j = 0; j < startsAlg; j++) {

                                    System.out.println(i + ":" + j + ": Запущен...");

                                    ArrayList<Order> orders = new ArrayList<>();
                                    inputOrderInformation.getOrders().forEach(inputOrder -> {
                                        orders.add(new Order(inputOrder));
                                    });
                                    long startTime = System.currentTimeMillis();
                                    Algorithm algorithm = null;
                                    if(threadsCount == 1) {
                                        algorithm = new AlphaVariatorAlgorithm(inputProduction, inputOrderInformation.getOrders(), null, argv[10], frontThreadsCount, startGen, budgetGen);
                                    } else {
                                        algorithm = new AlphaVariatorAlgorithm1Parallel(inputProduction, inputOrderInformation.getOrders(), null, argv[10], frontThreadsCount, startGen, budgetGen, threadsCount);
                                    }
                                    OutputResult result = algorithm.start();

                                    if (RealityTester.test(inputProduction, inputOrderInformation, result)) {
                                        long endTime = System.currentTimeMillis();

                                        performOperationsCount += Data.getPerformOperationsCount(result);
                                        averageOverdueDays += Data.getAverageOverdueDays(inputOrderInformation.getOrders(), result);
                                        averageCriterion += Criterion.getCriterion(inputOrderInformation, result);
                                        averageTime += (endTime - startTime) / 1000;

                                        System.out.println(i + ":" + j + ": Завершён...");
                                    } else {
                                        throw new Exception(i + ":" + j + ": Результат алгоритма не соответствует заказам");
                                    }
                                }

                                writeIntoTable(startsAlg, writer, i, inputOrderInformation, alternativenessCount, equipmentCount, performOperationsCount, averageOverdueDays, averageCriterion, averageTime);
                            } else {
                                throw new Exception(i + ": Заказы не соответствуют производству");
                            }
                        }
                        System.out.println("Работа завершена.");
                    }
                } else if("own_backpack".equalsIgnoreCase(argv[2])) {
                    int count = Integer.parseInt(argv[4]);
                    int budgetGen = Integer.parseInt(argv[5]);
                    int repeatBudget = Integer.parseInt(argv[6]);
                    int startsAlg = Integer.parseInt(argv[8]);
                    int frontThreadsCount = Integer.parseInt(argv[10]);

                    try (FileWriter writer = new FileWriter(argv[7], false)) {
                        writer.write("№;Количество заказов;Количество типов деталей;Среднее количество деталей каждого типа;Среднее количество операций на деталь;Количество атомарных ресурсов;Минимальное число альтернатив на деталь;" +
                                "Максимальное число альтернатив на деталь;Среднее число альтернатив на деталь;Количество произведенных операций;Среднее суммарное количество дней просрочки;Средний критерий;Среднее время исполнения в секундах\n");

                        for (int i = 0; i < count; i++) {
                            InputProduction inputProduction = READER.readProductionFile(argv[3] + "/" + (i + 1) + "_production.xml");
                            InputOrderInformation inputOrderInformation = READER.readOrderFile(argv[3] + "/" + (i + 1) + "_orders.xml");

                            if (PossibilityTester.test(inputProduction, inputOrderInformation)) {
                                AlternativenessCount alternativenessCount = Data.getAlternativenessCount(inputOrderInformation.getOrders());
                                long equipmentCount = Data.getEquipmentCount(inputProduction);

                                long performOperationsCount = 0;
                                long averageOverdueDays = 0;
                                double averageCriterion = 0;
                                long averageTime = 0;

                                for (int j = 0; j < startsAlg; j++) {

                                    System.out.println(i + ":" + j + ": Запущен...");

                                    ArrayList<Order> orders = new ArrayList<>();
                                    inputOrderInformation.getOrders().forEach(inputOrder -> {
                                        orders.add(new Order(inputOrder));
                                    });
                                    long startTime = System.currentTimeMillis();
                                    Algorithm algorithm = new BackpackAlgorithm(inputProduction, inputOrderInformation.getOrders(), null, argv[9], frontThreadsCount, budgetGen, repeatBudget);
                                    OutputResult result = algorithm.start();

                                    if (RealityTester.test(inputProduction, inputOrderInformation, result)) {
                                        long endTime = System.currentTimeMillis();

                                        performOperationsCount += Data.getPerformOperationsCount(result);
                                        averageOverdueDays += Data.getAverageOverdueDays(inputOrderInformation.getOrders(), result);
                                        averageCriterion += Criterion.getCriterion(inputOrderInformation, result);
                                        averageTime += (endTime - startTime) / 1000;

                                        System.out.println(i + ":" + j + ": Завершён...");
                                    } else {
                                        throw new Exception(i + ":" + j + ": Результат алгоритма не соответствует заказам");
                                    }
                                }

                                writeIntoTable(startsAlg, writer, i, inputOrderInformation, alternativenessCount, equipmentCount, performOperationsCount, averageOverdueDays, averageCriterion, averageTime);
                            } else {
                                throw new Exception(i + ": Заказы не соответствуют производству");
                            }
                        }
                        System.out.println("Работа завершена.");
                    }
                } else {
                    System.out.println("Неверный список аргументов после BASIS. Используйте \"help\" чтобы увидеть список доступных команд.");
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

                    if (results1.size() == results2.size()) {
                        try (FileWriter writer = new FileWriter(argv[3], false)) {
                            writer.write("№;Имя файла с лучшим результатом по времени просрочки;Обгон по времени просрочки;Имя файла с лучшим результатом по критерию;Обгон по критерию\n");

                            for (int i = 1; i < results1.size(); i++) {
                                String[] split1 = results1.get(i).split(";");
                                String[] split2 = results2.get(i).split(";");
                                double days1 = Double.parseDouble(split1[10]);
                                double days2 = Double.parseDouble(split2[10]);
                                double criterion1 = Double.parseDouble(split1[11]);
                                double criterion2 = Double.parseDouble(split2[11]);

                                if (days1 < days2) {
                                    writer.write(i + ";" +
                                            argv[1] + ";" +
                                            (days2 - days1) + ";");
                                } else if (days1 > days2) {
                                    writer.write(i + ";" +
                                            argv[2] + ";" +
                                            (days1 - days2) + ";");
                                } else {
                                    writer.write(i + ";Одинаково;0;");
                                }

                                if (criterion1 < criterion2) {
                                    writer.write(argv[1] + ";" +
                                            (criterion2 - criterion1) + "\n");
                                } else if (criterion1 > criterion2) {
                                    writer.write(argv[2] + ";" +
                                            (criterion1 - criterion2) + "\n");
                                } else {
                                    writer.write("Одинаково;0\n");
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
        } else {
            System.out.println("Неверный список аргументов. Используйте \"help\" чтобы увидеть список доступных команд.");
        }
    }

    private static void writeIntoTable(int startsAlg, FileWriter writer, int i, InputOrderInformation inputOrderInformation, AlternativenessCount alternativenessCount, long equipmentCount, double performOperationsCount, double averageOverdueDays, double averageCriterion, double averageTime) throws IOException {
        writer.write((i + 1) + ";" +
                inputOrderInformation.getOrders().size() + ";" +
                Data.getDetailTypesCount(inputOrderInformation.getOrders()) + ";" +
                Data.getAverageDetailsCount(inputOrderInformation.getOrders()) + ";" +
                Data.getAverageOperationsCountOnDetail(inputOrderInformation.getOrders()) + ";" +
                equipmentCount + ";" +
                alternativenessCount.min + ";" +
                alternativenessCount.max + ";" +
                alternativenessCount.average + ";" +
                (performOperationsCount / startsAlg) + ";" +
                (averageOverdueDays / startsAlg) + ";" +
                (averageCriterion / startsAlg) + ";" +
                (averageTime / startsAlg) + "\n");
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
        System.out.println(argv.length);
        System.out.println(Arrays.toString(argv));
        if (argv.length >= 2) {
            if ("poss".equalsIgnoreCase(argv[1]) && argv.length >= 4) {
                return true;
            } else if ("real".equalsIgnoreCase(argv[1]) && argv.length >= 5) {
                return true;
            } else if ("comp".equalsIgnoreCase(argv[1]) && argv.length >= 6) {
                return true;
            } else {
                if ("basis".equalsIgnoreCase(argv[1]) && argv.length >= 9) {
                    if ("base".equalsIgnoreCase(argv[2]) && argv.length == 9) {
                        Integer count = null;
                        Integer algStarts = null;
                        Integer frontThreadsCount = null;
                        try {
                            count = Integer.parseInt(argv[4]);
                            algStarts = Integer.parseInt(argv[6]);
                            frontThreadsCount = Integer.parseInt(argv[8]);
                        } catch (NumberFormatException e) {
                            return false;
                        }
                        return count > 0 && algStarts > 0 && frontThreadsCount > 0;
                    } else if ("own_alpha".equalsIgnoreCase(argv[2]) && argv.length == 12) {
                        Integer count = null;
                        Integer startsGen = null;
                        Integer budget = null;
                        Integer algStarts = null;
                        Integer threadsCount = null;
                        Integer frontThreadsCount = null;
                        if(!"candidates".equalsIgnoreCase(argv[10]) && !"record".equalsIgnoreCase(argv[10])) {
                            return false;
                        }
                        try {
                            count = Integer.parseInt(argv[4]);
                            startsGen = Integer.parseInt(argv[5]);
                            budget = Integer.parseInt(argv[6]);
                            algStarts = Integer.parseInt(argv[8]);
                            threadsCount = Integer.parseInt(argv[9]);
                            frontThreadsCount = Integer.parseInt(argv[11]);
                        } catch (NumberFormatException e) {
                            return false;
                        }
                        return count > 0 && startsGen > 0 && budget > startsGen && algStarts > 0 && threadsCount > 0 && frontThreadsCount > 0;
                    } else if("own_backpack".equalsIgnoreCase(argv[2]) && argv.length == 11) {
                        Integer count = null;
                        Integer budget = null;
                        Integer repeatBudget = null;
                        Integer algStarts = null;
                        Integer frontThreadsCount = null;
                        if(!"candidates".equalsIgnoreCase(argv[9]) && !"record".equalsIgnoreCase(argv[9])) {
                            return false;
                        }
                        try {
                            count = Integer.parseInt(argv[4]);
                            budget = Integer.parseInt(argv[5]);
                            repeatBudget = Integer.parseInt(argv[6]);
                            algStarts = Integer.parseInt(argv[8]);
                            frontThreadsCount = Integer.parseInt(argv[10]);
                        } catch (NumberFormatException e) {
                            return false;
                        }
                        return count > 0 && budget > 0 && repeatBudget > 0 && algStarts > 0 && frontThreadsCount > 0;
                    }
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
        System.out.println("        Аргументы для BASIS:  <тип алгоритма>(BASE / OWN_ALPHA / OWN_BACKPACK)");
        System.out.println("            Аргументы для BASE:  <тип алгоритма>(BASE / OWN_ALPHA / OWN_BACKPACK)");
        System.out.println("            Аргументы для OWN_ALPHA:  <Название папки с данными производства и заказов> <Количество пар производство-заказы><Бюджет генератора альтернативностей> <имя файла результатов>.xml <количество запусков алгоритма> <количество потоков для вариатора> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма>");
        System.out.println("            Аргументы для OWN_BACKPACK:  <Название папки с данными производства и заказов> <Количество пар производство-заказы> <Бюджет генератора альтернативностей> <Бюджет запусков пересчёта мощностей> <имя файла результатов>.xml <количество запусков алгоритма> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма>");
        System.out.println("    Аргументы для COMP_RESULT_TABLES: <имя файла с таблицей результатов первого алгоритма>.csv <имя файла с таблицей результатов второго алгоритма>.csv <имя файла с результатами сравнения>.csv");
    }
}