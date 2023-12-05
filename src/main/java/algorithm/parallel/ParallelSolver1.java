package algorithm.parallel;

import algorithm.Algorithm;
import algorithm.AlphaAlgorithm;
import algorithm.OwnAlgorithm;
import algorithm.alternativeness.FromMapAlternativeElector;
import algorithm.operationchooser.FirstElementChooser;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import util.Criterion;
import util.Pair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class ParallelSolver1 extends AlphaAlgorithm implements Runnable {

    private ParallelAlphaAlgorithm1 main;
    private boolean startGenerationFinished = false;
    private boolean budgetGenerationFinished = false;
    private Semaphore variationSemaphore;

    private Semaphore pairsSemaphore;

    private int calculated = 0;


    public ParallelSolver1(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, int startVariatorCount, int variatorBudget,
                           ArrayList<Pair<HashMap<Long, Integer>, Double>> variation, HashMap<Long, Boolean> variantPairs,
                           ParallelAlphaAlgorithm1 main, Semaphore variationSemaphore, Semaphore pairsSemaphore) {
        super(inputProduction, inputOrders, startTime, startVariatorCount, variatorBudget);

        this.variation = variation;
        this.variantPairs = variantPairs;
        this.main = main;
        this.variationSemaphore = variationSemaphore;
        this.pairsSemaphore = pairsSemaphore;
    }


    protected void addCriterionForVariant(HashMap<Long, Integer> variant, double criterion) throws InterruptedException {
        variationSemaphore.acquire();
        for (Pair<HashMap<Long, Integer>, Double> pair :
                this.variation) {
            if(pair.getKey().equals(variant)) {
                pair.setValue(criterion);
            }
        }
        variationSemaphore.release();
    }

    @Override
    protected void loading() {
        System.out.println(Thread.currentThread().getName() + " | Вычисление " + (calculated) + "/" + this.variatorBudget);
    }

    @Override
    protected boolean putPairIfAbsent(long pairsHash) {
        try {
            pairsSemaphore.acquire();
            if (!this.variantPairs.containsKey(pairsHash)) {
                this.variantPairs.put(pairsHash, true);
                pairsSemaphore.release();
                return true;
            }
            pairsSemaphore.release();
            return false;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean addVariantIfAbsent(HashMap<Long, Integer> variant) throws Exception {
        if(checkNegativeAndDeal(variant)) {
//            System.out.println(Thread.currentThread().getName() + " запрашивает разрешение." + variation.size());
            variationSemaphore.acquire();
//            System.out.println(Thread.currentThread().getName() + " заблокировал ресурс." + variation.size());
            if(checkExistence(variant)) {
                this.variation.add(new Pair<>(variant, -1.0));
                variationSemaphore.release();
//                System.out.println(Thread.currentThread().getName() + " освободил ресурс." + variation.size());

                Algorithm algorithm = new OwnAlgorithm(this.production, this.orders, this.startTime, new FirstElementChooser(), new FromMapAlternativeElector(variant), variant);
                OutputResult result = algorithm.start();
                addCriterionForVariant(variant, Criterion.getCriterion(this.orders, result));
                return true;
            } else {
                variationSemaphore.release();
//                System.out.println(Thread.currentThread().getName() + " освободил ресурс. checkExistence" + variation.size());
            }
        } else {
//            System.out.println(Thread.currentThread().getName() + " checkNegativeAndDeal" + variation.size());
        }
        return false;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + "(Id " + Thread.currentThread().getId() + ") is running.");

        try {
            for (int i = 0; i < this.startVariatorCount; i++) {
                HashMap<Long, Integer> variant = generateRandomAlternativesDistribution();
                if(!addVariantIfAbsent(variant)) {
                    i--;
                }
            }
            this.startGenerationFinished = true;

            while(!main.isStartAlphaGeneration()) {
                Thread.sleep(100);
            }

            for (int i = 0; i < this.variatorBudget;) {
                loading();
                int addCount = generateAndAddNewVariants();
                i += addCount;
                calculated += addCount;
            }

            this.budgetGenerationFinished = true;


        } catch (Exception e) {
            throw new RuntimeException(e);
        }





        System.out.println(Thread.currentThread().getName() + "(Id " + Thread.currentThread().getId() + ") finished.");
    }

    public boolean isStartGenerationFinished() {
        return startGenerationFinished;
    }

    public boolean isBudgetGenerationFinished() {
        return budgetGenerationFinished;
    }

    //    private InputProduction inputProduction;
//    private ArrayList<InputOrder> inputOrders;
//    private LocalDateTime startTime;
//
//    private OwnAlgorithm algorithm;
//    private HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> alternativeness;
//    private ParallelMain main;
//
//    private boolean solving = false;
//
//    public ParallelSolver(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime) {
//        this.inputProduction = inputProduction;
//        this.inputOrders = inputOrders;
//        this.startTime = startTime;
//    }
//
//    public void setMain(ParallelMain main) {
//        this.main = main;
//    }
//
//    public boolean setData(HashMap<Long, Integer> variant) {
//        if (solving) {
//            return false;
//        } else {
//            this.alternativeness = alternativeness;
////            this.algorithm = new OwnAlgorithm(this.inputProduction, this.inputOrders, this.startTime, "FirstElement", variant);
//            return true;
//        }
//    }
//
//    @Override
//    public void run() {
//        try {
//            if(!this.solving && this.alternativeness != null && this.algorithm != null) {
//                System.out.println("SOLVER " + this.getId() + " IS RUNNING");
//                this.solving = true;
//                OutputResult result = this.algorithm.start();
//
//                main.addResult(this.alternativeness, result);
//                this.solving = false;
//                System.out.println("SOLVER " + this.getId() + " FINISHED");
//            }
//
//
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public boolean isSolving() {
//        return solving;
//    }
}
