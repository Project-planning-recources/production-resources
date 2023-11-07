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
import util.Random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
        this.variantPairsCriterion = new HashMap<>();
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
        this.variantPairsCriterion = new HashMap<>();
    }

    @Override
    public OutputResult start() throws Exception {
        if(this.threadsNum == 1) {
            return startConsistentAlg();
        } else {
            return startParallelAlg();
        }
    }

    private ArrayList<HashMap<Long, Integer>> variation;

    private HashMap<Long, Double> variantPairsCriterion;

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
                this.variation.add(variant);
            } else {
                i--;
            }
        }

        Algorithm algorithm = new OwnAlgorithm(this.production, this.orders, this.startTime, new FirstElementChooser(), new FromMapAlternativeElector(this.variation.get(0)), this.variation.get(0));
        OutputResult result = algorithm.start();

        double criterion = Criterion.getCriterion(this.orders, result);



        while(this.variation.size() < this.variatorBudget) {


            generateAndAddNewVariant();

            if(true) {
                return result;
            }
        }




        return null;
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

    private void generateAndAddNewVariant() throws Exception {


        HashMap<Long, Integer> firstVariant = null;
        HashMap<Long, Integer> secondVariant = null;

        HashMap<Long, Double> alphaVariant = new HashMap<>();

        boolean flag = true;
        while(flag) {
            firstVariant = this.variation.get(Random.randomInt(this.variation.size()));
            do {
                secondVariant = this.variation.get(Random.randomInt(this.variation.size()));
            } while(firstVariant == secondVariant);


            if(!this.variantPairsCriterion.containsKey(
                    Hash.hash((long) this.variation.indexOf(firstVariant),
                            (long) this.variation.indexOf(secondVariant)))) {
                flag = false;
            }
        }

        System.out.println(firstVariant);
        System.out.println(secondVariant);

        double criterionForFirstVariant = getCriterionForVariant(firstVariant);
        double criterionForSecondVariant = getCriterionForVariant(secondVariant);

        HashMap<Long, Integer> betweenVariant = null;
        HashMap<Long, Integer> beyondVariant = null;

        if(criterionForFirstVariant < criterionForSecondVariant) {
            System.out.println("1 < 2");
            betweenVariant = generateVariantFromTwo(firstVariant, secondVariant, 10.8);
            beyondVariant = generateVariantFromTwo(firstVariant, secondVariant, 11.2);
        } else if(criterionForFirstVariant > criterionForSecondVariant) {
            System.out.println("1 > 2");
            betweenVariant = generateVariantFromTwo(firstVariant, secondVariant, 10.2);
            beyondVariant = generateVariantFromTwo(firstVariant, secondVariant, -10.2);
        } else {
            betweenVariant = generateVariantFromTwo(firstVariant, secondVariant, 0.5);
        }

        System.out.println(betweenVariant);
        System.out.println(beyondVariant);


    }

    private HashMap<Long, Integer> generateVariantFromTwo(HashMap<Long, Integer> firstVariant,
                                                          HashMap<Long, Integer> secondVariant,
                                                          double alpha) {
        HashMap<Long, Double> alphaVariant = new HashMap<>();
        for (Order order : this.orders) {
            for (Product product : order.getProducts()) {
                for (TechProcess techProcess : product.getTechProcesses()) {
                    long hash = Hash.hash(order.getId(), product.getId(), techProcess.getId());

                    alphaVariant.put(hash, firstVariant.get(hash) * alpha + secondVariant.get(hash) * (1-alpha));
                }
            }
        }
        System.out.println(alphaVariant);
        return makeVariantIntegerAndPositive(alphaVariant);
    }

    private HashMap<Long, Integer> makeVariantIntegerAndPositive(HashMap<Long, Double> alphaVariant) {
        HashMap<Long, Integer> integerAndPositiveAlphaVariant = new HashMap<>();
        System.out.println("negative double: " + alphaVariant);
        for (Order order : this.orders) {
            for (Product product : order.getProducts()) {
                ArrayList<TechProcess> techProcesses = product.getTechProcesses();
                for (int i = 0; i < techProcesses.size(); i++) {
                    long hash = Hash.hash(order.getId(), product.getId(), techProcesses.get(i).getId());

                    double value = alphaVariant.get(hash);
                    if (value < 0) {
                        alphaVariant.replace(hash, (double) 0);
                        for (int j = 0; j < techProcesses.size(); j++) {
                            if(i != j) {
                                long innerHash = Hash.hash(order.getId(), product.getId(), techProcesses.get(j).getId());

                                alphaVariant.replace(innerHash, alphaVariant.get(innerHash) + value / (techProcesses.size() - 1));
                            }
                        }
                    }
                }
            }
        }
        System.out.println("positive double: " + alphaVariant);


        for (Order order : this.orders) {
            for (Product product : order.getProducts()) {
                for (TechProcess techProcess : product.getTechProcesses()) {
                    long hash = Hash.hash(order.getId(), product.getId(), techProcess.getId());

                    integerAndPositiveAlphaVariant.put(hash, alphaVariant.get(hash).intValue());
                }
            }
        }

        System.out.println("positive integer: " + alphaVariant);
        return integerAndPositiveAlphaVariant;
    }



    private Boolean checkVariantAvailability(HashMap<Long, Integer> variant) {

        for (Order order : this.orders) {
            for (Product product : order.getProducts()) {
                int deal = product.getCount();
                for (TechProcess techProcess : product.getTechProcesses()) {
                    int value = variant.get(Hash.hash(order.getId(), product.getId(), techProcess.getId()));
                    if(value < 0) {
                        return false;
                    }
                    deal -= value;
                }
                if (deal != 0) {
                    return false;
                }
            }
        }

        for (HashMap<Long, Integer> v : this.variation) {
            if(v.equals(variant)) {
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
