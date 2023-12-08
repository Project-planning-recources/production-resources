package algorithm.alpha;

import algorithm.AbstractVariatorAlgorithm;
import algorithm.FrontAlgorithmFactory;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import util.Pair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class AlphaVariatorAlgorithm1Parallel extends AbstractVariatorAlgorithm {

    private String frontAlgorithmType;

    private int frontThreadsCount;
    private int threadsNum;

    private ArrayList<AlphaSolver1Parallel> solvers;
    private ArrayList<Thread> threads;

    private boolean startAlphaGeneration = false;

    public AlphaVariatorAlgorithm1Parallel(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, String frontAlgorithmType, int frontThreadsCount, int startVariatorCount, int variatorBudget, int threadsNum) {
        super(inputProduction, inputOrders, startTime, variatorBudget);
        this.frontAlgorithmType = frontAlgorithmType;
        this.frontThreadsCount = frontThreadsCount;
        this.startVariatorCount = startVariatorCount;
        this.threadsNum = threadsNum;
        Semaphore variationSemaphore = new Semaphore(1);
        Semaphore pairsSemaphore = new Semaphore(1);
        this.solvers = new ArrayList<>();
        this.threads = new ArrayList<>();

        int startGuaranteed = this.startVariatorCount / this.threadsNum;
        int startDistribute = this.startVariatorCount - startGuaranteed * this.threadsNum;
        int budgetGuaranteed = (this.variatorBudget - this.startVariatorCount) / this.threadsNum;
        int budgetDistribute = (this.variatorBudget - this.startVariatorCount) - budgetGuaranteed * this.threadsNum;


        for (int i = 1; i <= threadsNum; i++) {
            AlphaSolver1Parallel solver = new AlphaSolver1Parallel(inputProduction, inputOrders, startTime, this.frontAlgorithmType, this.frontThreadsCount,
                    startGuaranteed + (startDistribute-- > 0 ? 1 : 0),
                    budgetGuaranteed + (budgetDistribute-- > 0 ? 1 : 0),
                    this.variation, this.variantPairs, this, variationSemaphore, pairsSemaphore);
            this.solvers.add(solver);
            this.threads.add(new Thread(solver, "Solver " + i));
        }

    }

    public boolean isStartAlphaGeneration() {
        return startAlphaGeneration;
    }


    @Override
    public OutputResult start() throws Exception {

        this.threads.forEach(Thread::start);

        for (AlphaSolver1Parallel solver :
                this.solvers) {
            while(!solver.isStartGenerationFinished()) {
                Thread.sleep(100);
            }
        }

        startAlphaGeneration = true;

        for (AlphaSolver1Parallel solver :
                this.solvers) {
            while(!solver.isBudgetGenerationFinished()) {
                Thread.sleep(100);
            }
        }

        Pair<HashMap<Long, Integer>, Double> recordPair = null;
        double recordCriterion = Double.MAX_VALUE;


        for (Pair<HashMap<Long, Integer>, Double> variationPair : this.variation) {
            if (variationPair.getValue() < recordCriterion) {
                recordPair = variationPair;
                recordCriterion = variationPair.getValue();
            }
        }

        if (recordPair == null) {
            throw new Exception("Unreachable code");
        }

        return FrontAlgorithmFactory.getFrontAlgorithm(this.production, this.orders, this.startTime, recordPair.getKey(), this.frontAlgorithmType, this.frontThreadsCount).start();
    }
}
