package algorithm.parallel;

import algorithm.AbstractAlphaAlgorithm;
import algorithm.OwnAlgorithm;
import algorithm.alternativeness.FromMapAlternativeElector;
import algorithm.model.order.Order;
import algorithm.model.production.Production;
import algorithm.operationchooser.FirstElementChooser;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import util.Pair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class ParallelAlphaAlgorithm1 extends AbstractAlphaAlgorithm {

    private int threadsNum;

    private ArrayList<ParallelSolver1> solvers;
    private ArrayList<Thread> threads;

    private boolean startAlphaGeneration = false;

    public ParallelAlphaAlgorithm1(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, int startVariatorCount, int variatorBudget, int threadsNum) {

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
            ParallelSolver1 solver = new ParallelSolver1(inputProduction, inputOrders, startTime,
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

        for (ParallelSolver1 solver :
                this.solvers) {
            while(!solver.isStartGenerationFinished()) {
                Thread.sleep(100);
            }
        }

        startAlphaGeneration = true;

        for (ParallelSolver1 solver :
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

        return new OwnAlgorithm(this.production, this.orders, this.startTime, new FirstElementChooser(), new FromMapAlternativeElector(recordPair.getKey()), recordPair.getKey()).start();
    }
}
