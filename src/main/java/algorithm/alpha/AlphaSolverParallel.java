package algorithm.alpha;

import algorithm.Algorithm;
import algorithm.FrontAlgorithmFactory;
import algorithm.model.order.Order;
import algorithm.model.production.Production;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import util.Criterion;
import util.Pair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class AlphaSolverParallel extends AlphaVariatorAlgorithm implements Runnable {

    protected AlphaVariatorAlgorithmParallel main;
    protected boolean startGenerationFinished = false;
    protected boolean budgetGenerationFinished = false;

    protected Semaphore variationSemaphore;
    protected Semaphore pairsSemaphore;

    protected int calculated = 0;

    protected String frontAlgorithmType;

    protected int frontThreadsCount;


    public AlphaSolverParallel(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, String frontAlgorithmType, int frontThreadsCount, int startVariatorCount, int variatorBudget,
                               ArrayList<Pair<HashMap<Long, Integer>, Double>> variation, HashMap<Long, Boolean> variantPairs,
                               AlphaVariatorAlgorithmParallel main, Semaphore variationSemaphore, Semaphore pairsSemaphore) {
        super(inputProduction, inputOrders, startTime, frontAlgorithmType, frontThreadsCount, startVariatorCount, variatorBudget);

        this.variation = variation;
        this.variantPairs = variantPairs;
        this.main = main;
        this.variationSemaphore = variationSemaphore;
        this.pairsSemaphore = pairsSemaphore;
        this.frontAlgorithmType = frontAlgorithmType;
        this.frontThreadsCount = frontThreadsCount;
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

    protected Algorithm getAlgorithm(HashMap<Long, Integer> variant) {
        return FrontAlgorithmFactory.getOwnFrontAlgorithm(this.production, this.orders, this.startTime, variant, this.frontAlgorithmType, this.frontThreadsCount);
    }

    @Override
    protected boolean addVariantIfAbsent(HashMap<Long, Integer> variant) throws Exception {
        if(variant != null && checkNegativeAndDeal(variant)) {
            variationSemaphore.acquire();
            if(checkExistence(variant)) {
                this.variation.add(new Pair<>(variant, -1.0));
                variationSemaphore.release();


                Algorithm algorithm = getAlgorithm(variant);
                OutputResult result = algorithm.start();
                addCriterionForVariant(variant, Criterion.getCriterion(this.orders, result));
                return true;
            } else {
                variationSemaphore.release();
            }
        }
        return false;
    }

    @Override
    protected void generateAlphaVariants() throws Exception {
        for (int i = 0; i < this.variatorBudget;) {
            loading();
            int addCount = generateAndAddNewVariants();
            i += addCount;
            calculated += addCount;
        }
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

            try {
                generateAlphaVariants();
            } catch (InabilityGenerateException ignored) {
                System.out.println("Закончились варианты перебираемых пар");
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
}
