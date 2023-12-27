import algorithm.*;
import algorithm.alpha.AlphaClusterVariatorAlgorithm;
import algorithm.alpha.AlphaClusterVariatorAlgorithmParallel;
import algorithm.alpha.AlphaVariatorAlgorithm;
import algorithm.alpha.AlphaVariatorAlgorithmParallel;
import algorithm.backpack.BackpackAlgorithm;
import algorithm.candidates.CandidatesBaseAlgorithm;
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

    //todo: Добавить выбор типа алгоритма в ALG, переписать help и комментарий
    /**
     * Класс для запуска работы системы из консоли:
     * Перед созданием объекта алгоритма запустить со считанными из файла объектами production и orders PossibilityTester
     *
     * @param argv - аргументы командной строки:
     *             <p>Тип работы: ALG / GEN / TEST / COMP_RESULT_TABLES</p>
     *             <p>    ALG - запустить работу алгоритма, записать результаты в файл</p>
     *             <p>        Следующие аргументы для ALG: <тип алгоритма>(BASE / OWN_ALPHA / OWN_BACKPACK)</p>
     *             <p>              Следующие аргументы для BASE: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> </p>
     *             <p>                  <Тип фронтального алгоритма> <Количество потоков для фронтального алгоритма></p>
     *             <p>              Следующие аргументы для OWN_ALPHA: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> </p>
     *             <p>                 <Стартовое количество генераций альтернативностей> <Бюджет генератора альтернативностей></p>
     *             <p>                 <количество потоков для вариатора> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма></p>
     *             <p>              Следующие аргументы для OWN_BACKPACK: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> </p>
     *             <p>                 <Бюджет генератора альтернативностей> <Бюджет запусков пересчёта мощностей></p>
     *             <p>                 <тип фронтального алгоритма> <количество потоков для фронтального алгоритма></p>
     *             <p>    GEN - запустить генератор файлов производства и заказов, сохранить в файлы</p>
     *             <p>        Следующие аргументы для GEN: <имя файла параметров генератора>.json <количество экземпляров для генерации></p>
     *             <p>    TEST - запустить тестирование на уже существующих данных</p>
     *             <p>        Второй аргумент после TEST - Тип теста: POSS / REAL / COMP / BASIS</p>
     *             <p>           Следующие аргументы для POSS: <имя файла производства>.xml <имя файла заказов>.xml</p>
     *             <p>           Следующие аргументы для REAL: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов>.xml</p>
     *             <p>           Следующие аргументы для COMP: <имя файла заказов>.xml <имя файла результатов первого>.xml <имя файла результатов второго>.xml </p>
     *             <p>           Следующие аргументы для BASIS: <тип алгоритма>(BASE / OWN_ALPHA / OWN_BACKPACK)
     *             <p>               Следующие аргументы для BASE: <Название папки с данными производства и заказов>
     *             <p> <Количество пар производство-заказы> <имя файла результатов>.csv <количество запусков алгоритма> <Тип фронтального алгоритма> <Количество потоков для фронтального алгоритма></p>
     *             <p>               Следующие аргументы для OWN_ALPHA: <Название папки с данными производства и заказов> <Количество пар производство-заказы><p/>
     *             <p> <Стартовое количество генераций альтернативностей> <Бюджет генератора альтернативностей> <имя файла результатов>.csv</p>
     *             <p><количество запусков алгоритма> <количество потоков для вариатора> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма></p>
     *             <p>               Следующие аргументы для OWN_BACKPACK: <Название папки с данными производства и заказов> <Количество пар производство-заказы></p>
     *             <p> <Бюджет генератора альтернативностей> <Бюджет запусков пересчёта мощностей> <имя файла результатов>.xml</p>
     *             <p> <количество запусков алгоритма> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма></p>
     *             <p>    COMP_RESULT_TABLES - сравнить таблицы с результатами работы двух алгоритмов</p>
     *             <p>        Следующие аргументы для COMP_RESULT_TABLES: <имя файла с таблицей результатов первого алгоритма>.csv <имя файла с таблицей результатов второго алгоритма>.csv <имя файла с результатами сравнения>.csv</p>
     *             <br>
     */
    public static void main(String[] argv) throws Exception {
        if (argv.length > 0 && "alg".equalsIgnoreCase(argv[0]) && checkForAlg(argv)) {
            InputProduction inputProduction = READER.readProductionFile(argv[2]);
            InputOrderInformation inputOrderInformation = READER.readOrderFile(argv[3]);
            if (!PossibilityTester.test(inputProduction, inputOrderInformation)) {
                System.out.println("Заказы нельзя выполнить на данном производстве.");
            } else {
                if ("base".equalsIgnoreCase(argv[1])) {
                    //alg base 1.xml 2.xml res.xml 5 candidates 1
                    //0     1   2     3     4      5     6      7
                    //Следующие аргументы для BASE: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> </p>
//<Тип фронтального алгоритма> <Количество потоков для фронтального алгоритма></p>
                    int startsAlg = Integer.parseInt(argv[5]);
                    int frontThreadsCount = Integer.parseInt(argv[7]);

                    double recordCriterion = Double.MAX_VALUE;
                    OutputResult recordResult = null;

                    if (PossibilityTester.test(inputProduction, inputOrderInformation)) {
                        for (int j = 0; j < startsAlg; j++) {

                            System.out.println(j + ": Запущен...");

                            ArrayList<Order> orders = new ArrayList<>();
                            inputOrderInformation.getOrders().forEach(inputOrder -> {
                                orders.add(new Order(inputOrder));
                            });
                            Algorithm algorithm = FrontAlgorithmFactory.getBaseFrontAlgorithm(new Production(inputProduction), orders, null, argv[6], frontThreadsCount);
                            OutputResult result = algorithm.start();

                            if (RealityTester.test(inputProduction, inputOrderInformation, result)) {
                                double criterion = Criterion.getCriterion(orders, result);
                                if(recordCriterion > criterion) {
                                    recordCriterion = criterion;
                                    recordResult = result;
                                }
                                System.out.println(j + ": Завершён...");
                            } else {
                                throw new Exception(j + ": Результат алгоритма не соответствует заказам");
                            }
                        }
                        WRITER.writeResultFile(argv[4], recordResult);
                    } else {
                        throw new Exception("Заказы не соответствуют производству");
                    }
                    System.out.println("Работа завершена.");
                } else if ("own_alpha".equalsIgnoreCase(argv[1])) {
                    // alg own_alpha 1.xml 2.xml res.xml 5 10 100 4 candidates 1
                    //  0      1       2     3      4    5  6  7  8    9       10
//              Следующие аргументы для OWN_ALPHA: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> </p>
// <Стартовое количество генераций альтернативностей> <Бюджет генератора альтернативностей></p>
// <количество потоков для вариатора> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма></p>

                    int startsAlg = Integer.parseInt(argv[5]);
                    int startGen = Integer.parseInt(argv[6]);
                    int budget = Integer.parseInt(argv[7]);
                    int variatorThreadsCount = Integer.parseInt(argv[8]);
                    int frontThreadsCount = Integer.parseInt(argv[10]);

                    double recordCriterion = Double.MAX_VALUE;
                    OutputResult recordResult = null;

                    if (PossibilityTester.test(inputProduction, inputOrderInformation)) {
                        for (int j = 0; j < startsAlg; j++) {

                            System.out.println(j + ": Запущен...");

                            ArrayList<Order> orders = new ArrayList<>();
                            inputOrderInformation.getOrders().forEach(inputOrder -> {
                                orders.add(new Order(inputOrder));
                            });
                            Algorithm algorithm = null;
                            if(variatorThreadsCount == 1) {
                                algorithm = new AlphaClusterVariatorAlgorithm(inputProduction, inputOrderInformation.getOrders(), null, argv[9], frontThreadsCount, startGen, budget);
                            } else {
                                algorithm = new AlphaClusterVariatorAlgorithmParallel(inputProduction, inputOrderInformation.getOrders(), null, argv[9], frontThreadsCount, startGen, budget, variatorThreadsCount);
                            }
                            OutputResult result = algorithm.start();

                            if (RealityTester.test(inputProduction, inputOrderInformation, result)) {
                                double criterion = Criterion.getCriterion(orders, result);
                                if(recordCriterion > criterion) {
                                    recordCriterion = criterion;
                                    recordResult = result;
                                }
                                System.out.println(j + ": Завершён...");
                            } else {
                                throw new Exception(j + ": Результат алгоритма не соответствует заказам");
                            }
                        }
                        WRITER.writeResultFile(argv[4], recordResult);
                    } else {
                        throw new Exception("Заказы не соответствуют производству");
                    }
                    System.out.println("Работа завершена.");
                } else if("own_backpack".equalsIgnoreCase(argv[1])) {
                    // alg own_backpack 1.xml 2.xml res.xml 5 100 10 candidates 1
                    // 0       1          2     3      4    5  6  7       8     9
//Следующие аргументы для OWN_BACKPACK: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> </p>
// <Бюджет генератора альтернативностей> <Бюджет запусков пересчёта мощностей></p>
// <тип фронтального алгоритма> <количество потоков для фронтального алгоритма></p>
                    int startsAlg = Integer.parseInt(argv[5]);
                    int budget = Integer.parseInt(argv[6]);
                    int repeatCount = Integer.parseInt(argv[7]);
                    int frontThreadsCount = Integer.parseInt(argv[9]);

                    double recordCriterion = Double.MAX_VALUE;
                    OutputResult recordResult = null;

                    if (PossibilityTester.test(inputProduction, inputOrderInformation)) {
                        for (int j = 0; j < startsAlg; j++) {

                            System.out.println(j + ": Запущен...");

                            ArrayList<Order> orders = new ArrayList<>();
                            inputOrderInformation.getOrders().forEach(inputOrder -> {
                                orders.add(new Order(inputOrder));
                            });
                            Algorithm algorithm = new BackpackAlgorithm(inputProduction, inputOrderInformation.getOrders(), null, argv[8],frontThreadsCount, budget, repeatCount);
                            OutputResult result = algorithm.start();

                            if (RealityTester.test(inputProduction, inputOrderInformation, result)) {
                                double criterion = Criterion.getCriterion(orders, result);
                                if(recordCriterion > criterion) {
                                    recordCriterion = criterion;
                                    recordResult = result;
                                }
                                System.out.println(j + ": Завершён...");
                            } else {
                                throw new Exception(j + ": Результат алгоритма не соответствует заказам");
                            }
                        }
                        WRITER.writeResultFile(argv[4], recordResult);
                    } else {
                        throw new Exception("Заказы не соответствуют производству");
                    }
                    System.out.println("Работа завершена.");
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
                                        algorithm = new AlphaClusterVariatorAlgorithm(inputProduction, inputOrderInformation.getOrders(), null, argv[10], frontThreadsCount, startGen, budgetGen);
                                    } else {
                                        algorithm = new AlphaClusterVariatorAlgorithmParallel(inputProduction, inputOrderInformation.getOrders(), null, argv[10], frontThreadsCount, startGen, budgetGen, threadsCount);
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
                } else if("comp".equalsIgnoreCase(argv[1])) {
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
        //alg base 1.xml 2.xml res.xml 5 candidates 1
        //0     1   2     3     4      5     6      7
        //Следующие аргументы для BASE: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> </p>
//<Тип фронтального алгоритма> <Количество потоков для фронтального алгоритма></p>
        System.out.println(Arrays.toString(argv));
        if ("alg".equalsIgnoreCase(argv[0])) {
            if ("base".equalsIgnoreCase(argv[1]) && argv.length == 8) {
                Integer algStarts = null;
                Integer frontThreadsCount = null;
                if(!"candidates".equalsIgnoreCase(argv[6]) && !"record".equalsIgnoreCase(argv[6])) {
                    return false;
                }
                try {
                    algStarts = Integer.parseInt(argv[5]);
                    frontThreadsCount = Integer.parseInt(argv[7]);
                } catch (NumberFormatException e) {
                    return false;
                }
                return algStarts > 0 && frontThreadsCount > 0;
            } else if ("own_alpha".equalsIgnoreCase(argv[1]) && argv.length == 11) {
                // alg own_alpha 1.xml 2.xml res.xml 5 10 100 4 candidates 1
                //  0      1       2     3      4    5  6  7  8    9       10
//              Следующие аргументы для OWN_ALPHA: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> </p>
// <Стартовое количество генераций альтернативностей> <Бюджет генератора альтернативностей></p>
// <количество потоков для вариатора> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма></p>
                Integer algStarts = null;
                Integer startsGen = null;
                Integer budget = null;
                Integer threadsCount = null;
                Integer frontThreadsCount = null;
                if(!"candidates".equalsIgnoreCase(argv[9]) && !"record".equalsIgnoreCase(argv[9])) {
                    return false;
                }
                try {
                    algStarts = Integer.parseInt(argv[5]);
                    startsGen = Integer.parseInt(argv[6]);
                    budget = Integer.parseInt(argv[7]);
                    threadsCount = Integer.parseInt(argv[8]);
                    frontThreadsCount = Integer.parseInt(argv[10]);
                } catch (NumberFormatException e) {
                    return false;
                }
                return startsGen > 0 && budget > startsGen && algStarts > 0 && threadsCount > 0 && frontThreadsCount > 0;
            } else if("own_backpack".equalsIgnoreCase(argv[1]) && argv.length == 10) {
                // alg own_backpack 1.xml 2.xml res.xml 5 100 10 candidates 1
                // 0       1          2     3      4    5  6  7       8     9
//Следующие аргументы для OWN_BACKPACK: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> </p>
// <Бюджет генератора альтернативностей> <Бюджет запусков пересчёта мощностей></p>
// <тип фронтального алгоритма> <количество потоков для фронтального алгоритма></p>
                Integer algStarts = null;
                Integer budget = null;
                Integer repeatBudget = null;
                Integer frontThreadsCount = null;
                if(!"candidates".equalsIgnoreCase(argv[8]) && !"record".equalsIgnoreCase(argv[8])) {
                    return false;
                }
                try {
                    algStarts = Integer.parseInt(argv[5]);
                    budget = Integer.parseInt(argv[6]);
                    repeatBudget = Integer.parseInt(argv[7]);
                    frontThreadsCount = Integer.parseInt(argv[9]);
                } catch (NumberFormatException e) {
                    return false;
                }
                return budget > 0 && repeatBudget > 0 && algStarts > 0 && frontThreadsCount > 0;
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
            } else if ("comp".equalsIgnoreCase(argv[1]) && argv.length == 5) {
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


//    *             <p>Тип работы: ALG / GEN / TEST / COMP_RESULT_TABLES</p>
//            *             <p>    ALG - запустить работу алгоритма, записать результаты в файл</p>
//            *             <p>        Следующие аргументы для ALG: <тип алгоритма>(BASE / OWN_ALPHA / OWN_BACKPACK) <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml</p>
//            *             <p>              Следующие аргументы для BASE: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> </p>
//            *             <p>                  <Тип фронтального алгоритма> <Количество потоков для фронтального алгоритма></p>
//            *             <p>              Следующие аргументы для OWN_ALPHA: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> </p>
//            *             <p>                 <Стартовое количество генераций альтернативностей> <Бюджет генератора альтернативностей></p>
//            *             <p>                 <количество запусков алгоритма> <количество потоков для вариатора> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма></p>
//            *             <p>              Следующие аргументы для OWN_BACKPACK: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> </p>
//            *             <p>                 <Бюджет генератора альтернативностей> <Бюджет запусков пересчёта мощностей></p>
//            *             <p>                 <количество запусков алгоритма> <количество потоков для вариатора> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма></p>
//            *             <p>    GEN - запустить генератор файлов производства и заказов, сохранить в файлы</p>
//            *             <p>        Следующие аргументы для GEN: <имя файла параметров генератора>.json <количество экземпляров для генерации></p>
//            *             <p>    TEST - запустить тестирование на уже существующих данных</p>
//            *             <p>        Второй аргумент после TEST - Тип теста: POSS / REAL / COMP / BASIS</p>
//            *             <p>           Следующие аргументы для POSS: <имя файла производства>.xml <имя файла заказов>.xml</p>
//            *             <p>           Следующие аргументы для REAL: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов>.xml</p>
//            *             <p>           Следующие аргументы для COMP: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов первого>.xml <имя файла результатов второго>.xml </p>
//            *             <p>           Следующие аргументы для BASIS: <тип алгоритма>(BASE / OWN_ALPHA / OWN_BACKPACK)
//            *             <p>               Следующие аргументы для BASE: <Название папки с данными производства и заказов>
//            *             <p> <Количество пар производство-заказы> <имя файла результатов>.xml <количество запусков алгоритма> <Тип фронтального алгоритма> <Количество потоков для фронтального алгоритма></p>
//            *             <p>               Следующие аргументы для OWN_ALPHA:<Название папки с данными производства и заказов> <Количество пар производство-заказы><p/>
//            *             <p> <Стартовое количество генераций альтернативностей> <Бюджет генератора альтернативностей> <имя файла результатов>.xml</p>
//            *             <p><количество запусков алгоритма> <количество потоков для вариатора> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма></p>
//            *             <p>               Следующие аргументы для OWN_BACKPACK: <Название папки с данными производства и заказов> <Количество пар производство-заказы></p>
//            *             <p> <Бюджет генератора альтернативностей> <Бюджет запусков пересчёта мощностей> <имя файла результатов>.xml</p>
//            *             <p> <количество запусков алгоритма> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма></p>
//            *             <p>    COMP_RESULT_TABLES - сравнить таблицы с результатами работы двух алгоритмов</p>
//            *             <p>        Следующие аргументы для COMP_RESULT_TABLES: <имя файла с таблицей результатов первого алгоритма>.csv <имя файла с таблицей результатов второго алгоритма>.csv <имя файла с результатами сравнения>.csv</p>

    private static void help() {
        System.out.println("1 аргумент - Тип работы: ALG / GEN / TEST / COMP_RESULT_TABLES");
        System.out.println("    Следующие аргументы для ALG: <тип алгоритма>(BASE / OWN_ALPHA / OWN_BACKPACK) <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml");
        System.out.println("        Следующие аргументы для BASE: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> <Тип фронтального алгоритма> <Количество потоков для фронтального алгоритма>");
        System.out.println("        Следующие аргументы для OWN_ALPHA: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> <Стартовое количество генераций альтернативностей> <Бюджет генератора альтернативностей> <количество потоков для вариатора> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма>");
        System.out.println("        Следующие аргументы для OWN_BACKPACK: <имя файла производства>.xml <имя файла заказов>.xml <имя выходного файла результатов>.xml <количество запусков алгоритма> <Бюджет генератора альтернативностей> <Бюджет запусков пересчёта мощностей> <количество потоков для вариатора> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма>");
        System.out.println("    Следующие аргументы для GEN: <имя файла параметров генератора>.json <количество экземпляров для генерации>");
        System.out.println("    Следующие аргументы для TEST: Тип теста: POSS / REAL / COMP / BASIS");
        System.out.println("        Следующие аргументы для POSS: <имя файла производства>.xml <имя файла заказов>.xml");
        System.out.println("        Следующие аргументы для REAL: <имя файла производства>.xml <имя файла заказов>.xml <имя файла результатов>.xml");
        System.out.println("        Следующие аргументы для COMP: <имя файла заказов>.xml <имя файла результатов первого>.xml <имя файла результатов второго>.xml");
        System.out.println("        Следующие аргументы для BASIS:  <тип алгоритма>(BASE / OWN_ALPHA / OWN_BACKPACK)");
        System.out.println("            Следующие аргументы для BASE:  <тип алгоритма>(BASE / OWN_ALPHA / OWN_BACKPACK)");
        System.out.println("            Следующие аргументы для OWN_ALPHA:  <Название папки с данными производства и заказов> <Количество пар производство-заказы><Бюджет генератора альтернативностей> <имя файла результатов>.xml <количество запусков алгоритма> <количество потоков для вариатора> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма>");
        System.out.println("            Следующие аргументы для OWN_BACKPACK:  <Название папки с данными производства и заказов> <Количество пар производство-заказы> <Бюджет генератора альтернативностей> <Бюджет запусков пересчёта мощностей> <имя файла результатов>.xml <количество запусков алгоритма> <тип фронтального алгоритма> <количество потоков для фронтального алгоритма>");
        System.out.println("    Следующие аргументы для COMP_RESULT_TABLES: <имя файла с таблицей результатов первого алгоритма>.csv <имя файла с таблицей результатов второго алгоритма>.csv <имя файла с результатами сравнения>.csv");
    }
}