package algorithm;

import algorithm.model.order.Order;
import algorithm.model.production.Production;
import algorithm.parallel.ParallelMain;
import algorithm.parallel.ParallelSolver;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
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

    public AlternativenessOwnAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime) {

        this.production = new Production(inputProduction);
        ArrayList<Order> orders = new ArrayList<>();
        inputOrders.forEach(inputOrder -> {
            orders.add(new Order(inputOrder));
        });
        this.orders = orders;

        this.startTime = startTime;
    }
    public AlternativenessOwnAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, int threadsNum) {
        this.production = new Production(inputProduction);
        ArrayList<Order> orders = new ArrayList<>();
        inputOrders.forEach(inputOrder -> {
            orders.add(new Order(inputOrder));
        });
        this.orders = orders;

        this.startTime = startTime;
        this.threadsNum = threadsNum;
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

    private Long hashForTechProcess(Long orderId, Long productId, Long techProcessId) {
        return orderId + 11 * productId + 31 * techProcessId;
    }
    private OutputResult startConsistentAlg() {
        this.variation = new ArrayList<>();

        HashMap<Long, Integer> alt = new HashMap<>();


        this.orders.forEach(order -> {
            order.getProducts().forEach(product -> {
                product.getTechProcesses().forEach(techProcess -> {
                    alt.put(hashForTechProcess(order.getId(), product.getId(), techProcess.getId()), 1);
                });
            });
        });

        System.out.println(alt.size());


        for (int i = 0; i < this.startVariatorCount; i++) {
            HashMap<Long, Integer> variant = generateRandomAlternativesDistribution();

            this.orders.forEach(order -> {
                order.getProducts().forEach(product -> {
                    product.getTechProcesses().forEach(techProcess -> {
                        System.out.println(order.getId() + " " + product.getId() + " " + techProcess.getId() + ": " + variant.get(hashForTechProcess(order.getId(), product.getId(), techProcess.getId())));
                    });
                });
            });


            if(checkVariantAvailability(variant)) {
                this.variation.add(variant);
            } else {
                i--;
            }
        }

        if(true) {
            return null;
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
                        variant.put(hashForTechProcess(order.getId(), product.getId(), techProcesses.get(i)), remain);
                    } else {
                        int deal = Random.randomInt(remain + 1);
                        variant.put(hashForTechProcess(order.getId(), product.getId(), techProcesses.get(i)), deal);
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
        return null;
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
