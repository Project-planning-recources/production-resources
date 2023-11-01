package algorithm;

import algorithm.alternativeness.FromMapAlternativeElector;
import algorithm.model.order.Order;
import algorithm.model.order.Product;
import algorithm.model.order.TechProcess;
import algorithm.model.production.Production;
import algorithm.operationchooser.FirstElementChooser;
import algorithm.parallel.ParallelMain;
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

import java.util.concurrent.atomic.AtomicReference;

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


    private OutputResult startConsistentAlg() throws Exception {
        this.variation = new ArrayList<>();

        HashMap<Long, Integer> alt = new HashMap<>();


        this.orders.forEach(order -> {
            order.getProducts().forEach(product -> {
                product.getTechProcesses().forEach(techProcess -> {
                    alt.put(Hash.hashForTechProcess(order.getId(), product.getId(), techProcess.getId()), 1);
                });
            });
        });

//        System.out.println(alt.size());


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
        System.out.println(criterion);

        if(true) {
            return result;
        }

        while(this.variation.size() < this.variatorBudget) {
            generateAndAddNewVariants();
        }




        return null;
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
                        variant.put(Hash.hashForTechProcess(order.getId(), product.getId(), techProcesses.get(i)), remain);
                    } else {
                        int deal = Random.randomInt(remain + 1);
                        variant.put(Hash.hashForTechProcess(order.getId(), product.getId(), techProcesses.get(i)), deal);
                        remain -= deal;
                    }
                }
            });
        });

        return variant;
    }

    private HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> makeVariantIntegerAndPositive(HashMap<Long, HashMap<Long, HashMap<Long, Double>>> alphaVariant) {
        return null;
    }

    private HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> generateVariantFromTwo(HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> firstVariant,
                                                                                        HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> secondVariant,
                                                                                        double alpha) {
        HashMap<Long, HashMap<Long, HashMap<Long, Double>>> alphaVariant = null;

        return makeVariantIntegerAndPositive(alphaVariant);
    }

    private Boolean checkVariantAvailability(HashMap<Long, Integer> variant) {

        for (Order order : this.orders) {
            for (Product product : order.getProducts()) {
                int deal = product.getCount();
                for (TechProcess techProcess : product.getTechProcesses()) {
                    int value = variant.get(Hash.hashForTechProcess(order.getId(), product.getId(), techProcess.getId()));
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

    private void generateAndAddNewVariants() {

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
