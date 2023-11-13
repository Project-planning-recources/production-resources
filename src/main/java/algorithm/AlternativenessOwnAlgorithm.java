package algorithm;

import algorithm.alternativeness.FromMapAlternativeElector;
import algorithm.model.order.Order;
import algorithm.model.order.Product;
import algorithm.model.order.TechProcess;
import algorithm.model.production.Production;
import algorithm.operationchooser.FirstElementChooser;
import algorithm.parallel.ParallelSolver;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import util.Criterion;
import util.Hash;
import util.Pair;
import util.Random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class AlternativenessOwnAlgorithm implements Algorithm {

    /**
     * Предприятие
     */
    protected Production production;

    /**
     * Все заказы
     */
    protected ArrayList<Order> orders;

    /**
     * Время начала работы
     */
    protected LocalDateTime startTime;

    private int threadsNum = 1;
    private int startVariatorCount = 10;
    private int variatorBudget = 100;

    public AlternativenessOwnAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, int startVariatorCount, int variatorBudget) {

        this.production = new Production(inputProduction);
        ArrayList<Order> orders = new ArrayList<>();
        inputOrders.forEach(inputOrder -> {
            orders.add(new Order(inputOrder));
        });
        this.orders = orders;

        this.startTime = startTime;
        this.startVariatorCount = startVariatorCount;
        this.variatorBudget = variatorBudget;

        this.variation = new ArrayList<>();
        this.variantPairs = new HashMap<>();
    }
    public AlternativenessOwnAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, int threadsNum, int startVariatorCount, int variatorBudget) {
        this.production = new Production(inputProduction);
        ArrayList<Order> orders = new ArrayList<>();
        inputOrders.forEach(inputOrder -> {
            orders.add(new Order(inputOrder));
        });
        this.orders = orders;

        this.startTime = startTime;
        this.threadsNum = threadsNum;
        this.startVariatorCount = startVariatorCount;
        this.variatorBudget = variatorBudget;

        this.variation = new ArrayList<>();
        this.variantPairs = new HashMap<>();
    }

    @Override
    public OutputResult start() throws Exception {
        if(this.threadsNum == 1) {
            return startConsistentAlg();
        } else {

            // todo: Написать тестер и адаптировать записываемую табличку в отдельном классе, запускать и собирать информацию
            return startParallelAlg();
        }
    }

    private ArrayList<Pair<HashMap<Long, Integer>, Double>> variation;

    private HashMap<Long, Boolean> variantPairs;

    private OutputResult startConsistentAlg() throws Exception {


        HashMap<Long, Integer> alt = new HashMap<>();


        this.orders.forEach(order -> {
            order.getProducts().forEach(product -> {
                product.getTechProcesses().forEach(techProcess -> {
                    alt.put(Hash.hash(order.getId(), product.getId(), techProcess.getId()), 1);
                });
            });
        });



        for (int i = 0; i < this.startVariatorCount; i++) {
            HashMap<Long, Integer> variant = generateRandomAlternativesDistribution();
            if(checkVariantAvailability(variant)) {

                Algorithm algorithm = new OwnAlgorithm(this.production, this.orders, this.startTime, new FirstElementChooser(), new FromMapAlternativeElector(variant), variant);
                OutputResult result = algorithm.start();

                this.variation.add(new Pair<>(variant, Criterion.getCriterion(this.orders, result)));
            } else {
                i--;
            }
        }

        while(this.variation.size() < this.variatorBudget) {
            generateAndAddNewVariants();

            System.out.println("variation " + this.variation.size());
        }

        Pair<HashMap<Long, Integer>, Double> recordPair = null;
        double recordCriterion = Double.MAX_VALUE;


        for (Pair<HashMap<Long, Integer>, Double> variationPair : this.variation) {
            if(variationPair.getValue() < recordCriterion) {
                recordPair = variationPair;
                recordCriterion = variationPair.getValue();
            }
            System.out.println(variationPair.getValue() + " " + recordCriterion);
        }

        if(recordPair == null) {
            throw new Exception("Unreachable code");
        }

        return new OwnAlgorithm(this.production, this.orders, this.startTime, new FirstElementChooser(), new FromMapAlternativeElector(recordPair.getKey()), recordPair.getKey()).start();
    }

    private double getCriterionForVariant(HashMap<Long, Integer> variant) throws Exception {
        Algorithm algorithm = new OwnAlgorithm(this.production, this.orders, this.startTime, new FirstElementChooser(), new FromMapAlternativeElector(variant), variant);
        OutputResult result = algorithm.start();

        return Criterion.getCriterion(this.orders, result);
    }

    private HashMap<Long, Integer> generateRandomAlternativesDistribution() {
        HashMap<Long, Integer> variant = new HashMap<>();

        this.orders.forEach(order -> {
            order.getProducts().forEach(product -> {
                ArrayList<Long> techProcesses = new ArrayList<>(product.getTechProcesses().size());
                product.getTechProcesses().forEach(techProcess -> techProcesses.add(techProcess.getId()));

                Collections.shuffle(techProcesses);

                int remain = product.getCount();
                for (int i = 0; i < techProcesses.size(); i++) {
                    if(i == techProcesses.size() - 1) {
                        variant.put(Hash.hash(order.getId(), product.getId(), techProcesses.get(i)), remain);
                    } else {
                        int deal = Random.randomInt(remain + 1);
                        variant.put(Hash.hash(order.getId(), product.getId(), techProcesses.get(i)), deal);
                        remain -= deal;
                    }
                }
            });
        });

        return variant;
    }

    private void generateAndAddNewVariants() throws Exception {

        boolean generationFlag = true;

        while(generationFlag) {
            HashMap<Long, Integer> firstVariant = null;
            HashMap<Long, Integer> secondVariant = null;

            long pairsHash = 0;
            double criterionForFirstVariant = 0;
            double criterionForSecondVariant = 0;

            boolean flag = true;
            while(flag) {

                Pair<HashMap<Long, Integer>, Double> firstPair = this.variation.get(Random.randomInt(this.variation.size()));
                firstVariant = firstPair.getKey();
                criterionForFirstVariant = firstPair.getValue();
                do {
                    Pair<HashMap<Long, Integer>, Double> secondPair = this.variation.get(Random.randomInt(this.variation.size()));
                    secondVariant = secondPair.getKey();
                    criterionForSecondVariant = secondPair.getValue();
                } while(firstVariant == secondVariant);

                pairsHash = Hash.hash((long) firstVariant.hashCode(), (long) secondVariant.hashCode());
//                System.out.println(firstVariant + " " + secondVariant + pairsHash);
                if(!this.variantPairs.containsKey(pairsHash)) {
                    flag = false;
                    this.variantPairs.put(pairsHash, true);
                }
            }

//        System.out.println(firstVariant);
//        System.out.println(secondVariant);



            HashMap<Long, Integer> betweenVariant = null;
            HashMap<Long, Integer> beyondVariant = null;

            if(criterionForFirstVariant < criterionForSecondVariant) {
//            System.out.println("1 < 2");
                betweenVariant = generateVariantFromTwo(firstVariant, secondVariant, 0.8);
                beyondVariant = generateVariantFromTwo(firstVariant, secondVariant, 1.2);
            } else if(criterionForFirstVariant > criterionForSecondVariant) {
//            System.out.println("1 > 2");
                betweenVariant = generateVariantFromTwo(firstVariant, secondVariant, 0.2);
                beyondVariant = generateVariantFromTwo(firstVariant, secondVariant, -0.2);
            } else {
                betweenVariant = generateVariantFromTwo(firstVariant, secondVariant, 0.5);
            }


            int addCount = 0;
            if(checkVariantAvailability(betweenVariant)) {
                double criterionForBetween = getCriterionForVariant(betweenVariant);
                this.variation.add(new Pair<>(betweenVariant, criterionForBetween));
                addCount++;
            }


            if(checkVariantAvailability(beyondVariant)) {
                double criterionForBeyond = getCriterionForVariant(beyondVariant);
                this.variation.add(new Pair<>(beyondVariant, criterionForBeyond));
                addCount++;
            }
            if(addCount != 0) {
                generationFlag = false;
            }
        }
    }



    private HashMap<Long, Integer> generateVariantFromTwo(HashMap<Long, Integer> firstVariant,
                                                          HashMap<Long, Integer> secondVariant,
                                                          double alpha) {

        HashMap<Long, Double> alphaVariant = new HashMap<>();
        for (Order order : this.orders) {
            for (Product product : order.getProducts()) {
                double negativeBudget = 0;
                ArrayList<Pair<Long, Double>> positiveValues = new ArrayList<>();

                for (TechProcess techProcess : product.getTechProcesses()) {
                    long hash = Hash.hash(order.getId(), product.getId(), techProcess.getId());

                    double value = firstVariant.get(hash) * alpha + secondVariant.get(hash) * (1-alpha);
                    if(value < 0) {
                        alphaVariant.put(hash, 0.0);
                        negativeBudget -= value;
                    } else {
                        positiveValues.add(new Pair(hash, value));
                    }

                }

                positiveValues.sort((first, second) -> (int) (first.getValue() - second.getValue()));

                int count = positiveValues.size();
                double avg = negativeBudget / count;
                int budget = product.getCount();

                for (Pair<Long, Double> p :
                        positiveValues) {
                    if(p.getValue() < avg) {
                        negativeBudget -= p.getValue();
                        count--;
                        avg = negativeBudget / count;
                        p.setValue(0.0);
                    } else {
                        p.setValue(p.getValue() - avg);;
                        int round = (int) Math.round(p.getValue());
                        budget -= round;
                        negativeBudget -= avg;
                    }
                }

                if(budget == 1) {
                    positiveValues.get(positiveValues.size()-1).setValue(positiveValues.get(positiveValues.size()-1).getValue() + 1);
                } else if(budget == -1) {
                    positiveValues.get(positiveValues.size()-1).setValue(positiveValues.get(positiveValues.size()-1).getValue() - 1);
                }

//                System.out.println("afterpairs " + positiveValues + " " + negativeBudget + " " + product.getCount());

                for (Pair<Long, Double> pair : positiveValues) {
                    int round = (int) Math.round(pair.getValue());
                    alphaVariant.put(pair.getKey(), (double) round);
                }

            }
        }

        HashMap<Long, Integer> readyAlphaVariant = new HashMap<>();
        alphaVariant.forEach((k, v) -> readyAlphaVariant.put(k, v.intValue()));
        return readyAlphaVariant;
    }

    private Boolean checkVariantAvailability(HashMap<Long, Integer> variant) {

        for (Order order : this.orders) {
            for (Product product : order.getProducts()) {
                int deal = product.getCount();
                for (TechProcess techProcess : product.getTechProcesses()) {
                    System.out.println(order.getId() + " " + product.getId() + " " + techProcess.getId() + " " + variant);
                    int value = variant.get(Hash.hash(order.getId(), product.getId(), techProcess.getId()));
                    if(value < 0) {
                        System.out.println("||||||||||||Значение < 0   " + value);
                        return false;
                    }
                    deal -= value;
                }
                if (deal != 0) {
                    System.out.println("||||||||||||Несоответствие количества деталей   " + deal);
                    return false;
                }
            }
        }

        for (Pair<HashMap<Long, Integer>, Double> v : this.variation) {
            if(v.getKey().equals(variant)) {
                System.out.println("||||||||||||Такой уже есть");
                return false;
            }
        }

        return true;
    }




    private OutputResult startParallelAlg() {
        ArrayList<ParallelSolver> solvers = new ArrayList<>();
//        solvers.add(new ParallelSolver(this.inputProduction, this.inputOrders, this.startTime));
//        solvers.add(new ParallelSolver(this.inputProduction, this.inputOrders, this.startTime));
//        solvers.add(new ParallelSolver(this.inputProduction, this.inputOrders, this.startTime));
//        solvers.add(new ParallelSolver(this.inputProduction, this.inputOrders, this.startTime));
//
//        ParallelMain parallelMain = new ParallelMain(this.inputProduction, this.inputOrders, this.startTime, solvers);
//        parallelMain.start();

        return null;
    }
}
