package algorithm.alpha;

import algorithm.AbstractVariatorAlgorithm;
import algorithm.FrontAlgorithmFactory;
import algorithm.model.order.Order;
import algorithm.model.production.Production;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import util.Pair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class AlphaVariatorAlgorithmParallel extends AbstractVariatorAlgorithm {

    protected InputProduction inputProduction;
    protected ArrayList<InputOrder> inputOrders;

    protected String frontAlgorithmType;

    protected int frontThreadsCount;
    protected int threadsNum;

    protected ArrayList<AlphaSolverParallel> solvers;
    protected ArrayList<Thread> threads;

    protected boolean startAlphaGeneration = false;

    public AlphaVariatorAlgorithmParallel(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, String frontAlgorithmType, int frontThreadsCount, int startVariatorCount, int variatorBudget, int threadsNum) {
        super(inputProduction, inputOrders, startTime, variatorBudget);
        this.inputProduction = inputProduction;
        this.inputOrders = inputOrders;
        this.frontAlgorithmType = frontAlgorithmType;
        this.frontThreadsCount = frontThreadsCount;
        this.startVariatorCount = startVariatorCount;
        this.threadsNum = threadsNum;
        this.solvers = new ArrayList<>();
        this.threads = new ArrayList<>();
    }

    protected void initThreads() {
        int startGuaranteed = this.startVariatorCount / this.threadsNum;
        int startDistribute = this.startVariatorCount - startGuaranteed * this.threadsNum;
        int budgetGuaranteed = (this.variatorBudget - this.startVariatorCount) / this.threadsNum;
        int budgetDistribute = (this.variatorBudget - this.startVariatorCount) - budgetGuaranteed * this.threadsNum;

        Semaphore variationSemaphore = new Semaphore(1);
        Semaphore pairsSemaphore = new Semaphore(1);

        for (int i = 1; i <= threadsNum; i++) {
            AlphaSolverParallel solver = new AlphaSolverParallel(this.inputProduction, this.inputOrders, startTime, this.frontAlgorithmType, this.frontThreadsCount,
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

    protected void setStartAlphaGeneration() {
        this.startAlphaGeneration = true;
    }

    @Override
    public OutputResult start() throws Exception {
        initThreads();
        this.threads.forEach(Thread::start);
        for (AlphaSolverParallel solver :
                this.solvers) {
            while(!solver.isStartGenerationFinished()) {
                Thread.sleep(100);
            }
        }
        setStartAlphaGeneration();
        for (AlphaSolverParallel solver :
                this.solvers) {
            while(!solver.isBudgetGenerationFinished()) {
                Thread.sleep(100);
            }
        }

        Pair<HashMap<Long, Integer>, Double> recordPair = returnRecordVariantPair(this.variation);
        return FrontAlgorithmFactory.getOwnFrontAlgorithm(this.production, this.orders, this.startTime, recordPair.getKey(), this.frontAlgorithmType, this.frontThreadsCount).start();
    }
}
